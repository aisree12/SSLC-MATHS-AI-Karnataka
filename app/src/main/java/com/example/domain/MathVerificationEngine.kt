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

data class MathAuditCheckResult(
    val chapterNumber: Int,
    val problemTitle: String,
    val expectedSummary: String,
    val computedAnswer: String,
    val passed: Boolean
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

    fun solveCircleTangentLength(centerDistance: Double, radius: Double): MathEngineSolution {
        val diff = centerDistance * centerDistance - radius * radius
        if (diff <= 0.0) {
            return MathEngineSolution(
                topic = "Circle Tangent Verification",
                summary = "Invalid geometry: point must lie strictly outside circle (d > r).",
                steps = listOf("Given OQ = ${fmt(centerDistance)}, r = ${fmt(radius)} ⇒ OQ ≤ r."),
                finalAnswer = "No tangent exists"
            )
        }
        val tangentLen = sqrt(diff)
        val steps = listOf(
            "1. By Theorem 4.1, radius OP ⊥ tangent PQ ⇒ ∠OPQ = 90°",
            "2. OQ² = OP² + PQ² ⇒ PQ = √(${fmt(centerDistance)}² - ${fmt(radius)}²)",
            "3. PQ = √${fmt(diff)} = ${fmt(tangentLen)}"
        )
        return MathEngineSolution(
            topic = "Circle Tangent Verification",
            summary = "PQ = ${fmt(tangentLen)}",
            steps = steps,
            finalAnswer = "PQ = ${fmt(tangentLen)}",
            numericResults = listOf(tangentLen)
        )
    }

    fun solveSectorAndSegment(
        radius: Double,
        angleDeg: Double,
        piValue: Double = 22.0 / 7.0
    ): MathEngineSolution {
        val arcLen = (angleDeg / 360.0) * 2.0 * piValue * radius
        val sectorArea = (angleDeg / 360.0) * piValue * radius * radius
        val triArea = 0.5 * radius * radius * kotlin.math.sin(Math.toRadians(angleDeg))
        val segmentArea = sectorArea - triArea
        val steps = listOf(
            "1. Given r = ${fmt(radius)}, θ = ${fmt(angleDeg)}°, π = ${fmt(piValue)}",
            "2. Arc Length l = (θ/360) × 2πr = ${fmt(arcLen)}",
            "3. Sector Area = (θ/360) × πr² = ${fmt(sectorArea)}",
            "4. Minor Segment = Sector - ½r²sinθ = ${fmt(sectorArea)} - ${fmt(triArea)} = ${fmt(segmentArea)}"
        )
        return MathEngineSolution(
            topic = "Sector & Segment Verification",
            summary = "Sector = ${fmt(sectorArea)}, Minor Segment = ${fmt(segmentArea)}",
            steps = steps,
            finalAnswer = "Sector = ${fmt(sectorArea)}, Segment = ${fmt(segmentArea)}",
            numericResults = listOf(arcLen, sectorArea, segmentArea)
        )
    }

    fun solveEuclidHcfLcm(a: Int, b: Int): MathEngineSolution {
        var x = abs(a)
        var y = abs(b)
        val steps = mutableListOf<String>()
        while (y != 0) {
            val q = x / y
            val r = x % y
            steps.add("$x = $y × $q + $r")
            x = y
            y = r
        }
        val hcf = x.coerceAtLeast(1)
        val lcm = (abs(a.toLong() * b.toLong()) / hcf).toDouble()
        steps.add("HCF($a, $b) = $hcf; LCM($a, $b) = ($a × $b)/$hcf = ${fmt(lcm)}")
        return MathEngineSolution(
            topic = "Euclid's Lemma HCF & LCM",
            summary = "HCF = $hcf, LCM = ${fmt(lcm)}",
            steps = steps,
            finalAnswer = "HCF = $hcf, LCM = ${fmt(lcm)}",
            numericResults = listOf(hcf.toDouble(), lcm)
        )
    }

    fun solveElevationHeight(horizontalDistance: Double, angleDeg: Int): MathEngineSolution {
        val tanTheta = kotlin.math.tan(Math.toRadians(angleDeg.toDouble()))
        val height = horizontalDistance * tanTheta
        val steps = listOf(
            "1. Base distance BC = ${fmt(horizontalDistance)}, Angle of elevation θ = $angleDeg°",
            "2. tan($angleDeg°) = Height / Base ⇒ h = ${fmt(horizontalDistance)} × ${fmt(tanTheta)} = ${fmt(height)}"
        )
        return MathEngineSolution(
            topic = "Heights & Distances",
            summary = "Height = ${fmt(height)}",
            steps = steps,
            finalAnswer = "h = ${fmt(height)}",
            numericResults = listOf(height)
        )
    }

    fun solveGroupedDirectMean(classMarks: List<Double>, frequencies: List<Double>): MathEngineSolution {
        val sumF = frequencies.sum().coerceAtLeast(1e-9)
        val sumFx = classMarks.zip(frequencies).sumOf { (x, f) -> x * f }
        val mean = sumFx / sumF
        return MathEngineSolution(
            topic = "Statistics Direct Mean",
            summary = "Σfᵢxᵢ = ${fmt(sumFx)}, Σfᵢ = ${fmt(sumF)}, Mean = ${fmt(mean)}",
            steps = listOf(
                "1. Σfᵢ = ${fmt(sumF)}, Σfᵢxᵢ = ${fmt(sumFx)}",
                "2. Mean x̄ = Σfᵢxᵢ / Σfᵢ = ${fmt(mean)}"
            ),
            finalAnswer = "x̄ = ${fmt(mean)}",
            numericResults = listOf(mean, sumFx, sumF)
        )
    }

    fun solveMensurationFrustum(
        r1: Double,
        r2: Double,
        height: Double,
        piValue: Double = 22.0 / 7.0
    ): MathEngineSolution {
        val slant = sqrt(height * height + (r1 - r2) * (r1 - r2))
        val csa = piValue * (r1 + r2) * slant
        val vol = (1.0 / 3.0) * piValue * height * (r1 * r1 + r2 * r2 + r1 * r2)
        return MathEngineSolution(
            topic = "Frustum of a Cone",
            summary = "l = ${fmt(slant)}, CSA = ${fmt(csa)}, V = ${fmt(vol)}",
            steps = listOf(
                "1. l = √[h² + (r₁ - r₂)²] = ${fmt(slant)}",
                "2. CSA = π(r₁ + r₂)l = ${fmt(csa)}",
                "3. Volume = (1/3)πh(r₁² + r₂² + r₁r₂) = ${fmt(vol)}"
            ),
            finalAnswer = "V = ${fmt(vol)} cm³",
            numericResults = listOf(vol, csa, slant)
        )
    }

    fun verifyAllBundledTextbookExamples(): List<MathAuditCheckResult> {
        val checks = mutableListOf<MathAuditCheckResult>()

        val ch1 = solveArithmeticProgression(3.0, 5.0, 20)
        checks += MathAuditCheckResult(11, "Ch 5: 20th term & sum of AP 3, 8, 13...", "a20=98, S20=1010", ch1.finalAnswer,
            abs(ch1.numericResults[0] - 98.0) < 1e-4 && abs(ch1.numericResults[1] - 1010.0) < 1e-4)

        val ch3 = solveLinearPair(1.0, 1.0, -14.0, 1.0, -1.0, -4.0)
        checks += MathAuditCheckResult(3, "Ch 3: Linear pair x+y=14, x-y=4", "(9, 5)", ch3.finalAnswer,
            abs(ch3.numericResults[0] - 9.0) < 1e-4 && abs(ch3.numericResults[1] - 5.0) < 1e-4)

        val ch4 = solveCircleTangentLength(25.0, 7.0)
        checks += MathAuditCheckResult(4, "Ch 10: Tangent length (OQ=25, r=7)", "24", ch4.finalAnswer,
            abs(ch4.numericResults[0] - 24.0) < 1e-4)

        val ch5 = solveSectorAndSegment(10.0, 90.0, 3.14)
        checks += MathAuditCheckResult(5, "Ch 11: Sector & minor segment (r=10, 90°)", "78.5 / 28.5", ch5.finalAnswer,
            abs(ch5.numericResults[1] - 78.5) < 0.05 && abs(ch5.numericResults[2] - 28.5) < 0.05)

        val ch7 = solveCoordinateGeometry(-1.0, 7.0, 4.0, -3.0, 2.0, 3.0)
        checks += MathAuditCheckResult(7, "Ch 7: Section formula (2:3) on (-1,7) & (4,-3)", "P(1, 3)", "(${fmt(ch7.numericResults[3])}, ${fmt(ch7.numericResults[4])})",
            abs(ch7.numericResults[3] - 1.0) < 1e-4 && abs(ch7.numericResults[4] - 3.0) < 1e-4)

        val ch8 = solveEuclidHcfLcm(96, 404)
        checks += MathAuditCheckResult(1, "Ch 1: Euclid HCF & LCM of 96 and 404", "HCF=4, LCM=9696", ch8.finalAnswer,
            abs(ch8.numericResults[0] - 4.0) < 1e-4 && abs(ch8.numericResults[1] - 9696.0) < 1e-4)

        val ch10 = solveQuadratic(2.0, -7.0, 3.0)
        checks += MathAuditCheckResult(10, "Ch 4: Roots of 2x² - 7x + 3 = 0", "3, 0.5", ch10.finalAnswer,
            abs(ch10.numericResults[0] - 3.0) < 1e-4 && abs(ch10.numericResults[1] - 0.5) < 1e-4)

        val ch12 = solveElevationHeight(30.0, 30)
        checks += MathAuditCheckResult(9, "Ch 9: Tower height (30m base, 30°)", "10√3 ≈ 17.32", ch12.finalAnswer,
            abs(ch12.numericResults[0] - 10.0 * sqrt(3.0)) < 0.05)

        val ch13 = solveGroupedDirectMean(listOf(5.0, 15.0, 25.0, 35.0, 45.0), listOf(3.0, 5.0, 9.0, 5.0, 3.0))
        checks += MathAuditCheckResult(13, "Ch 13: Direct Mean of grouped table", "25", ch13.finalAnswer,
            abs(ch13.numericResults[0] - 25.0) < 1e-4)

        val ch15 = solveMensurationFrustum(2.0, 1.0, 14.0)
        checks += MathAuditCheckResult(12, "Ch 12: Frustum drinking glass capacity (r1=2, r2=1, h=14)", "102.67", ch15.finalAnswer,
            abs(ch15.numericResults[0] - (308.0 / 3.0)) < 0.05)

        return checks
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
