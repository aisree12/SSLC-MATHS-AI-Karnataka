package com.example.domain

import kotlin.math.abs

/**
 * Independent deterministic KSEAB Marking Engine.
 * Evaluates student step-by-step solutions strictly against official marking pillars
 * without relying on an LLM for numeric grading.
 */
object IndependentMarkingEngine {

    fun evaluateWrittenSolution(
        questionEn: String,
        officialAnswer: String,
        markingPoints: List<String>,
        maxMarks: Int,
        studentSteps: String,
        conceptTag: String? = null
    ): ExaminerEvaluationReport {
        val trimmed = studentSteps.trim()
        if (trimmed.isBlank()) {
            return ExaminerEvaluationReport(
                marksAwarded = 0.0,
                maxMarks = maxMarks.toDouble(),
                stepChecks = listOf(
                    AnswerStepCheck("Submission", maxMarks.toDouble(), 0.0, "No steps written yet.")
                ),
                missingSteps = listOf("Write Given, Formula, Substitution, Steps, and Final Answer."),
                detectedWeakConcept = conceptTag,
                summaryFeedback = "Start by writing the 'Given' data and the exact formula for partial marks."
            )
        }

        val lower = trimmed.lowercase()
        val lines = trimmed.lines().map { it.trim() }.filter { it.isNotEmpty() }

        // Pillar weights scaled cleanly to maxMarks
        val hasGiven = lower.contains("given") || lower.contains("let ") ||
            lines.any { it.contains("=") } || lower.contains("a =") || lower.contains("r =")
        val hasFormula = lower.contains("formula") || lower.contains("=") ||
            lower.contains("by ") || lower.contains("theorem") ||
            lower.contains("sin") || lower.contains("cos") || lower.contains("tan") ||
            lower.contains("π") || lower.contains("pi") || lower.contains("√") ||
            lower.contains("Δ") || lower.contains("b²") || lower.contains("an =") ||
            lower.contains("sn =")
        val hasWorking = lines.size >= 2 || trimmed.length >= 28

        val expectedNums = extractNumbers(officialAnswer)
        val studentNums = extractNumbers(trimmed)
        val finalAnswerMatched = if (expectedNums.isNotEmpty()) {
            expectedNums.all { exp -> studentNums.any { abs(it - exp) <= 0.1 } }
        } else {
            val keyTokens = officialAnswer.lowercase()
                .split(Regex("[^a-z0-9√π]+"))
                .filter { it.length >= 2 }
            keyTokens.isNotEmpty() && keyTokens.count { lower.contains(it) } >= (keyTokens.size / 2).coerceAtLeast(1)
        }

        val expectedUnits = extractExpectedUnit(officialAnswer)
        val hasUnits = expectedUnits == null || lower.contains(expectedUnits.lowercase())

        // Allocate marks proportionally across KSEAB pillars
        val totalM = maxMarks.coerceAtLeast(1).toDouble()
        val wGiven = (totalM * 0.2).coerceAtLeast(0.25)
        val wFormula = (totalM * 0.25).coerceAtLeast(0.25)
        val wWorking = (totalM * 0.25).coerceAtLeast(0.25)
        val wAnswer = totalM - (wGiven + wFormula + wWorking)

        val checks = mutableListOf<AnswerStepCheck>()
        val missing = mutableListOf<String>()

        val gScore = if (hasGiven) wGiven else 0.0
        checks += AnswerStepCheck(
            "1. Given Data / Setup",
            roundHalf(wGiven),
            roundHalf(gScore),
            if (hasGiven) "Given values identified cleanly." else "State given values explicitly (e.g., 'Given: r = ..., h = ...')."
        )
        if (!hasGiven) missing += "Explicit 'Given' values (-${MathVerificationEngine.fmt(roundHalf(wGiven))}M)"

        val fScore = if (hasFormula) wFormula else 0.0
        checks += AnswerStepCheck(
            "2. Formula / Theorem Statement",
            roundHalf(wFormula),
            roundHalf(fScore),
            if (hasFormula) "Formula / relation present." else "Name the exact formula or theorem before substituting."
        )
        if (!hasFormula) missing += "Formula / Theorem statement (-${MathVerificationEngine.fmt(roundHalf(wFormula))}M)"

        val wScore = if (hasWorking) wWorking else 0.0
        checks += AnswerStepCheck(
            "3. Intermediate Substitution & Steps",
            roundHalf(wWorking),
            roundHalf(wScore),
            if (hasWorking) "Step-by-step substitution shown." else "Show intermediate algebraic steps — never jump straight to an answer."
        )
        if (!hasWorking) missing += "Intermediate calculation steps (-${MathVerificationEngine.fmt(roundHalf(wWorking))}M)"

        val aScore = when {
            finalAnswerMatched && hasUnits -> wAnswer
            finalAnswerMatched && !hasUnits -> (wAnswer - 0.5).coerceAtLeast(0.0)
            else -> 0.0
        }
        val ansFeedback = when {
            finalAnswerMatched && hasUnits -> "Matches official answer ($officialAnswer) with proper units."
            finalAnswerMatched && !hasUnits -> "Numeric value matches, but missing unit '$expectedUnits' (-0.5M board penalty)."
            else -> {
                val signFlip = expectedNums.isNotEmpty() && expectedNums.any { exp -> studentNums.any { abs(it + exp) <= 0.1 } }
                if (signFlip) "Sign error detected (+/- flip). Official answer: $officialAnswer."
                else "Final answer does not match official key ($officialAnswer)."
            }
        }
        checks += AnswerStepCheck("4. Final Answer & Units", roundHalf(wAnswer), roundHalf(aScore), ansFeedback)
        if (!finalAnswerMatched) missing += "Incorrect final answer (Expected: $officialAnswer)"
        else if (!hasUnits) missing += "Missing final unit '$expectedUnits' (-0.5M)"

        val awarded = roundHalf((gScore + fScore + wScore + aScore).coerceIn(0.0, totalM))
        val weakConcept = if (awarded < totalM * 0.75) conceptTag else null

        val summary = buildString {
            append("Examiner Score: ${MathVerificationEngine.fmt(awarded)} / ${MathVerificationEngine.fmt(totalM)} Marks. ")
            if (missing.isEmpty()) {
                append("Full-mark KSEAB presentation!")
            } else {
                append("Marks lost due to: ${missing.joinToString("; ")}.")
            }
        }

        return ExaminerEvaluationReport(
            marksAwarded = awarded,
            maxMarks = totalM,
            stepChecks = checks,
            missingSteps = missing,
            detectedWeakConcept = weakConcept,
            summaryFeedback = summary
        )
    }

