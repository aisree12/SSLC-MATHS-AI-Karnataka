package com.example.data.ksmke

import com.example.domain.ChapterQuestionPaper
import com.example.domain.DifficultyLevel
import com.example.domain.KsmkeChapter
import com.example.domain.PyqQuestion
import com.example.domain.QuestionFormat
import com.example.domain.QuestionSource
import com.example.domain.SolvedExample
import com.example.domain.TheoremCard
import com.example.domain.TopicDetailedNote

/**
 * Karnataka SSLC Mathematics Knowledge Engine (KSMKE)
 * Complete 15-chapter KSEAB Class 10 syllabus + verified 2015-2026+ PYQ & Model Paper bank.
 */
object KsmkeSyllabusRepository {

    private fun detailedTopicsFor(chapterNumber: Int): List<TopicDetailedNote> =
        (KsmkeDetailedNotesPart1.byChapter[chapterNumber]
            ?: KsmkeDetailedNotesPart2.byChapter[chapterNumber])
            .orEmpty()
            .map { note ->
                val src = OfficialSourceLibrary.topicSourceIndex[note.topicNumber]
                if (src != null) {
                    note.copy(
                        sourcePageRef = src.sourcePageRef,
                        exerciseRef = src.exerciseRef,
                        dikshaModuleTitle = src.dikshaModuleTitle
                    )
                } else note
            }

