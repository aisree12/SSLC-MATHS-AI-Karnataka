package com.example.domain

import java.util.Locale
import kotlin.math.abs
import kotlin.math.sqrt

data class MathEngineSolution(
    val topic: String,
    val summary: String,
    val steps: List<String>,
    val finalAnswer: String,
    val numericResults: List<Double> = emptyList()
)

data class AnswerVerificationResult(
    val matched: Boolean,
    val expectedDisplay: String,
    val feedback: String
)

/**
 * Independent deterministic Mathematics Engine for Karnataka SSLC Class 10.
 * Never delegates arithmetic or algebraic roots to an LLM.
 */
object MathVerificationEngine {

    fun solveQuadratic(a: Double, b: Double, c: Double): MathEngineSolution {
        if (abs(a) < 1e-9) {
            if (abs(b) < 1e-9) {
                return MathEngineSolution(
                    topic = "Quadratic Equation",
                    summary = "Invalid equation: both a and b cannot be 0.",
                    steps = listOf("For ax² + bx + c = 0 to be quadratic, coefficient a ≠ 0."),
                    finalAnswer = "Not a valid equation"
                )
            }
            val root = -c / b
            return MathEngineSolution(
                topic = "Linear Degenerate Case",
                summary = "Since a = 0, this reduces to a linear equation.",
                steps = listOf(
                    "Given: ${fmt(b)}x + (${fmt(c)}) = 0",
                    "x = -(${fmt(c)}) / ${fmt(b)} = ${fmt(root)}"
                ),
                finalAnswer = "x = ${fmt(root)}",
                numericResults = listOf(root)
            )
        }

        val discriminant = b * b - 4.0 * a * c
        val steps = mutableListOf(
            "1. Given equation: ${fmt(a)}x² + (${fmt(b)})x + (${fmt(c)}) = 0",
            "2. Compare with ax² + bx + c = 0: a = ${fmt(a)}, b = ${fmt(b)}, c = ${fmt(c)}",
            "3. Discriminant Δ = b² - 4ac = (${fmt(b)})² - 4(${fmt(a)})(${fmt(c)}) = ${fmt(b * b)} - (${fmt(4.0 * a * c)}) = ${fmt(discriminant)}"
        )

        return when {
            discriminant > 1e-9 -> {
                val sqrtD = sqrt(discriminant)
                val x1 = (-b + sqrtD) / (2.0 * a)
                val x2 = (-b - sqrtD) / (2.0 * a)
                steps.add("4. Since Δ > 0, roots are real and distinct.")
                steps.add("5. x = (-b ± √Δ) / 2a = (${fmt(-b)} ± √${fmt(discriminant)}) / ${fmt(2.0 * a)}")
                steps.add("6. x₁ = ${fmt(x1)},  x₂ = ${fmt(x2)}")
                MathEngineSolution(
                    topic = "Quadratic Formula Verification",
                    summary = "Two real and distinct roots (Δ = ${fmt(discriminant)} > 0)",
                    steps = steps,
                    finalAnswer = "x = ${fmt(x1)} or x = ${fmt(x2)}",
                    numericResults = listOf(x1, x2)
                )
            }
            abs(discriminant) <= 1e-9 -> {
                val x = -b / (2.0 * a)
                steps.add("4. Since Δ = 0, roots are real and equal.")
                steps.add("5. x = -b / 2a = ${fmt(-b)} / ${fmt(2.0 * a)} = ${fmt(x)}")
                MathEngineSolution(
                    topic = "Quadratic Formula Verification",
                    summary = "Two real and equal roots (Δ = 0)",
                    steps = steps,
                    finalAnswer = "x = ${fmt(x)}, ${fmt(x)}",
                    numericResults = listOf(x)
                )
            }
            else -> {
                val realPart = -b / (2.0 * a)
                val imagPart = sqrt(-discriminant) / abs(2.0 * a)
                steps.add("4. Since Δ < 0, the equation has NO real roots.")
                steps.add("5. Complex roots: ${fmt(realPart)} ± ${fmt(imagPart)}i")
                MathEngineSolution(
                    topic = "Quadratic Formula Verification",
                    summary = "No real roots (Δ = ${fmt(discriminant)} < 0)",
                    steps = steps,
                    finalAnswer = "No real roots (Δ < 0)"
                )
            }
        }
    }

