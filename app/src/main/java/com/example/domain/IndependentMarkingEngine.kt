package com.example.domain

import kotlin.math.abs
import kotlin.math.round

/**
 * Conservative question-specific rubric aid.
 * Each marking point is scored separately using evidence from that question's
 * own marking scheme. It does not award generic marks for equals signs or
 * generic "given/formula/working" keywords. Ambiguous rubric items are flagged.
 * This is a practice aid, not an official KSEAB evaluation.
 */
object IndependentMarkingEngine {
    private data class RubricItem(val text: String, val marks: Double)

    fun evaluateWrittenSolution(
        questionEn: String,
        officialAnswer: String,
        markingPoints: List<String>,
        maxMarks: Int,
        studentSteps: String,
        conceptTag: String? = null
    ): ExaminerEvaluationReport {
        val total = maxMarks.coerceAtLeast(0).toDouble()
        val student = studentSteps.trim()
        if (student.isBlank()) {
            return ExaminerEvaluationReport(
                0.0, total,
                listOf(AnswerStepCheck("No answer submitted", total, 0.0, "Write your solution to receive rubric feedback.")),
                listOf("No student working submitted."),
                conceptTag,
                "No marks awarded because no answer was submitted."
            )
        }
        if (markingPoints.isEmpty() || total <= 0.0) {
            return ExaminerEvaluationReport(
                0.0, total,
                listOf(AnswerStepCheck("Manual review required", total, 0.0, "No usable question-specific marking rubric is available.")),
                listOf("Question-specific marking rubric missing."),
                conceptTag,
                "Automatic marking is unavailable for this question. Teacher review is required."
            )
        }

        val parsed = markingPoints.map { point ->
            val weight = Regex("""\(?\s*(\d+(?:\.\d+)?)\s*M\s*\)?""", RegexOption.IGNORE_CASE)
                .find(point)?.groupValues?.getOrNull(1)?.toDoubleOrNull()
            point to weight
        }
        val explicitWeights = parsed.all { it.second != null } && parsed.sumOf { it.second ?: 0.0 } > 0.0
        val rawWeights = if (explicitWeights) parsed.map { it.second!! } else List(parsed.size) { 1.0 }
        val weightSum = rawWeights.sum().takeIf { it > 0.0 } ?: 1.0
        val rubric = parsed.mapIndexed { index, (point, _) ->
            RubricItem(point, total * rawWeights[index] / weightSum)
        }

        val studentNumbers = extractNumbers(student)
        val finalLine = student.lineSequence().lastOrNull { it.isNotBlank() }.orEmpty()
        val checks = mutableListOf<AnswerStepCheck>()
        val missing = mutableListOf<String>()
        var awarded = 0.0
        var manualReviewItems = 0

        rubric.forEachIndexed { index, item ->
            val clean = item.text
                .replace(Regex("""\(?\s*\d+(?:\.\d+)?\s*M\s*\)?""", RegexOption.IGNORE_CASE), "")
                .replace(Regex("""[^\p{L}\p{N}./+−-]+"""), " ")
                .trim()
            val expectedNumbers = extractNumbers(clean)
            val evidenceText = if (clean.contains("final answer", ignoreCase = true)) finalLine else student
            val evidenceNumbers = if (clean.contains("final answer", ignoreCase = true)) extractNumbers(finalLine) else studentNumbers
            val numericEvidence = expectedNumbers.isNotEmpty() &&
                expectedNumbers.all { expected -> evidenceNumbers.any { abs(it - expected) < 0.0001 } }
            val anchors = clean.lowercase()
                .split(Regex("""[^a-z0-9]+"""))
                .filter { it.length >= 4 && it !in STOP_WORDS }
                .distinct()
            val textualEvidence = anchors.size >= 2 &&
                anchors.count { evidenceText.contains(it, ignoreCase = true) } >=
                (anchors.size * 0.7).toInt().coerceAtLeast(2)
            val verifiable = expectedNumbers.isNotEmpty() || anchors.size >= 2
            val satisfied = verifiable && (numericEvidence || textualEvidence)
            if (!verifiable) manualReviewItems++
            val score = if (satisfied) item.marks else 0.0
            awarded += score
            checks += AnswerStepCheck(
                "Rubric ${index + 1}",
                roundHalf(item.marks),
                roundHalf(score),
                when {
                    satisfied -> "Evidence for this question-specific marking point was found."
                    !verifiable -> "This marking point cannot be reliably verified automatically; teacher review required."
                    else -> "Required evidence for this marking point was not found."
                }
            )
            if (!satisfied) missing += item.text
        }

        val expectedFinal = extractNumbers(officialAnswer)
        val finalLine = student.lines().lastOrNull { it.isNotBlank() }.orEmpty()
        val finalMatch = if (expectedFinal.isNotEmpty()) {
            MathVerificationEngine.verifyNumericAnswer(finalLine, expectedFinal).matched
        } else null
        val expectedUnit = extractExpectedUnit(officialAnswer)
        val hasUnit = expectedUnit == null || finalLine.contains(expectedUnit, ignoreCase = true)
        if (finalMatch == false) {
            missing += "Final answer does not match numeric answer key: $officialAnswer"
            val finalRubricIndex = rubric.indexOfLast {
                val label = it.text.lowercase()
                label.contains("final answer") || label.contains("final result") || label.contains("conclusion")
            }
            if (finalRubricIndex >= 0) awarded = (awarded - rubric[finalRubricIndex].marks).coerceAtLeast(0.0)
        }
        if (finalMatch == true && !hasUnit) missing += "Final answer unit '$expectedUnit' is missing."
        checks += AnswerStepCheck(
            "Final answer cross-check",
            0.0,
            0.0,
            when {
                finalMatch == null -> "Final answer requires manual comparison with the answer key."
                !finalMatch -> "Numeric final-answer values do not match the answer key."
                !hasUnit -> "Numeric values match, but the expected unit '$expectedUnit' is missing."
                else -> "Numeric values match. Mathematical equivalence and unit correctness still require checking."
            }
        )

        val finalAward = roundHalf(awarded.coerceIn(0.0, total))
        val summary = buildString {
            append("Provisional rubric score: ${MathVerificationEngine.fmt(finalAward)} / ${MathVerificationEngine.fmt(total)}. ")
            if (manualReviewItems > 0) append("$manualReviewItems rubric item(s) require teacher review. ")
            append("This is an automated practice aid, not an official KSEAB evaluation.")
        }
        return ExaminerEvaluationReport(
            finalAward, total, checks, missing,
            if (finalAward < total * 0.75) conceptTag else null,
            summary
        )
    }

    private fun extractNumbers(text: String): List<Double> =
        Regex("""-?\d+(?:\.\d+)?(?:/\d+(?:\.\d+)?)?""")
            .findAll(text.replace(",", " "))
            .mapNotNull { match ->
                val parts = match.value.split("/")
                val numerator = parts[0].toDoubleOrNull() ?: return@mapNotNull null
                if (parts.size == 1) numerator else {
                    val denominator = parts[1].toDoubleOrNull() ?: return@mapNotNull null
                    if (abs(denominator) < 1e-9) null else numerator / denominator
                }
            }.toList()

    private fun extractExpectedUnit(answer: String): String? {
        val lower = answer.lowercase()
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

    private fun roundHalf(value: Double): Double = round(value * 2.0) / 2.0
    private val STOP_WORDS = setOf("that", "this", "with", "from", "then", "when", "find", "using", "given", "show", "prove", "hence", "equal", "each", "same", "step")
}