    val chapters: List<KsmkeChapter> = listOf(
        KsmkeChapter(
            chapterNumber = 1,
            titleEn = "Arithmetic Progressions",
            titleKn = "ಸಮಾಂತರ ಶ್ರೇಢಿಗಳು",
            unitArea = "Algebra",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 1–26",
            boardMarksWeightage = 6,
            summaryEn = "Sequences with constant common difference d, nth term aₙ, and sum of n terms Sₙ with real-life word problems.",
            summaryKn = "ಸ್ಥಿರ ಸಾಮಾನ್ಯ ವ್ಯತ್ಯಾಸ d ಇರುವ ಶ್ರೇಢಿಗಳು, n-ನೇ ಪದ aₙ ಮತ್ತು n ಪದಗಳ ಮೊತ್ತ Sₙ ಕಂಡುಹಿಡಿಯುವ ವಿಧಾನ.",
            keyFormulas = listOf(
                "Common difference: d = a₂ - a₁ = aₙ - aₙ₋₁",
                "nth term from start: aₙ = a + (n - 1)d",
                "Sum of n terms: Sₙ = (n / 2)[2a + (n - 1)d]",
                "Sum using first & last term: Sₙ = (n / 2)(a + l)",
                "Relation: aₙ = Sₙ - Sₙ₋₁"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Find the 20th term of AP: 3, 8, 13, ...",
                    problem = "Find the 20th term and sum of first 20 terms of the AP: 3, 8, 13, 18, ...",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Given AP: a = 3, d = 8 - 3 = 5, n = 20",
                        "Step 2 (0.5M): Formula: aₙ = a + (n - 1)d",
                        "Step 3 (1.0M): a₂₀ = 3 + (20 - 1)(5) = 3 + 95 = 98",
                        "Step 4 (1.0M): S₂₀ = (20/2)[3 + 98] = 10 × 101 = 1010"
                    ),
                    finalAnswer = "a₂₀ = 98, S₂₀ = 1010",
                    examinerTip = "Always write d = a₂ - a₁ explicitly and highlight both final values."
                )
            ),
            commonMistakes = listOf(
                "Taking d = a₁ - a₂ instead of a₂ - a₁ in decreasing APs (forgetting negative sign).",
                "Using n instead of (n - 1) inside aₙ = a + (n - 1)d."
            ),
            starterQuestion = "Why do we subtract 1 from n in aₙ = a + (n - 1)d?"
        ),
        KsmkeChapter(
            chapterNumber = 2,
            titleEn = "Triangles",
            titleKn = "ತ್ರಿಭುಜಗಳು",
            unitArea = "Geometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 27–58",
            boardMarksWeightage = 8,
            summaryEn = "Similarity of triangles, Basic Proportionality Theorem (Thales Theorem), AAA/SAS/SSS criteria, and areas of similar triangles.",
            summaryKn = "ಸಮರೂಪ ತ್ರಿಭುಜಗಳು, ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯ (ಥೇಲ್ಸ್ ಪ್ರಮೇಯ) ಮತ್ತು ಸಮರೂಪತೆಯ ನಿರ್ಧಾರಕ ಗುಣಗಳು.",
            keyFormulas = listOf(
                "BPT (Thales): If DE || BC in △ABC, then AD/DB = AE/EC",
                "AAA / AA Similarity: Corresponding angles equal ⇒ sides proportional",
                "Area Ratio Theorem: Area(△ABC)/Area(△DEF) = (AB/DE)² = (BC/EF)² = (AC/DF)²"
            ),
            theorems = listOf(
                TheoremCard(
                    name = "Basic Proportionality Theorem (Thales Theorem)",
                    kannadaName = "ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯ",
                    statement = "If a line is drawn parallel to one side of a triangle intersecting the other two sides in distinct points, the other two sides are divided in the same ratio.",
                    keyProofSteps = listOf(
                        "1. Statement & Data (1M): In △ABC, DE || BC intersecting AB at D and AC at E. To prove: AD/DB = AE/EC.",
                        "2. Construction (0.5M): Join BE and CD; draw EN ⊥ AB and DM ⊥ AC.",
                        "3. Ratio 1 (1M): ar(△ADE)/ar(△BDE) = (½·AD·EN)/(½·DB·EN) = AD/DB.",
                        "4. Ratio 2 (1M): ar(△ADE)/ar(△CDE) = (½·AE·DM)/(½·EC·DM) = AE/EC.",
                        "5. Conclusion (0.5M): △BDE and △CDE lie on same base DE between parallels DE || BC ⇒ ar(△BDE) = ar(△CDE). Hence AD/DB = AE/EC."
                    ),
                    marksWeight = 4
                )
            ),
            solvedExamples = listOf(
                SolvedExample(
                    title = "BPT Numerical Application",
                    problem = "In △ABC, DE || BC. If AD = 1.5 cm, DB = 3 cm and AE = 1 cm, find EC.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Since DE || BC, by Thales Theorem: AD/DB = AE/EC",
                        "Step 2 (1.0M): 1.5 / 3 = 1 / EC",
                        "Step 3 (0.5M): EC = 3 / 1.5 = 2 cm"
                    ),
                    finalAnswer = "EC = 2 cm",
                    examinerTip = "Draw a neat labelled triangle with parallel arrows on DE and BC."
                )
            ),
            commonMistakes = listOf(
                "Omitting Data / To Prove / Construction headings in 4-mark theorem proofs.",
                "Forgetting to state 'same base and between same parallels' in BPT proof."
            ),
            starterQuestion = "How do I remember all 5 steps of Thales (BPT) theorem proof for 4 marks?"
        ),
        KsmkeChapter(
            chapterNumber = 3,
            titleEn = "Pair of Linear Equations in Two Variables",
            titleKn = "ಎರಡು ಚರಾಕ್ಷರಗಳಿರುವ ರೇಖಾತ್ಮಕ ಸಮೀಕರಣಗಳ ಜೋಡಿಗಳು",
            unitArea = "Algebra",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 59–88",
            boardMarksWeightage = 8,
            summaryEn = "Graphical solution (4-mark mandatory board question), substitution, elimination, and ratio consistency criteria.",
            summaryKn = "ನಕ್ಷೆಯ ವಿಧಾನ (4 ಅಂಕ), ಆದೇಶ ವಿಧಾನ, ವರ್ಜಿಸುವ ವಿಧಾನ ಮತ್ತು ಸಮೀಕರಣಗಳ ಸ್ಥಿರತೆ.",
            keyFormulas = listOf(
                "Intersecting (Unique solution): a₁/a₂ ≠ b₁/b₂",
                "Coincident (Infinite solutions): a₁/a₂ = b₁/b₂ = c₁/c₂",
                "Parallel (No solution): a₁/a₂ = b₁/b₂ ≠ c₁/c₂"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Solve by Elimination: x + y = 14 and x - y = 4",
                    problem = "Solve the pair of linear equations: x + y = 14 and x - y = 4.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Label equations: x + y = 14 ...(1), x - y = 4 ...(2)",
                        "Step 2 (0.5M): Add (1) and (2): 2x = 18 ⇒ x = 9",
                        "Step 3 (0.5M): Substitute x = 9 in (1): 9 + y = 14 ⇒ y = 5",
                        "Step 4 (0.5M): Verify: 9 - 5 = 4 (True). Solution: x = 9, y = 5"
                    ),
                    finalAnswer = "x = 9, y = 5",
                    examinerTip = "For 4-mark graph questions, write at least 3 clean (x, y) pairs in a table and write Scale on top-right."
                )
            ),
            commonMistakes = listOf(
                "Forgetting to write Scale (e.g., X-axis: 1 cm = 1 unit) on the graph sheet.",
                "Sign errors when subtracting two equations during elimination."
            ),
            starterQuestion = "How do I choose easy points for the 4-mark linear equations graph question?"
        ),
        KsmkeChapter(
            chapterNumber = 4,
            titleEn = "Circles",
            titleKn = "ವೃತ್ತಗಳು",
            unitArea = "Geometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 89–104",
            boardMarksWeightage = 5,
            summaryEn = "Tangent to a circle, perpendicularity at point of contact, and equality of tangents drawn from an external point.",
            summaryKn = "ವೃತ್ತಕ್ಕೆ ಸ್ಪರ್ಶಕಗಳು, ಸ್ಪರ್ಶ ಬಿಂದುವಿನಲ್ಲಿ ತ್ರಿಜ್ಯದ ಲಂಬತೆ ಮತ್ತು ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳ ಸಮಾನತೆ.",
            keyFormulas = listOf(
                "Theorem 4.1: Tangent at any point of a circle is perpendicular to the radius through the point of contact (OP ⊥ XY).",
                "Theorem 4.2: Lengths of tangents drawn from an external point to a circle are equal (PQ = PR).",
                "Angle relation: ∠QOR + ∠QPR = 180°"
            ),
            theorems = listOf(
                TheoremCard(
                    name = "Tangents from an External Point",
                    kannadaName = "ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ವೃತ್ತಕ್ಕೆ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳ ಪ್ರಮೇಯ",
                    statement = "The lengths of tangents drawn from an external point to a circle are equal.",
                    keyProofSteps = listOf(
                        "1. Data: Circle with centre O, external point P, tangents PQ and PR.",
                        "2. To Prove: PQ = PR.",
                        "3. Construction: Join OP, OQ, OR.",
                        "4. Proof: In right △OQP and △ORP, ∠OQP = ∠ORP = 90° (radius ⊥ tangent), OP = OP (common hypotenuse), OQ = OR (radii).",
                        "5. By RHS congruence: △OQP ≅ △ORP ⇒ PQ = PR (CPCT)."
                    ),
                    marksWeight = 3
                )
            ),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Tangent Length using Pythagoras",
                    problem = "From a point Q, the length of the tangent to a circle is 24 cm and distance of Q from centre is 25 cm. Find the radius.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): By Theorem 4.1, radius OP ⊥ tangent PQ ⇒ ∠OPQ = 90°",
                        "Step 2 (0.5M): By Pythagoras theorem: OQ² = OP² + PQ²",
                        "Step 3 (1.0M): 25² = r² + 24² ⇒ 625 = r² + 576 ⇒ r² = 49 ⇒ r = 7 cm"
                    ),
                    finalAnswer = "r = 7 cm",
                    examinerTip = "Remember the Pythagorean triplet (7, 24, 25) to verify your answer quickly."
                )
            ),
            commonMistakes = listOf(
                "Using SAS instead of RHS congruence in the 3-mark tangent theorem proof.",
                "Confusing distance from centre (hypotenuse) with tangent length."
            ),
            starterQuestion = "Prove that tangents from an external point to a circle are equal."
        ),
        KsmkeChapter(
            chapterNumber = 5,
            titleEn = "Areas Related to Circles",
            titleKn = "ವೃತ್ತಗಳಿಗೆ ಸಂಬಂಧಿಸಿದ ವಿಸ್ತೀರ್ಣಗಳು",
            unitArea = "Mensuration",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 105–122",
            boardMarksWeightage = 4,
            summaryEn = "Area and perimeter of circles, sectors, segments, and combined shaded plane figures.",
            summaryKn = "ವೃತ್ತಖಂಡ, ತ್ರಿಜ್ಯಾಂತರ ಖಂಡದ ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಕಂಸದ ಉದ್ದ.",
            keyFormulas = listOf(
                "Length of arc: l = (θ / 360°) × 2πr",
                "Area of sector: A = (θ / 360°) × πr² = ½ × l × r",
                "Area of segment: Area of sector - Area of corresponding triangle",
                "Area of quadrant: ¼ πr²"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Area of Sector with θ = 60°",
                    problem = "Find the area of a sector of a circle with radius 6 cm if angle of the sector is 60°. (Take π = 22/7)",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Given: r = 6 cm, θ = 60°",
                        "Step 2 (0.5M): Formula: Area = (θ / 360°) × πr²",
                        "Step 3 (1.0M): Area = (60/360) × (22/7) × 6 × 6 = 132/7 = 18.86 cm²"
                    ),
                    finalAnswer = "132/7 cm² (≈ 18.86 cm²)",
                    examinerTip = "Write cm² as the final unit; missing units can cost 0.5 marks."
                )
            ),
            commonMistakes = listOf(
                "Using πr² instead of 2πr for arc length.",
                "Forgetting to subtract triangle area when asked for 'segment' instead of 'sector'."
            ),
            starterQuestion = "What is the difference between a sector and a segment of a circle?"
        ),
        KsmkeChapter(
            chapterNumber = 6,
            titleEn = "Constructions",
            titleKn = "ರಚನೆಗಳು",
            unitArea = "Geometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 123–134",
            boardMarksWeightage = 5,
            summaryEn = "Dividing a line segment in a given ratio, constructing a triangle similar to a given triangle, and tangents to a circle.",
            summaryKn = "ರೇಖಾಖಂಡದ ವಿಭಜನೆ, ಸಮರೂಪ ತ್ರಿಭುಜಗಳ ರಚನೆ ಮತ್ತು ವೃತ್ತಕ್ಕೆ ಸ್ಪರ್ಶಕಗಳ ರಚನೆ.",
            keyFormulas = listOf(
                "Line division m:n -> Draw ray at acute angle, mark m + n equal arcs",
                "Similar triangle scale factor a/b -> Mark max(a, b) equal arcs; connect bth arc to vertex",
                "Tangents from point P at distance d -> Bisect OP, draw circle with midpoint M as centre"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Scale Factor 3/5 Similar Triangle",
                    problem = "Construct a triangle with sides 5 cm, 6 cm, 7 cm and then another triangle whose sides are 3/5 of the corresponding sides.",
                    markingSteps = listOf(
                        "Step 1 (1.0M): Construct △ABC with BC = 7 cm, AB = 5 cm, AC = 6 cm.",
                        "Step 2 (1.0M): Draw acute ray BX below BC and mark 5 equal points B₁..B₅.",
                        "Step 3 (1.0M): Join B₅C; draw B₃C' || B₅C and C'A' || CA to get △A'BC'."
                    ),
                    finalAnswer = "△A'BC' is the required smaller similar triangle",
                    examinerTip = "Never erase construction arcs; keep pencil sharp and label every intersection."
                )
            ),
            commonMistakes = listOf(
                "Joining numerator arc instead of denominator arc to vertex C.",
                "Erasing compass arcs or failing to label the resulting triangle."
            ),
            starterQuestion = "In similar triangle construction, should the new triangle be inside or outside if scale factor is 7/5?"
        ),
        KsmkeChapter(
            chapterNumber = 7,
            titleEn = "Coordinate Geometry",
            titleKn = "ನಿರ್ದೇಶಾಂಕ ರೇಖಾಗಣಿತ",
            unitArea = "Coordinate Geometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 135–154",
            boardMarksWeightage = 5,
            summaryEn = "Distance formula, distance from origin, section formula (internal division), midpoint formula, and area of a triangle.",
            summaryKn = "ದೂರ ಸೂತ್ರ, ಭಾಗ ಪ್ರಮಾಣ ಸೂತ್ರ, ಮಧ್ಯಬಿಂದು ಸೂತ್ರ ಮತ್ತು ತ್ರಿಭುಜದ ವಿಸ್ತೀರ್ಣ.",
            keyFormulas = listOf(
                "Distance: d = √[(x₂ - x₁)² + (y₂ - y₁)²]",
                "Distance from origin: d = √(x² + y²)",
                "Section Formula: P(x, y) = ((m₁x₂ + m₂x₁)/(m₁ + m₂), (m₁y₂ + m₂y₁)/(m₁ + m₂))",
                "Midpoint: M = ((x₁ + x₂)/2, (y₁ + y₂)/2)",
                "Area of △: ½ |x₁(y₂ - y₃) + x₂(y₃ - y₁) + x₃(y₁ - y₂)|"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Distance between (2, 3) and (4, 1)",
                    problem = "Find the distance between the points P(2, 3) and Q(4, 1).",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Let (x₁, y₁) = (2, 3) and (x₂, y₂) = (4, 1)",
                        "Step 2 (0.5M): d = √[(x₂ - x₁)² + (y₂ - y₁)²]",
                        "Step 3 (1.0M): d = √[(4 - 2)² + (1 - 3)²] = √[4 + 4] = √8 = 2√2 units"
                    ),
                    finalAnswer = "2√2 units",
                    examinerTip = "Simplify √8 into 2√2 and always write 'units'."
                )
            ),
            commonMistakes = listOf(
                "Writing (x₂ + x₁)² instead of (x₂ - x₁)² in the distance formula.",
                "Swapping m₁ and m₂ in the section formula."
            ),
            starterQuestion = "How do we prove three points are collinear using Coordinate Geometry?"
        ),
        KsmkeChapter(
            chapterNumber = 8,
            titleEn = "Real Numbers",
            titleKn = "ವಾಸ್ತವ ಸಂಖ್ಯೆಗಳು",
            unitArea = "Number System",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 155–170",
            boardMarksWeightage = 4,
            summaryEn = "Euclid's division lemma, Fundamental Theorem of Arithmetic (HCF & LCM), proving irrationality of √2, √3, √5, and decimal expansions.",
            summaryKn = "ಯೂಕ್ಲಿಡ್ ಭಾಗಾಕಾರ ಅನುಪ್ರಮೇಯ, ಅಂಕಗಣಿತದ ಮೂಲ ಪ್ರಮೇಯ ಮತ್ತು ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆಗಳ ಸಾಧನೆ.",
            keyFormulas = listOf(
                "Euclid's Division Lemma: a = bq + r, where 0 ≤ r < b",
                "Product Rule: HCF(a, b) × LCM(a, b) = a × b",
                "Terminating decimal: Denominator q = 2ⁿ × 5ᵐ"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Prove √3 is Irrational",
                    problem = "Prove that √3 is an irrational number.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Assume contrary that √3 is rational: √3 = p/q (p, q coprime integers, q ≠ 0).",
                        "Step 2 (0.5M): Squaring both sides: 3q² = p² ⇒ 3 divides p² ⇒ 3 divides p.",
                        "Step 3 (0.5M): Let p = 3c ⇒ 3q² = 9c² ⇒ q² = 3c² ⇒ 3 divides q.",
                        "Step 4 (0.5M): Both p and q have common factor 3, contradicting coprime assumption. Hence √3 is irrational."
                    ),
                    finalAnswer = "Hence proved √3 is irrational",
                    examinerTip = "State clearly that p and q are 'co-prime' at the very first line."
                )
            ),
            commonMistakes = listOf(
                "Applying HCF × LCM = a × b to three numbers (it only holds for two positive integers!)."
            ),
            starterQuestion = "Walk me through the proof by contradiction that √5 is irrational."
        ),
        KsmkeChapter(
            chapterNumber = 9,
            titleEn = "Polynomials",
            titleKn = "ಬಹುಪದೋಕ್ತಿಗಳು",
            unitArea = "Algebra",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 171–190",
            boardMarksWeightage = 5,
            summaryEn = "Geometrical meaning of zeroes, relationship between zeroes and coefficients of quadratic/cubic polynomials, and division algorithm.",
            summaryKn = "ಬಹುಪದೋಕ್ತಿಯ ಶೂನ್ಯತೆಗಳು, ಗುಣಾಂಕಗಳ ನಡುವಿನ ಸಂಬಂಧ ಮತ್ತು ಭಾಗಾಕಾರ ಅಲ್ಗಾರಿದಂ.",
            keyFormulas = listOf(
                "Sum of zeroes: α + β = -b / a",
                "Product of zeroes: αβ = c / a",
                "Quadratic polynomial: k[x² - (α + β)x + αβ]",
                "Division algorithm: p(x) = g(x)·q(x) + r(x)"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Find Quadratic Polynomial from Zeroes",
                    problem = "Find a quadratic polynomial whose sum and product of zeroes are -3 and 2 respectively.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Given α + β = -3 and αβ = 2",
                        "Step 2 (0.5M): Formula: p(x) = x² - (α + β)x + αβ",
                        "Step 3 (1.0M): p(x) = x² - (-3)x + 2 = x² + 3x + 2"
                    ),
                    finalAnswer = "x² + 3x + 2",
                    examinerTip = "Watch the minus sign before (α + β)x!"
                )
            ),
            commonMistakes = listOf(
                "Writing x² + (α + β)x + αβ instead of minus."
            ),
            starterQuestion = "How do I find the number of zeroes from a polynomial graph?"
        ),
        KsmkeChapter(
            chapterNumber = 10,
            titleEn = "Quadratic Equations",
            titleKn = "ವರ್ಗ ಸಮೀಕರಣಗಳು",
            unitArea = "Algebra",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 191–216",
            boardMarksWeightage = 7,
            summaryEn = "Standard form ax² + bx + c = 0, factorisation, quadratic formula, discriminant Δ = b² - 4ac, and nature of roots.",
            summaryKn = "ವರ್ಗ ಸಮೀಕರಣದ ಆದರ್ಶ ರೂಪ, ಅಪವರ್ತನ ವಿಧಾನ, ಶ್ರೀಧರಾಚಾರ್ಯ ಸೂತ್ರ ಮತ್ತು ಮೂಲಗಳ ಸ್ವಭಾವ.",
            keyFormulas = listOf(
                "Standard form: ax² + bx + c = 0 (a ≠ 0)",
                "Quadratic Formula: x = [-b ± √(b² - 4ac)] / 2a",
                "Discriminant: Δ = b² - 4ac",
                "Δ > 0: Real & distinct; Δ = 0: Real & equal; Δ < 0: No real roots"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Solve 2x² - 7x + 3 = 0 by Formula",
                    problem = "Find the roots of 2x² - 7x + 3 = 0 using the quadratic formula.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Compare with ax² + bx + c = 0: a = 2, b = -7, c = 3",
                        "Step 2 (0.5M): Δ = b² - 4ac = (-7)² - 4(2)(3) = 49 - 24 = 25",
                        "Step 3 (1.0M): x = [7 ± √25] / (2×2) = (7 ± 5) / 4",
                        "Step 4 (1.0M): x₁ = 12/4 = 3, x₂ = 2/4 = ½"
                    ),
                    finalAnswer = "x = 3 or x = ½",
                    examinerTip = "Always compute Δ = b² - 4ac first in a separate line for neatness and partial marks."
                )
            ),
            commonMistakes = listOf(
                "Squaring negative b incorrectly: (-7)² = +49, NOT -49.",
                "Dividing only √Δ by 2a instead of the entire numerator (-b ± √Δ)."
            ),
            starterQuestion = "Why do we use the quadratic formula?"
        ),
        KsmkeChapter(
            chapterNumber = 11,
            titleEn = "Introduction to Trigonometry",
            titleKn = "ತ್ರಿಕೋನಮಿತಿಯ ಪ್ರಸ್ತಾವನೆ",
            unitArea = "Trigonometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 217–244",
            boardMarksWeightage = 6,
            summaryEn = "Trigonometric ratios in a right triangle, standard angles (0°, 30°, 45°, 60°, 90°), complementary angles, and trigonometric identities.",
            summaryKn = "ತ್ರಿಕೋನಮಿತಿಯ ಅನುಪಾತಗಳು, ವಿಶಿಷ್ಟ ಕೋನಗಳ ಬೆಲೆಗಳು ಮತ್ತು ತ್ರಿಕೋನಮಿತೀಯ ನಿತ್ಯಸಮೀಕರಣಗಳು.",
            keyFormulas = listOf(
                "sin θ = Opp/Hyp, cos θ = Adj/Hyp, tan θ = Opp/Adj = sin θ / cos θ",
                "sin²θ + cos²θ = 1",
                "1 + tan²θ = sec²θ",
                "1 + cot²θ = cosec²θ",
                "sin(90° - θ) = cos θ, tan(90° - θ) = cot θ"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Evaluate sin 60° cos 30° + sin 30° cos 60°",
                    problem = "Evaluate sin 60° cos 30° + sin 30° cos 60°.",
                    markingSteps = listOf(
                        "Step 1 (1.0M): Substitute exact values: sin 60° = √3/2, cos 30° = √3/2, sin 30° = ½, cos 60° = ½",
                        "Step 2 (0.5M): = (√3/2)(√3/2) + (½)(½) = 3/4 + 1/4",
                        "Step 3 (0.5M): = 4/4 = 1"
                    ),
                    finalAnswer = "1",
                    examinerTip = "Write the standard values table on the rough column before solving trig problems."
                )
            ),
            commonMistakes = listOf(
                "Writing (sin θ)² as sin θ² instead of sin²θ.",
                "Mixing up sin 30° (1/2) and cos 30° (√3/2)."
            ),
            starterQuestion = "I feel confused by Trigonometry identities — can we start from zero?"
        ),
        KsmkeChapter(
            chapterNumber = 12,
            titleEn = "Some Applications of Trigonometry",
            titleKn = "ತ್ರಿಕೋನಮಿತಿಯ ಕೆಲವು ಅನ್ವಯಗಳು",
            unitArea = "Trigonometry",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 245–258",
            boardMarksWeightage = 4,
            summaryEn = "Heights and distances using angle of elevation and angle of depression with neat labelled right-triangle diagrams.",
            summaryKn = "ಉನ್ನತ ಕೋನ ಮತ್ತು ಅವನತ ಕೋನಗಳ ಸಹಾಯದಿಂದ ಎತ್ತರ ಮತ್ತು ದೂರ ಕಂಡುಹಿಡಿಯುವುದು.",
            keyFormulas = listOf(
                "Angle of Elevation = Angle of Depression (alternate interior angles)",
                "tan θ = Height / Base (most common for tower/building problems)",
                "sin θ = Height / Slant length (ladder / kite string)"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Height of Tower from 30° Elevation",
                    problem = "The angle of elevation of the top of a tower from a point 30 m away from its foot is 30°. Find the height of the tower.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Draw right △ABC with ∠B = 90°, BC = 30 m, ∠C = 30°.",
                        "Step 2 (0.5M): tan 30° = AB / BC",
                        "Step 3 (1.0M): 1/√3 = h / 30 ⇒ h = 30/√3 = 10√3 m"
                    ),
                    finalAnswer = "10√3 m (≈ 17.32 m)",
                    examinerTip = "In 4-mark Heights & Distances questions, the diagram itself carries 1 mark!"
                )
            ),
            commonMistakes = listOf(
                "Rationalising 30/√3 wrongly or forgetting units (metres).",
                "Drawing the angle of depression inside the triangle instead of from the horizontal line of sight."
            ),
            starterQuestion = "How do I draw the correct diagram for angle of depression?"
        ),
        KsmkeChapter(
            chapterNumber = 13,
            titleEn = "Statistics",
            titleKn = "ಸಂಖ್ಯಾಶಾಸ್ತ್ರ",
            unitArea = "Statistics & Probability",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 259–292",
            boardMarksWeightage = 6,
            summaryEn = "Mean (Direct, Assumed Mean, Step-Deviation), Mode, Median of grouped data, Empirical relation, and Ogive curves.",
            summaryKn = "ವರ್ಗೀಕೃತ ದತ್ತಾಂಶಗಳ ಸರಾಸರಿ, ಬಹುಲಕ, ಮಧ್ಯಾಂಕ ಮತ್ತು ಓಜೀವ್ ರೇಖೆಗಳು.",
            keyFormulas = listOf(
                "Mean (Direct): x̄ = Σfᵢxᵢ / Σfᵢ",
                "Mode = l + [(f₁ - f₀) / (2f₁ - f₀ - f₂)] × h",
                "Median = l + [(n/2 - cf) / f] × h",
                "Empirical Relation: 3 Median = Mode + 2 Mean"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Empirical Formula Check",
                    problem = "If the mean and median of a distribution are 20 and 22 respectively, find the mode.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Empirical relation: 3 Median = Mode + 2 Mean",
                        "Step 2 (0.5M): 3(22) = Mode + 2(20) ⇒ 66 = Mode + 40",
                        "Step 3 (0.5M): Mode = 66 - 40 = 26"
                    ),
                    finalAnswer = "Mode = 26",
                    examinerTip = "3-mark Mean/Mode/Median table questions are high-scoring — double-check column sums!"
                )
            ),
            commonMistakes = listOf(
                "Using cumulative frequency of the median class instead of the PRECEDING class (cf) in the Median formula.",
                "Adding f₀ + f₂ wrongly in the Mode denominator (2f₁ - f₀ - f₂)."
            ),
            starterQuestion = "Give me a quick trick to avoid mistakes in the 3-mark Median table."
        ),
        KsmkeChapter(
            chapterNumber = 14,
            titleEn = "Probability",
            titleKn = "ಸಂಭವನೀಯತೆ",
            unitArea = "Statistics & Probability",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 293–312",
            boardMarksWeightage = 3,
            summaryEn = "Classical definition of probability P(E), complementary events P(E) + P(Ē) = 1, coins, dice, and playing cards.",
            summaryKn = "ಸಂಭವನೀಯತೆಯ ಸೂತ್ರ P(E), ಪೂರಕ ಘಟನೆಗಳು, ನಾಣ್ಯಗಳು, ದಾಳಗಳು ಮತ್ತು ಇಸ್ಪೀಟ್ ಎಲೆಗಳು.",
            keyFormulas = listOf(
                "P(E) = Number of favourable outcomes / Total number of outcomes",
                "0 ≤ P(E) ≤ 1",
                "P(E) + P(not E) = 1"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Two Dice Sum Problem",
                    problem = "Two fair dice are thrown simultaneously. Find the probability that the sum of numbers appearing is 8.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): Total outcomes n(S) = 6 × 6 = 36",
                        "Step 2 (0.5M): Favourable pairs for sum 8: {(2,6), (3,5), (4,4), (5,3), (6,2)} ⇒ n(E) = 5",
                        "Step 3 (1.0M): P(E) = 5 / 36"
                    ),
                    finalAnswer = "5/36",
                    examinerTip = "Always list n(S) and the favourable set E explicitly."
                )
            ),
            commonMistakes = listOf(
                "Giving a probability value > 1 or negative.",
                "Counting (4,4) twice when throwing two dice."
            ),
            starterQuestion = "How many face cards and red kings are in a standard 52-card deck?"
        ),
        KsmkeChapter(
            chapterNumber = 15,
            titleEn = "Surface Areas and Volumes",
            titleKn = "ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣಗಳು ಮತ್ತು ಘನಫಲಗಳು",
            unitArea = "Mensuration",
            textbookEdition = "KSEAB SSLC 2026-27",
            pageRange = "pp. 313–342",
            boardMarksWeightage = 4,
            summaryEn = "Surface area and volume of combinations of solids (cylinder + cone/hemisphere), conversion of solids, and frustum of a cone.",
            summaryKn = "ಘನಾಕೃತಿಗಳ ಸಂಯೋಜನೆಯ ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣ, ಘನಫಲ ಮತ್ತು ಶಂಕುವಿನ ಭಿನ್ನಕ.",
            keyFormulas = listOf(
                "Cylinder: CSA = 2πrh, TSA = 2πr(r + h), V = πr²h",
                "Cone: l = √(r² + h²), CSA = πrl, V = ⅓πr²h",
                "Sphere: SA = 4πr², V = (4/3)πr³",
                "Hemisphere: CSA = 2πr², TSA = 3πr², V = (2/3)πr³",
                "Frustum of Cone: V = (1/3)πh(r₁² + r₂² + r₁r₂)"
            ),
            theorems = emptyList(),
            solvedExamples = listOf(
                SolvedExample(
                    title = "Cone Mounted on Hemisphere",
                    problem = "A toy is in the form of a cone of radius 3.5 cm mounted on a hemisphere of same radius. Total height is 15.5 cm. Find its TSA.",
                    markingSteps = listOf(
                        "Step 1 (0.5M): r = 3.5 cm, height of cone h = 15.5 - 3.5 = 12 cm",
                        "Step 2 (1.0M): Slant height l = √(3.5² + 12²) = √(12.25 + 144) = √156.25 = 12.5 cm",
                        "Step 3 (1.5M): TSA = CSA(cone) + CSA(hemisphere) = πr(l + 2r) = (22/7)×3.5×(12.5 + 7) = 11 × 19.5 = 214.5 cm²"
                    ),
                    finalAnswer = "214.5 cm²",
                    examinerTip = "Take πr common before multiplying — it saves 3 minutes of decimal calculation!"
                )
            ),
            commonMistakes = listOf(
                "Taking total height instead of cone height (total - r).",
                "Using TSA instead of CSA when two solids are joined face-to-face."
            ),
            starterQuestion = "Why do we add CSA of cone and CSA of hemisphere instead of TSA?"
        )
    ).map { it.copy(detailedTopics = detailedTopicsFor(it.chapterNumber)) }

    val pyqQuestions: List<PyqQuestion> = listOf(
        // 1-Mark MCQs (2026-27 MQP-01 & 2019–2025 Board)
        PyqQuestion(
            id = "pyq_2026_mqp_01",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 1,
            topic = "Arithmetic Progressions",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "If the nth term of an arithmetic progression is aₙ = 3n + 2, then its common difference is:",
            questionKn = "ಒಂದು ಸಮಾಂತರ ಶ್ರೇಢಿಯ n-ನೇ ಪದ aₙ = 3n + 2 ಆದರೆ, ಸಾಮಾನ್ಯ ವ್ಯತ್ಯಾಸವು:",
            mcqOptions = listOf("2", "3", "5", "-3"),
            correctMcqIndex = 1,
            officialAnswer = "3",
            markingPoints = listOf("a₁ = 3(1)+2 = 5, a₂ = 3(2)+2 = 8 ⇒ d = 8 - 5 = 3 (1M)"),
            requiredMethod = "Substitute n = 1, 2 or read coefficient of n",
            commonMistakes = "Selecting the constant term 2 instead of the coefficient of n."
        ),
        PyqQuestion(
            id = "pyq_2024_board_02",
            yearLabel = "2024 SSLC Main",
            examNumber = "KSEAB 2024-81E",
            chapterNumber = 3,
            topic = "Pair of Linear Equations",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If the lines represented by 3x + 2ky = 2 and 2x + 5y + 1 = 0 are parallel, then the value of k is:",
            questionKn = "3x + 2ky = 2 ಮತ್ತು 2x + 5y + 1 = 0 ಸಮೀಕರಣಗಳ ರೇಖೆಗಳು ಸಮಾಂತರವಾಗಿದ್ದರೆ k ಬೆಲೆ:",
            mcqOptions = listOf("-5/4", "2/5", "15/4", "3/2"),
            correctMcqIndex = 2,
            officialAnswer = "15/4",
            markingPoints = listOf("Parallel condition: a₁/a₂ = b₁/b₂ ⇒ 3/2 = 2k/5 ⇒ 4k = 15 ⇒ k = 15/4 (1M)"),
            requiredMethod = "Ratio comparison a₁/a₂ = b₁/b₂ ≠ c₁/c₂",
            commonMistakes = "Solving 2k/5 = 3/2 as k = 15/2 instead of 15/4."
        ),
        PyqQuestion(
            id = "pyq_2023_board_03",
            yearLabel = "2023 SSLC Main",
            examNumber = "KSEAB 2023-81E",
            chapterNumber = 10,
            topic = "Quadratic Equations",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The discriminant of the quadratic equation 2x² - 4x + 3 = 0 is:",
            questionKn = "2x² - 4x + 3 = 0 ವರ್ಗ ಸಮೀಕರಣದ ಶೋಧಕದ ಬೆಲೆ:",
            mcqOptions = listOf("-8", "8", "40", "-40"),
            correctMcqIndex = 0,
            officialAnswer = "-8",
            markingPoints = listOf("Δ = b² - 4ac = (-4)² - 4(2)(3) = 16 - 24 = -8 (1M)"),
            requiredMethod = "Discriminant formula Δ = b² - 4ac",
            commonMistakes = "Writing (-4)² as -16 instead of +16."
        ),
        PyqQuestion(
            id = "pyq_2022_board_04",
            yearLabel = "2022 SSLC Main",
            examNumber = "KSEAB 2022-81E",
            chapterNumber = 7,
            topic = "Coordinate Geometry",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The distance of the point P(3, -4) from the origin is:",
            questionKn = "ಮೂಲ ಬಿಂದುವಿನಿಂದ P(3, -4) ಬಿಂದುವಿಗಿರುವ ದೂರ:",
            mcqOptions = listOf("7 units", "1 unit", "5 units", "25 units"),
            correctMcqIndex = 2,
            officialAnswer = "5 units",
            markingPoints = listOf("d = √(x² + y²) = √(3² + (-4)²) = √(9 + 16) = √25 = 5 units (1M)"),
            requiredMethod = "Origin distance formula √(x² + y²)",
            commonMistakes = "Forgetting to take square root and picking 25."
        ),
        PyqQuestion(
            id = "pyq_2025_board_05",
            yearLabel = "2025 SSLC Main",
            examNumber = "KSEAB 2025-81E",
            chapterNumber = 11,
            topic = "Trigonometry",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The value of 9 sec²A - 9 tan²A is equal to:",
            questionKn = "9 sec²A - 9 tan²A ಇದರ ಬೆಲೆ:",
            mcqOptions = listOf("1", "9", "8", "0"),
            correctMcqIndex = 1,
            officialAnswer = "9",
            markingPoints = listOf("9(sec²A - tan²A) = 9(1) = 9 (1M)"),
            requiredMethod = "Trigonometric identity sec²A - tan²A = 1",
            commonMistakes = "Picking 1 without multiplying by common factor 9."
        ),
        PyqQuestion(
            id = "pyq_2020_board_06",
            yearLabel = "2020 SSLC Main",
            examNumber = "KSEAB 2020-81E",
            chapterNumber = 4,
            topic = "Circles",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If tangents PA and PB from a point P to a circle with centre O are inclined to each other at angle of 80°, then ∠POA is equal to:",
            questionKn = "O ಕೇಂದ್ರವುಳ್ಳ ವೃತ್ತಕ್ಕೆ P ಬಿಂದುವಿನಿಂದ ಎಳೆದ PA ಮತ್ತು PB ಸ್ಪರ್ಶಕಗಳ ನಡುವಿನ ಕೋನ 80° ಆದರೆ ∠POA ಬೆಲೆ:",
            mcqOptions = listOf("50°", "60°", "70°", "80°"),
            correctMcqIndex = 0,
            officialAnswer = "50°",
            markingPoints = listOf("∠AOB = 180° - 80° = 100°; OP bisects ∠AOB ⇒ ∠POA = 50° (1M)"),
            requiredMethod = "Supplementary angle + congruence of △OAP and △OBP",
            commonMistakes = "Choosing 100° without halving ∠AOB."
        ),
        PyqQuestion(
            id = "pyq_2019_board_07",
            yearLabel = "2019 SSLC Main",
            examNumber = "KSEAB 2019-81E",
            chapterNumber = 14,
            topic = "Probability",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If P(E) = 0.05, what is the probability of 'not E'?",
            questionKn = "P(E) = 0.05 ಆದರೆ, 'E ಅಲ್ಲದ' ಘಟನೆಯ ಸಂಭವನೀಯತೆ ಎಷ್ಟು?",
            mcqOptions = listOf("0.5", "0.95", "0.9", "0.05"),
            correctMcqIndex = 1,
            officialAnswer = "0.95",
            markingPoints = listOf("P(Ē) = 1 - P(E) = 1 - 0.05 = 0.95 (1M)"),
            requiredMethod = "Complementary event rule P(E) + P(Ē) = 1",
            commonMistakes = "Subtracting carelessly as 0.9 instead of 0.95."
        ),
        PyqQuestion(
            id = "pyq_2026_mqp_08",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 8,
            topic = "Real Numbers",
            marks = 1,
            format = QuestionFormat.MCQ,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "If HCF(26, 91) = 13, then LCM(26, 91) is:",
            questionKn = "HCF(26, 91) = 13 ಆದರೆ, LCM(26, 91) ಎಷ್ಟು?",
            mcqOptions = listOf("182", "91", "364", "26"),
            correctMcqIndex = 0,
            officialAnswer = "182",
            markingPoints = listOf("LCM = (26 × 91) / 13 = 2 × 91 = 182 (1M)"),
            requiredMethod = "HCF × LCM = Product of two numbers",
            commonMistakes = "Multiplying 26 × 91 × 13 instead of dividing by 13."
        ),

        // 1-Mark VSA
        PyqQuestion(
            id = "pyq_2026_mqp_09",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 9,
            topic = "Polynomials",
            marks = 1,
            format = QuestionFormat.VSA_1M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "Write the degree of the polynomial p(x) = 4x³ - 3x² + 5x - 7.",
            questionKn = "p(x) = 4x³ - 3x² + 5x - 7 ಬಹುಪದೋಕ್ತಿಯ ಮಹತ್ತಮ ಘಾತವನ್ನು ಬರೆಯಿರಿ.",
            officialAnswer = "3",
            markingPoints = listOf("Highest power of variable x is 3 (1M)"),
            requiredMethod = "Identify highest exponent of x with non-zero coefficient",
            commonMistakes = "Writing the leading coefficient 4 instead of exponent 3."
        ),
        PyqQuestion(
            id = "pyq_2023_board_10",
            yearLabel = "2023 SSLC Main",
            examNumber = "KSEAB 2023-81E",
            chapterNumber = 15,
            topic = "Surface Areas and Volumes",
            marks = 1,
            format = QuestionFormat.VSA_1M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Write the formula to find the total surface area of a solid hemisphere of radius 'r' units.",
            questionKn = "'r' ತ್ರಿಜ್ಯವಿರುವ ಘನ ಅರ್ಧಗೋಳದ ಪೂರ್ಣ ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣ ಕಂಡುಹಿಡಿಯುವ ಸೂತ್ರ ಬರೆಯಿರಿ.",
            officialAnswer = "3πr² sq. units",
            markingPoints = listOf("TSA of solid hemisphere = 3πr² (1M)"),
            requiredMethod = "CSA (2πr²) + circular base (πr²) = 3πr²",
            commonMistakes = "Writing 2πr² (which is CSA, not TSA)."
        ),
        PyqQuestion(
            id = "pyq_2022_board_11",
            yearLabel = "2022 SSLC Main",
            examNumber = "KSEAB 2022-81E",
            chapterNumber = 13,
            topic = "Statistics",
            marks = 1,
            format = QuestionFormat.VSA_1M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Write the empirical relationship between the three measures of central tendency: Mean, Median and Mode.",
            questionKn = "ಸರಾಸರಿ, ಮಧ್ಯಾಂಕ ಮತ್ತು ಬಹುಲಕಗಳ ನಡುವಿನ ಪ್ರಾಯೋಗಿಕ ಸಂಬಂಧವನ್ನು ಬರೆಯಿರಿ.",
            officialAnswer = "3 Median = Mode + 2 Mean",
            markingPoints = listOf("3 Median = Mode + 2 Mean (1M)"),
            requiredMethod = "Direct formula statement",
            commonMistakes = "Swapping coefficients of Median and Mean."
        ),
        PyqQuestion(
            id = "pyq_2018_board_12",
            yearLabel = "2018 SSLC Main",
            examNumber = "KSEAB 2018-81E",
            chapterNumber = 5,
            topic = "Areas Related to Circles",
            marks = 1,
            format = QuestionFormat.VSA_1M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Write the formula to find the length of an arc of a sector of angle θ and radius r.",
            questionKn = "θ ಕೋನ ಮತ್ತು r ತ್ರಿಜ್ಯವಿರುವ ತ್ರಿಜ್ಯಾಂತರ ಖಂಡದ ಕಂಸದ ಉದ್ದದ ಸೂತ್ರ ಬರೆಯಿರಿ.",
            officialAnswer = "(θ / 360°) × 2πr",
            markingPoints = listOf("Length of arc = (θ / 360°) × 2πr (1M)"),
            requiredMethod = "Sector arc length formula",
            commonMistakes = "Writing πr² instead of 2πr."
        ),

        // 2-Mark Questions
        PyqQuestion(
            id = "pyq_2026_mqp_13",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 1,
            topic = "Arithmetic Progressions",
            marks = 2,
            format = QuestionFormat.SA_2M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "Find the 15th term of the arithmetic progression 2, 7, 12, ... using formula.",
            questionKn = "2, 7, 12, ... ಸಮಾಂತರ ಶ್ರೇಢಿಯ 15ನೇ ಪದವನ್ನು ಸೂತ್ರದ ಸಹಾಯದಿಂದ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "72",
            markingPoints = listOf(
                "Identify a = 2, d = 7 - 2 = 5, n = 15 (0.5M)",
                "Write formula aₙ = a + (n - 1)d (0.5M)",
                "Substitute: a₁₅ = 2 + (15 - 1)(5) = 2 + 70 (0.5M)",
                "Final answer: a₁₅ = 72 (0.5M)"
            ),
            requiredMethod = "nth term formula aₙ = a + (n - 1)d",
            commonMistakes = "Using n = 15 instead of (15 - 1) = 14."
        ),
        PyqQuestion(
            id = "pyq_2024_board_14",
            yearLabel = "2024 SSLC Main",
            examNumber = "KSEAB 2024-81E",
            chapterNumber = 10,
            topic = "Quadratic Equations",
            marks = 2,
            format = QuestionFormat.SA_2M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve the quadratic equation x² + 5x + 6 = 0 by quadratic formula.",
            questionKn = "x² + 5x + 6 = 0 ವರ್ಗ ಸಮೀಕರಣವನ್ನು ಸೂತ್ರದ ಸಹಾಯದಿಂದ ಬಿಡಿಸಿ.",
            officialAnswer = "x = -2 or x = -3",
            markingPoints = listOf(
                "a = 1, b = 5, c = 6 and formula x = [-b ± √(b² - 4ac)] / 2a (0.5M)",
                "Δ = 25 - 24 = 1 (0.5M)",
                "x = (-5 ± 1) / 2 (0.5M)",
                "x = -2, -3 (0.5M)"
            ),
            requiredMethod = "Quadratic formula",
            commonMistakes = "Writing +5 instead of -5 in numerator."
        ),
        PyqQuestion(
            id = "pyq_2023_board_15",
            yearLabel = "2023 SSLC Main",
            examNumber = "KSEAB 2023-81E",
            chapterNumber = 7,
            topic = "Coordinate Geometry",
            marks = 2,
            format = QuestionFormat.SA_2M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the coordinates of the midpoint of the line segment joining the points (4, -5) and (6, 3).",
            questionKn = "(4, -5) ಮತ್ತು (6, 3) ಬಿಂದುಗಳನ್ನು ಸೇರಿಸುವ ರೇಖಾಖಂಡದ ಮಧ್ಯಬಿಂದುವಿನ ನಿರ್ದೇಶಾಂಕಗಳನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "(5, -1)",
            markingPoints = listOf(
                "Formula: M = ((x₁ + x₂)/2, (y₁ + y₂)/2) (0.5M)",
                "Substitute: ((4 + 6)/2, (-5 + 3)/2) (1.0M)",
                "Result: (10/2, -2/2) = (5, -1) (0.5M)"
            ),
            requiredMethod = "Midpoint formula",
            commonMistakes = "Adding -5 + 3 wrongly as -8 or +2."
        ),
        PyqQuestion(
            id = "pyq_2021_board_16",
            yearLabel = "2021 SSLC Model",
            examNumber = "KSEAB 2021-81E",
            chapterNumber = 8,
            topic = "Real Numbers",
            marks = 2,
            format = QuestionFormat.SA_2M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that 3 + 2√5 is an irrational number (given √5 is irrational).",
            questionKn = "√5 ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆ ಎಂದು ನೀಡಿದಾಗ, 3 + 2√5 ಒಂದು ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆ ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "Contradiction proof showing √5 = (p - 3q)/(2q)",
            markingPoints = listOf(
                "Assume 3 + 2√5 = p/q is rational (q ≠ 0) (0.5M)",
                "Rearrange: 2√5 = p/q - 3 = (p - 3q)/q (0.5M)",
                "√5 = (p - 3q)/(2q) which is rational (0.5M)",
                "Contradicts given fact that √5 is irrational ⇒ 3 + 2√5 is irrational (0.5M)"
            ),
            requiredMethod = "Proof by contradiction",
            commonMistakes = "Not isolating √5 completely on LHS before concluding."
        ),
        PyqQuestion(
            id = "pyq_2025_board_17",
            yearLabel = "2025 SSLC Main",
            examNumber = "KSEAB 2025-81E",
            chapterNumber = 3,
            topic = "Pair of Linear Equations",
            marks = 2,
            format = QuestionFormat.SA_2M,
            difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve the pair of linear equations by elimination: 2x + y = 8 and x - y = 1.",
            questionKn = "ವರ್ಜಿಸುವ ವಿಧಾನದಿಂದ ಬಿಡಿಸಿ: 2x + y = 8 ಮತ್ತು x - y = 1.",
            officialAnswer = "x = 3, y = 2",
            markingPoints = listOf(
                "Add equations: 3x = 9 ⇒ x = 3 (1.0M)",
                "Substitute x = 3 in x - y = 1 ⇒ 3 - y = 1 ⇒ y = 2 (1.0M)"
            ),
            requiredMethod = "Elimination method",
            commonMistakes = "Subtracting instead of adding opposite-signed y terms."
        ),

        // 3-Mark Questions
        PyqQuestion(
            id = "pyq_2026_mqp_18",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 4,
            topic = "Circles (Theorem)",
            marks = 3,
            format = QuestionFormat.LA_3M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "Prove that 'the lengths of tangents drawn from an external point to a circle are equal'.",
            questionKn = "'ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ವೃತ್ತಕ್ಕೆ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳ ಉದ್ದಗಳು ಸಮನಾಗಿರುತ್ತವೆ' ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "RHS Congruence proof of △OQP ≅ △ORP",
            markingPoints = listOf(
                "Neat labelled figure + Data + To Prove + Construction (1.0M)",
                "Right angles ∠OQP = ∠ORP = 90°, common hypotenuse OP, equal radii OQ = OR (1.0M)",
                "RHS congruence △OQP ≅ △ORP ⇒ PQ = PR (CPCT) (1.0M)"
            ),
            requiredMethod = "RHS triangle congruence",
            commonMistakes = "Missing figure or stating SAS instead of RHS."
        ),
        PyqQuestion(
            id = "pyq_2024_board_19",
            yearLabel = "2024 SSLC Main",
            examNumber = "KSEAB 2024-81E",
            chapterNumber = 13,
            topic = "Statistics (Mode)",
            marks = 3,
            format = QuestionFormat.LA_3M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the Mode for the grouped data: Class 10-20 (f=4), 20-30 (f=8), 30-40 (f=12), 40-50 (f=6), 50-60 (f=3).",
            questionKn = "ನೀಡಲಾದ ವರ್ಗೀಕೃತ ದತ್ತಾಂಶಕ್ಕೆ ಬಹುಲಕ ಕಂಡುಹಿಡಿಯಿರಿ: 10-20(4), 20-30(8), 30-40(12), 40-50(6), 50-60(3).",
            officialAnswer = "34",
            markingPoints = listOf(
                "Modal class = 30-40, l = 30, f₁ = 12, f₀ = 8, f₂ = 6, h = 10 (1.0M)",
                "Formula: Mode = l + [(f₁ - f₀)/(2f₁ - f₀ - f₂)] × h (0.5M)",
                "Mode = 30 + [(12 - 8)/(24 - 8 - 6)] × 10 = 30 + (4/10)×10 = 34 (1.5M)"
            ),
            requiredMethod = "Grouped Mode formula",
            commonMistakes = "Taking f₀ = 12 or wrong 2f₁ calculation."
        ),
        PyqQuestion(
            id = "pyq_2023_board_20",
            yearLabel = "2023 SSLC Main",
            examNumber = "KSEAB 2023-81E",
            chapterNumber = 10,
            topic = "Quadratic Equations (Word Problem)",
            marks = 3,
            format = QuestionFormat.LA_3M,
            difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The altitude of a right triangle is 7 cm less than its base. If the hypotenuse is 13 cm, find the other two sides.",
            questionKn = "ಒಂದು ಲಂಬಕೋನ ತ್ರಿಭುಜದ ಎತ್ತರವು ಅದರ ಪಾದಕ್ಕಿಂತ 7 cm ಕಡಿಮೆಯಾಗಿದೆ. ವಿಕರ್ಣವು 13 cm ಆದರೆ ಉಳಿದ ಎರಡು ಬಾಹುಗಳನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "Base = 12 cm, Altitude = 5 cm",
            markingPoints = listOf(
                "Let base = x cm, altitude = (x - 7) cm (0.5M)",
                "By Pythagoras: x² + (x - 7)² = 13² ⇒ 2x² - 14x - 120 = 0 ⇒ x² - 7x - 60 = 0 (1.0M)",
                "Factorise: (x - 12)(x + 5) = 0 ⇒ x = 12 (reject -5) (1.0M)",
                "Base = 12 cm, Altitude = 5 cm (0.5M)"
            ),
            requiredMethod = "Quadratic modelling + Pythagoras theorem",
            commonMistakes = "Accepting negative length x = -5 or expanding (x - 7)² as x² - 49."
        ),
        PyqQuestion(
            id = "pyq_2022_board_21",
            yearLabel = "2022 SSLC Main",
            examNumber = "KSEAB 2022-81E",
            chapterNumber = 11,
            topic = "Trigonometry (Identity Proof)",
            marks = 3,
            format = QuestionFormat.LA_3M,
            difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that (sin A + cosec A)² + (cos A + sec A)² = 7 + tan²A + cot²A.",
            questionKn = "(sin A + cosec A)² + (cos A + sec A)² = 7 + tan²A + cot²A ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "7 + tan²A + cot²A",
            markingPoints = listOf(
                "Expand LHS using (a+b)²: sin²A + cosec²A + 2 + cos²A + sec²A + 2 (1.0M)",
                "Group sin²A + cos²A = 1 ⇒ 1 + 2 + 2 + cosec²A + sec²A = 5 + cosec²A + sec²A (1.0M)",
                "Substitute cosec²A = 1 + cot²A and sec²A = 1 + tan²A ⇒ 7 + tan²A + cot²A = RHS (1.0M)"
            ),
            requiredMethod = "Algebraic expansion + Pythagorean trig identities",
            commonMistakes = "Forgetting 2·sinA·cosecA = 2 and 2·cosA·secA = 2."
        ),
        PyqQuestion(
            id = "pyq_2020_board_22",
            yearLabel = "2020 SSLC Main",
            examNumber = "KSEAB 2020-81E",
            chapterNumber = 6,
            topic = "Constructions",
            marks = 3,
            format = QuestionFormat.LA_3M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Draw a circle of radius 3 cm. From a point 7 cm away from its centre, construct a pair of tangents to the circle.",
            questionKn = "3 cm ತ್ರಿಜ್ಯದ ವೃತ್ತ ಎಳೆದು, ಕೇಂದ್ರದಿಂದ 7 cm ದೂರದಲ್ಲಿರುವ ಬಿಂದುವಿನಿಂದ ವೃತ್ತಕ್ಕೆ ಎರಡು ಸ್ಪರ್ಶಕಗಳನ್ನು ರಚಿಸಿ.",
            officialAnswer = "Compass construction with perpendicular bisector of OP",
            markingPoints = listOf(
                "Circle of radius 3 cm & line OP = 7 cm (1.0M)",
                "Perpendicular bisector of OP to locate midpoint M & auxiliary circle (1.0M)",
                "Join PQ and PR cleanly with arcs retained (1.0M)"
            ),
            requiredMethod = "Compass + Straightedge perpendicular bisector",
            commonMistakes = "Measuring 7 cm from circumference instead of centre O."
        ),

        // 4-Mark & 5-Mark Board Questions
        PyqQuestion(
            id = "pyq_2026_mqp_23",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 2,
            topic = "Triangles (Thales Theorem)",
            marks = 4,
            format = QuestionFormat.LA_4M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "State and prove Basic Proportionality Theorem (Thales Theorem).",
            questionKn = "ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯವನ್ನು (ಥೇಲ್ಸ್ ಪ್ರಮೇಯ) ನಿರೂಪಿಸಿ ಮತ್ತು ಸಾಧಿಸಿ.",
            officialAnswer = "Full 5-part proof of AD/DB = AE/EC",
            markingPoints = listOf(
                "Statement (1.0M)",
                "Neat Figure, Data, To Prove, Construction (1.0M)",
                "Area ratios ar(△ADE)/ar(△BDE) and ar(△ADE)/ar(△CDE) (1.0M)",
                "Equal area reasoning on same base DE between parallels ⇒ AD/DB = AE/EC (1.0M)"
            ),
            requiredMethod = "Area ratio proof with perpendicular heights",
            commonMistakes = "Missing the formal statement when question says 'State and prove'."
        ),
        PyqQuestion(
            id = "pyq_2025_board_24",
            yearLabel = "2025 SSLC Main",
            examNumber = "KSEAB 2025-81E",
            chapterNumber = 3,
            topic = "Pair of Linear Equations (Graph)",
            marks = 4,
            format = QuestionFormat.LA_4M,
            difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve graphically: 2x + y = 6 and 2x - y = 2.",
            questionKn = "ನಕ್ಷೆಯ ವಿಧಾನದಿಂದ ಬಿಡಿಸಿ: 2x + y = 6 ಮತ್ತು 2x - y = 2.",
            officialAnswer = "x = 2, y = 2",
            markingPoints = listOf(
                "Table of values for 2x + y = 6: (0,6), (2,2), (3,0) (1.0M)",
                "Table of values for 2x - y = 2: (0,-2), (1,0), (2,2) (1.0M)",
                "Plotting lines accurately with X/Y axes and Scale (1.0M)",
                "Intersection point (2, 2) ⇒ x = 2, y = 2 (1.0M)"
            ),
            requiredMethod = "Cartesian graphing with 3 points per line",
            commonMistakes = "Plotting only 2 points or omitting Scale on graph."
        ),
        PyqQuestion(
            id = "pyq_2024_board_25",
            yearLabel = "2024 SSLC Main",
            examNumber = "KSEAB 2024-81E",
            chapterNumber = 12,
            topic = "Applications of Trigonometry",
            marks = 4,
            format = QuestionFormat.LA_4M,
            difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "From the top of a 7 m high building, the angle of elevation of the top of a cable tower is 60° and the angle of depression of its foot is 45°. Determine the height of the tower.",
            questionKn = "7 m ಎತ್ತರದ ಕಟ್ಟಡದ ಮೇಲಿನಿಂದ ಕೇಬಲ್ ಗೋಪುರದ ತುದಿಯ ಉನ್ನತ ಕೋನ 60° ಮತ್ತು ಪಾದದ ಅವನತ ಕೋನ 45° ಆಗಿದೆ. ಗೋಪುರದ ಎತ್ತರವನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "7(√3 + 1) m (≈ 19.12 m)",
            markingPoints = listOf(
                "Neat labelled diagram showing building 7 m, 45° depression, 60° elevation (1.0M)",
                "In lower right triangle: tan 45° = 7 / x ⇒ x = 7 m (1.0M)",
                "In upper right triangle: tan 60° = h / 7 ⇒ h = 7√3 m (1.0M)",
                "Total tower height = 7 + 7√3 = 7(√3 + 1) m (1.0M)"
            ),
            requiredMethod = "Two coupled right triangles with tan 45° and tan 60°",
            commonMistakes = "Stopping at h = 7√3 without adding building height 7 m."
        ),
        PyqQuestion(
            id = "pyq_2019_board_26",
            yearLabel = "2019 SSLC Main",
            examNumber = "KSEAB 2019-81E",
            chapterNumber = 1,
            topic = "Arithmetic Progressions (5M)",
            marks = 5,
            format = QuestionFormat.LA_5M,
            difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The sum of the 4th and 8th terms of an AP is 24 and the sum of the 6th and 10th terms is 44. Find the first three terms and the sum of first 10 terms of the AP.",
            questionKn = "ಒಂದು ಸಮಾಂತರ ಶ್ರೇಢಿಯ 4ನೇ ಮತ್ತು 8ನೇ ಪದಗಳ ಮೊತ್ತ 24, ಹಾಗೂ 6ನೇ ಮತ್ತು 10ನೇ ಪದಗಳ ಮೊತ್ತ 44 ಆದರೆ ಮೊದಲ ಮೂರು ಪದಗಳು ಮತ್ತು ಮೊದಲ 10 ಪದಗಳ ಮೊತ್ತ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "First three terms: -13, -8, -3; S₁₀ = 95",
            markingPoints = listOf(
                "Equation 1: (a + 3d) + (a + 7d) = 24 ⇒ a + 5d = 12 (1.0M)",
                "Equation 2: (a + 5d) + (a + 9d) = 44 ⇒ a + 7d = 22 (1.0M)",
                "Solve simultaneously: 2d = 10 ⇒ d = 5, a = -13 (1.5M)",
                "First three terms: -13, -8, -3 (0.5M)",
                "S₁₀ = 5[2(-13) + 9(5)] = 5[-26 + 45] = 95 (1.0M)"
            ),
            requiredMethod = "Simultaneous linear equations in a and d",
            commonMistakes = "Miswriting a₄ as a + 4d instead of a + 3d."
        ),
        PyqQuestion(
            id = "pyq_2026_mqp_27",
            yearLabel = "2026-27 Model Paper",
            examNumber = "81E MQP-01",
            chapterNumber = 15,
            topic = "Surface Areas & Volumes (5M)",
            marks = 5,
            format = QuestionFormat.LA_5M,
            difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "A medicine capsule is in the shape of a cylinder with two hemispheres stuck to each of its ends. The length of the entire capsule is 14 mm and the diameter of the capsule is 5 mm. Find its total surface area and volume. (Take π = 22/7)",
            questionKn = "ಒಂದು ಔಷಧಿ ಕ್ಯಾಪ್ಸೂಲ್ ಸಿಲಿಂಡರ್ ಆಕಾರದಲ್ಲಿದ್ದು ಅದರ ಎರಡೂ ತುದಿಗಳಿಗೆ ಅರ್ಧಗೋಳಗಳನ್ನು ಜೋಡಿಸಲಾಗಿದೆ. ಒಟ್ಟು ಉದ್ದ 14 mm ಮತ್ತು ವ್ಯಾಸ 5 mm ಆದರೆ, ಅದರ ಪೂರ್ಣ ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಘನಫಲ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "TSA = 220 mm², Volume ≈ 229.17 mm³",
            markingPoints = listOf(
                "Radius r = 2.5 mm; Cylinder length h = 14 - 2(2.5) = 9 mm (1.0M)",
                "TSA = CSA(cyl) + 2×CSA(hemi) = 2πrh + 4πr² = 2πr(h + 2r) (1.0M)",
                "TSA = 2×(22/7)×2.5×(9 + 5) = 2×(22/7)×2.5×14 = 220 mm² (1.5M)",
                "Volume = πr²h + (4/3)πr³ = (22/7)×6.25×(9 + 10/3) ≈ 229.17 mm³ (1.5M)"
            ),
            requiredMethod = "Combined cylinder + sphere mensuration",
            commonMistakes = "Subtracting r only once instead of 2r from capsule length."
        )
    ).map { q ->
        q.copy(conceptTag = q.conceptTag.ifBlank { canonicalConceptTagFor(q) })
    } + PyqArchivePart1.questions + PyqArchivePart2.questions

    private fun canonicalConceptTagFor(q: PyqQuestion): String = when {
        q.chapterNumber == 1 && (q.questionEn.contains("sum", true) || q.questionEn.contains("S₁₀", true)) -> "AP Sum of n Terms (Sn)"
        q.chapterNumber == 1 -> "AP nth Term Formula"
        q.chapterNumber == 2 && q.questionEn.contains("Proportionality", true) -> "Thales BPT Proof & Application"
        q.chapterNumber == 2 -> "Area Ratio of Similar Triangles"
        q.chapterNumber == 3 && q.questionEn.contains("graph", true) -> "Graphical Solution of Linear Pair"
        q.chapterNumber == 3 && q.questionEn.contains("parallel", true) -> "Consistency Ratios (a1/a2, b1/b2)"
        q.chapterNumber == 3 -> "Elimination & Substitution Method"
        q.chapterNumber == 4 -> "External Tangents Equal Proof"
        q.chapterNumber == 5 -> "Arc Length & Sector Area"
        q.chapterNumber == 6 -> "Tangents & Similar Triangle Construction"
        q.chapterNumber == 7 && q.questionEn.contains("midpoint", true) -> "Section & Midpoint Formula"
        q.chapterNumber == 7 -> "Distance Formula"
        q.chapterNumber == 8 && q.questionEn.contains("irrational", true) -> "Irrationality Contradiction Proof"
        q.chapterNumber == 8 -> "HCF × LCM Product Rule"
        q.chapterNumber == 9 -> "Zeroes & Coefficients Relation"
        q.chapterNumber == 10 && q.questionEn.contains("discriminant", true) -> "Discriminant & Nature of Roots"
        q.chapterNumber == 10 -> "Quadratic Formula & Word Problems"
        q.chapterNumber == 11 && q.questionEn.contains("Prove", true) -> "Trigonometric Identity Proofs"
        q.chapterNumber == 11 -> "Standard Angles & Identities"
        q.chapterNumber == 12 -> "Heights & Distances (Elevation/Depression)"
        q.chapterNumber == 13 && q.questionEn.contains("empirical", true) -> "Empirical Relation (Mean/Median/Mode)"
        q.chapterNumber == 13 -> "Grouped Mean, Mode & Median"
        q.chapterNumber == 14 -> "Classical & Complementary Probability"
        q.chapterNumber == 15 -> "Combined Solids & Frustum Volume"
        else -> q.topic
    }

    fun getChapter(chapterNumber: Int): KsmkeChapter =
        chapters.firstOrNull { it.chapterNumber == chapterNumber } ?: chapters.first()

    fun buildChapterQuestionPaper(chapterNumber: Int): ChapterQuestionPaper =
        KsmkeChapterPapers.buildForChapter(chapterNumber)

    fun buildExamPaper(mode: com.example.domain.ExamMode): List<PyqQuestion> {
        return when (mode) {
            com.example.domain.ExamMode.SPEED_DRILL_10 ->
                pyqQuestions.filter { it.marks == 1 }.take(4) +
                    pyqQuestions.filter { it.marks == 2 }.take(3).let { listOf(it.first()) } +
                    pyqQuestions.filter { it.marks == 4 }.take(1)
            com.example.domain.ExamMode.CHAPTER_TEST_20 ->
                pyqQuestions.filter { it.marks == 1 }.take(4) +
                    pyqQuestions.filter { it.marks == 2 }.take(3) +
                    pyqQuestions.filter { it.marks == 3 }.take(2) +
                    pyqQuestions.filter { it.marks == 4 }.take(1)
            com.example.domain.ExamMode.FULL_BOARD_80 -> {
                // Assemble full 38-question / 80-mark structure (8 MCQ + 8 VSA + 8x2M + 9x3M + 4x4M + 1x5M = 80M)
                val base = mutableListOf<PyqQuestion>()
                val mcqs = pyqQuestions.filter { it.format == QuestionFormat.MCQ }
                val vsas = pyqQuestions.filter { it.format == QuestionFormat.VSA_1M }
                val twoM = pyqQuestions.filter { it.marks == 2 }
                val threeM = pyqQuestions.filter { it.marks == 3 }
                val fourM = pyqQuestions.filter { it.marks == 4 }
                val fiveM = pyqQuestions.filter { it.marks == 5 }

                repeat(8) { i -> base.add(mcqs[i % mcqs.size].copy(id = "sim_mcq_$i")) }
                repeat(8) { i -> base.add(vsas[i % vsas.size].copy(id = "sim_vsa_$i")) }
                repeat(8) { i -> base.add(twoM[i % twoM.size].copy(id = "sim_2m_$i")) }
                repeat(9) { i -> base.add(threeM[i % threeM.size].copy(id = "sim_3m_$i")) }
                repeat(4) { i -> base.add(fourM[i % fourM.size].copy(id = "sim_4m_$i")) }
                repeat(1) { i -> base.add(fiveM[i % fiveM.size].copy(id = "sim_5m_$i")) }
                base
            }
        }
    }
}