    fun solveArithmeticProgression(a: Double, d: Double, n: Int): MathEngineSolution {
        val safeN = n.coerceAtLeast(1)
        val an = a + (safeN - 1) * d
        val sn = (safeN / 2.0) * (2.0 * a + (safeN - 1) * d)
        val steps = listOf(
            "1. Given AP parameters: First term a = ${fmt(a)}, Common difference d = ${fmt(d)}, Terms n = $safeN",
            "2. Formula for nth term: aₙ = a + (n - 1)d",
            "3. Substitution: a_$safeN = ${fmt(a)} + ($safeN - 1)(${fmt(d)}) = ${fmt(a)} + ${fmt((safeN - 1) * d)} = ${fmt(an)}",
            "4. Formula for sum of n terms: Sₙ = (n / 2)[2a + (n - 1)d]",
            "5. Substitution: S_$safeN = (${safeN}/2)[2(${fmt(a)}) + ($safeN - 1)(${fmt(d)})] = ${fmt(sn)}"
        )
        return MathEngineSolution(
            topic = "Arithmetic Progression Verification",
            summary = "a_$safeN = ${fmt(an)} and S_$safeN = ${fmt(sn)}",
            steps = steps,
            finalAnswer = "a_$safeN = ${fmt(an)}, S_$safeN = ${fmt(sn)}",
            numericResults = listOf(an, sn)
        )
    }

    fun solveLinearPair(
        a1: Double, b1: Double, c1: Double,
        a2: Double, b2: Double, c2: Double
    ): MathEngineSolution {
        val det = a1 * b2 - a2 * b1
        val steps = mutableListOf(
            "1. System: ${fmt(a1)}x + (${fmt(b1)})y + (${fmt(c1)}) = 0  and  ${fmt(a2)}x + (${fmt(b2)})y + (${fmt(c2)}) = 0"
        )
        return if (abs(det) > 1e-9) {
            val x = (b1 * c2 - b2 * c1) / det
            val y = (c1 * a2 - c2 * a1) / det
            steps.add("2. Ratio check: a₁/a₂ ≠ b₁/b₂ (since a₁b₂ - a₂b₁ = ${fmt(det)} ≠ 0) -> Intersecting lines, unique solution.")
            steps.add("3. By cross-multiplication: x = (b₁c₂ - b₂c₁)/(a₁b₂ - a₂b₁) = ${fmt(x)}")
            steps.add("4. y = (c₁a₂ - c₂a₁)/(a₁b₂ - a₂b₁) = ${fmt(y)}")
            MathEngineSolution(
                topic = "Pair of Linear Equations",
                summary = "Consistent system with unique solution",
                steps = steps,
                finalAnswer = "(x, y) = (${fmt(x)}, ${fmt(y)})",
                numericResults = listOf(x, y)
            )
        } else {
            val isParallel = abs(a1 * c2 - a2 * c1) > 1e-9 || abs(b1 * c2 - b2 * c1) > 1e-9
            if (isParallel) {
                steps.add("2. a₁/a₂ = b₁/b₂ ≠ c₁/c₂ -> Parallel lines.")
                MathEngineSolution(
                    topic = "Pair of Linear Equations",
                    summary = "Inconsistent system (parallel lines)",
                    steps = steps,
                    finalAnswer = "No solution"
                )
            } else {
                steps.add("2. a₁/a₂ = b₁/b₂ = c₁/c₂ -> Coincident lines.")
                MathEngineSolution(
                    topic = "Pair of Linear Equations",
                    summary = "Dependent consistent system (coincident lines)",
                    steps = steps,
                    finalAnswer = "Infinitely many solutions"
                )
            }
        }
    }