    private fun extractNumbers(text: String): List<Double> {
        val cleaned = text.replace("=", " ").replace(",", " ")
        return Regex("-?\\d+(?:\\.\\d+)?(?:/\\d+(?:\\.\\d+)?)?")
            .findAll(cleaned)
            .mapNotNull { match ->
                val raw = match.value
                if (raw.contains("/")) {
                    val parts = raw.split("/")
                    val n = parts.getOrNull(0)?.toDoubleOrNull()
                    val d = parts.getOrNull(1)?.toDoubleOrNull()
                    if (n != null && d != null && abs(d) > 1e-9) n / d else null
                } else {
                    raw.toDoubleOrNull()
                }
            }.toList()
    }

    private fun extractExpectedUnit(officialAnswer: String): String? {
        val lower = officialAnswer.lowercase()
        return when {
            lower.contains("cm³") || lower.contains("cm^3") -> "cm³"
            lower.contains("cm²") || lower.contains("cm^2") -> "cm²"
            lower.contains("mm²") -> "mm²"
            lower.contains("mm³") -> "mm³"
            lower.contains("units") -> "units"
            lower.contains("cm") -> "cm"
            lower.contains(" m") -> "m"
            else -> null
        }
    }

    private fun roundHalf(v: Double): Double = kotlin.math.round(v * 2.0) / 2.0
}