    fun solveCoordinateGeometry(
        x1: Double, y1: Double,
        x2: Double, y2: Double,
        m1: Double = 1.0, m2: Double = 1.0
    ): MathEngineSolution {
        val dx = x2 - x1
        val dy = y2 - y1
        val distSq = dx * dx + dy * dy
        val dist = sqrt(distSq)
        val denom = if (abs(m1 + m2) < 1e-9) 1.0 else m1 + m2
        val px = (m1 * x2 + m2 * x1) / denom
        val py = (m1 * y2 + m2 * y1) / denom
        val midX = (x1 + x2) / 2.0
        val midY = (y1 + y2) / 2.0

        val steps = listOf(
            "1. Points A(${fmt(x1)}, ${fmt(y1)}) and B(${fmt(x2)}, ${fmt(y2)})",
            "2. Distance d = √[(x₂ - x₁)² + (y₂ - y₁)²] = √[(${fmt(dx)})² + (${fmt(dy)})²] = √${fmt(distSq)} ≈ ${fmt(dist)}",
            "3. Midpoint M = ((x₁ + x₂)/2, (y₁ + y₂)/2) = (${fmt(midX)}, ${fmt(midY)})",
            "4. Section point P(${fmt(m1)}:${fmt(m2)}) = (${fmt(px)}, ${fmt(py)})"
        )
        return MathEngineSolution(
            topic = "Coordinate Geometry Verification",
            summary = "Distance = √${fmt(distSq)} (${fmt(dist)}), Midpoint = (${fmt(midX)}, ${fmt(midY)})",
            steps = steps,
            finalAnswer = "d = ${fmt(dist)}, M = (${fmt(midX)}, ${fmt(midY)})",
            numericResults = listOf(dist, midX, midY, px, py)
        )
    }

    fun verifyNumericAnswer(
        studentInput: String,
        expectedValues: List<Double>,
        tolerance: Double = 0.05
    ): AnswerVerificationResult {
        val parsed = parseNumericTokens(studentInput)
        if (parsed.isEmpty()) {
            return AnswerVerificationResult(
                matched = false,
                expectedDisplay = expectedValues.joinToString(", ") { fmt(it) },
                feedback = "Please enter a numeric value (e.g. 5, -3/2, or 2.5) so the Mathematics Engine can verify it."
            )
        }
        val allMatch = expectedValues.all { exp ->
            parsed.any { abs(it - exp) <= tolerance }
        }
        return if (allMatch) {
            AnswerVerificationResult(
                matched = true,
                expectedDisplay = expectedValues.joinToString(", ") { fmt(it) },
                feedback = "Verified! Your numerical value matches the exact mathematical solution."
            )
        } else {
            val signFlip = expectedValues.any { exp -> parsed.any { abs(it + exp) <= tolerance } }
            val hint = if (signFlip) {
                "Your magnitude is right, but check a +/- sign during transposition or substitution."
            } else {
                "Your intermediate step may have a calculation slip. Double-check formula substitution and try again."
            }
            AnswerVerificationResult(
                matched = false,
                expectedDisplay = expectedValues.joinToString(", ") { fmt(it) },
                feedback = hint
            )
        }
    }

    private fun parseNumericTokens(input: String): List<Double> {
        val cleaned = input.replace("=", " ").replace(",", " ").replace("x", " ").replace("y", " ")
        val regex = Regex("-?\\d+(?:\\.\\d+)?(?:/\\d+(?:\\.\\d+)?)?")
        return regex.findAll(cleaned).mapNotNull { match ->
            val raw = match.value
            if (raw.contains("/")) {
                val parts = raw.split("/")
                val num = parts.getOrNull(0)?.toDoubleOrNull()
                val den = parts.getOrNull(1)?.toDoubleOrNull()
                if (num != null && den != null && abs(den) > 1e-9) num / den else null
            } else {
                raw.toDoubleOrNull()
            }
        }.toList()
    }

    fun fmt(value: Double): String {
        val rounded = kotlin.math.round(value * 1000.0) / 1000.0
        return if (abs(rounded - rounded.toLong().toDouble()) < 1e-6) {
            rounded.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", rounded).trimEnd('0').trimEnd('.')
        }
    }
}
