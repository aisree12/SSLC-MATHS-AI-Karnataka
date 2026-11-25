package com.example.data.ksmke

import com.example.domain.DifficultyLevel
import com.example.domain.PyqQuestion
import com.example.domain.QuestionFormat
import com.example.domain.QuestionSource

/**
 * Historical KSEAB SSLC Mathematics Archive (Part 2: 2022–2027 Main, Preparatory & Model Papers 81E).
 */
internal object PyqArchivePart2 {

    val questions: List<PyqQuestion> = listOf(
        PyqQuestion(
            id = "arch_2022_ch2", yearLabel = "2022 SSLC Main", examNumber = "KSEAB 2022-81E",
            chapterNumber = 2, topic = "Triangles", conceptTag = "Area Ratio of Similar Triangles",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If △ABC ~ △DEF, ar(△ABC) = 64 cm², ar(△DEF) = 121 cm², and EF = 15.4 cm, find BC.",
            questionKn = "△ABC ~ △DEF ವಿಸ್ತೀರ್ಣಗಳು 64 ಮತ್ತು 121 cm², EF = 15.4 cm ಆದರೆ BC ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "11.2 cm",
            markingPoints = listOf("(BC/EF)² = 64/121 (1M)", "BC/15.4 = 8/11 ⇒ BC = 11.2 cm (1M)"),
            requiredMethod = "Area ratio = square of side ratio",
            commonMistakes = "Forgetting square root of 64/121."
        ),
        PyqQuestion(
            id = "arch_2022_ch15", yearLabel = "2022 SSLC Main", examNumber = "KSEAB 2022-81E",
            chapterNumber = 15, topic = "Surface Areas & Volumes", conceptTag = "Combined Solids & Frustum Volume",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Two cubes each of volume 64 cm³ are joined end to end. Find the surface area of the resulting cuboid.",
            questionKn = "64 cm³ ಘನಫಲವುಳ್ಳ ಎರಡು ಘನಗಳನ್ನು ತುದಿಯಿಂದ ತುದಿಗೆ ಜೋಡಿಸಿದಾಗ ಉಂಟಾಗುವ ಆಯತಘನದ ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "160 cm²",
            markingPoints = listOf("Side a³ = 64 ⇒ a = 4 cm (1M)", "Cuboid l = 8, b = 4, h = 4 cm (0.5M)", "TSA = 2(32 + 16 + 32) = 160 cm² (1.5M)"),
            requiredMethod = "Cuboid TSA after joining cubes",
            commonMistakes = "Taking 2 × 6a² = 192 instead of subtracting hidden joined faces."
        ),
        PyqQuestion(
            id = "arch_2023_ch1", yearLabel = "2023 SSLC Main", examNumber = "KSEAB 2023-81E",
            chapterNumber = 1, topic = "Arithmetic Progressions", conceptTag = "AP Sum of n Terms (Sn)",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the sum of first 20 positive integers.",
            questionKn = "ಮೊದಲ 20 ಧನ ಪೂರ್ಣಾಂಕಗಳ ಮೊತ್ತವನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "210",
            markingPoints = listOf("Sₙ = n(n+1)/2 (1M)", "S₂₀ = 20 × 21 / 2 = 210 (1M)"),
            requiredMethod = "AP sum formula",
            commonMistakes = "Arithmetic slip 20×19/2."
        ),
        PyqQuestion(
            id = "arch_2023_ch4", yearLabel = "2023 SSLC Main", examNumber = "KSEAB 2023-81E",
            chapterNumber = 4, topic = "Circles (Theorem 4.2)", conceptTag = "External Tangents Equal Proof",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that the tangents drawn to a circle from an external point are equal in length.",
            questionKn = "ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ವೃತ್ತಕ್ಕೆ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳು ಸಮವಾಗಿರುತ್ತವೆ ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "PQ = PR proved via RHS",
            markingPoints = listOf("Data, To Prove, Construction (1M)", "RHS proof (1.5M)", "CPCT (0.5M)"),
            requiredMethod = "RHS congruence",
            commonMistakes = "Missing ∠OQP = 90° theorem citation."
        ),
        PyqQuestion(
            id = "arch_2023_ch13", yearLabel = "2023 SSLC Main", examNumber = "KSEAB 2023-81E",
            chapterNumber = 13, topic = "Statistics", conceptTag = "Grouped Median & Mode Table",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the Median of: 0-10(5), 10-20(8), 20-30(20), 30-40(15), 40-50(7), 50-60(5).",
            questionKn = "ದತ್ತಾಂಶಕ್ಕೆ ಮಧ್ಯಾಂಕ ಕಂಡುಹಿಡಿಯಿರಿ: 0-10(5)..50-60(5).",
            officialAnswer = "28.5",
            markingPoints = listOf("n=60, n/2=30; Median class 20-30 (1M)", "l=20, cf=13, f=20, h=10 (1M)", "20 + [(30-13)/20]×10 = 28.5 (1M)"),
            requiredMethod = "Grouped Median formula",
            commonMistakes = "Taking cf = 33 instead of preceding cf = 13."
        ),
        PyqQuestion(
            id = "arch_2024_ch1", yearLabel = "2024 SSLC Main", examNumber = "KSEAB 2024-81E",
            chapterNumber = 1, topic = "Arithmetic Progressions", conceptTag = "AP Sum of n Terms (Sn)",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The sum of first 7 terms of an AP is 49 and that of 17 terms is 289. Find the sum of first n terms.",
            questionKn = "ಒಂದು ಸಮಾಂತರ ಶ್ರೇಢಿಯ ಮೊದಲ 7 ಪದಗಳ ಮೊತ್ತ 49 ಮತ್ತು 17 ಪದಗಳ ಮೊತ್ತ 289 ಆದರೆ ಮೊದಲ n ಪದಗಳ ಮೊತ್ತ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "n²",
            markingPoints = listOf("a + 3d = 7 and a + 8d = 17 (1.5M)", "d = 2, a = 1 (0.5M)", "Sₙ = (n/2)[2 + 2(n-1)] = n² (1M)"),
            requiredMethod = "Simultaneous linear equations in a, d",
            commonMistakes = "Miswriting S₇ = 49 as a + 6d = 49."
        ),
        PyqQuestion(
            id = "arch_2024_ch6", yearLabel = "2024 SSLC Main", examNumber = "KSEAB 2024-81E",
            chapterNumber = 6, topic = "Constructions", conceptTag = "Similar Triangle & Tangent Construction",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Draw a circle of radius 4 cm and construct a pair of tangents inclined at 60° to each other.",
            questionKn = "4 cm ತ್ರಿಜ್ಯದ ವೃತ್ತ ಎಳೆದು ಪರಸ್ಪರ 60° ಕೋನದಲ್ಲಿರುವ ಎರಡು ಸ್ಪರ್ಶಕಗಳನ್ನು ರಚಿಸಿ.",
            officialAnswer = "120° central angle construction + 90° perpendiculars",
            markingPoints = listOf("Circle r=4 cm (0.5M)", "Central angle 180-60 = 120° (1M)", "Two 90° angles meeting at P (1.5M)"),
            requiredMethod = "Central supplementary angle method",
            commonMistakes = "Constructing 60° at centre."
        ),
        PyqQuestion(
            id = "arch_2024_ch8", yearLabel = "2024 SSLC Main", examNumber = "KSEAB 2024-81E",
            chapterNumber = 8, topic = "Real Numbers", conceptTag = "Irrationality Proof by Contradiction",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that 3 + 2√5 is an irrational number.",
            questionKn = "3 + 2√5 ಒಂದು ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆ ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "Proved by contradiction",
            markingPoints = listOf("3 + 2√5 = a/b (0.5M)", "√5 = (a - 3b)/(2b) (1M)", "Rational ≠ Irrational contradiction (0.5M)"),
            requiredMethod = "Isolate √5",
            commonMistakes = "Squaring both sides unnecessarily."
        ),
        PyqQuestion(
            id = "arch_2025_ch2", yearLabel = "2025 SSLC Main", examNumber = "KSEAB 2025-81E",
            chapterNumber = 2, topic = "Triangles (BPT)", conceptTag = "Thales BPT Proof & Application",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "State and prove Thales Theorem (Basic Proportionality Theorem).",
            questionKn = "ಥೇಲ್ಸ್ ಪ್ರಮೇಯವನ್ನು ನಿರೂಪಿಸಿ ಮತ್ತು ಸಾಧಿಸಿ.",
            officialAnswer = "AD/DB = AE/EC",
            markingPoints = listOf("Statement (1M)", "Figure & Construction (1M)", "Area ratios (1M)", "Conclusion (1M)"),
            requiredMethod = "Area ratio proof",
            commonMistakes = "Omitting 'same base DE'."
        ),
        PyqQuestion(
            id = "arch_2025_ch7", yearLabel = "2025 SSLC Main", examNumber = "KSEAB 2025-81E",
            chapterNumber = 7, topic = "Coordinate Geometry", conceptTag = "Distance & Section Formula",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the coordinates of points of trisection of the line segment joining (4, -1) and (-2, -3).",
            questionKn = "(4, -1) ಮತ್ತು (-2, -3) ಬಿಂದುಗಳನ್ನು ಸೇರಿಸುವ ರೇಖಾಖಂಡದ ತ್ರಿಭಾಗ ಬಿಂದುಗಳ ನಿರ್ದೇಶಾಂಕಗಳನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "(2, -5/3) and (0, -7/3)",
            markingPoints = listOf("Ratios 1:2 and 2:1 (0.5M)", "P(1:2) = (2, -5/3) (1.25M)", "Q(2:1) = (0, -7/3) (1.25M)"),
            requiredMethod = "Section formula",
            commonMistakes = "Using 1:3 ratio instead of 1:2."
        ),
        PyqQuestion(
            id = "arch_2025_ch10", yearLabel = "2025 SSLC Main", examNumber = "KSEAB 2025-81E",
            chapterNumber = 10, topic = "Quadratic Equations", conceptTag = "Quadratic Formula & Discriminant",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve 2x² + x - 6 = 0 using the quadratic formula.",
            questionKn = "ವರ್ಗ ಸೂತ್ರದ ಸಹಾಯದಿಂದ 2x² + x - 6 = 0 ಬಿಡಿಸಿ.",
            officialAnswer = "x = 3/2 or x = -2",
            markingPoints = listOf("Δ = 1 - 4(2)(-6) = 49 (1M)", "x = (-1 ± 7)/4 ⇒ 3/2, -2 (1M)"),
            requiredMethod = "Quadratic formula",
            commonMistakes = "Missing minus sign in -4ac when c is negative."
        ),
        PyqQuestion(
            id = "arch_2025_ch14", yearLabel = "2025 SSLC Main", examNumber = "KSEAB 2025-81E",
            chapterNumber = 14, topic = "Probability", conceptTag = "Complementary & Sample Space Probability",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "A box contains 5 red marbles, 8 white marbles and 4 green marbles. Find the probability of drawing (i) red, (ii) not green.",
            questionKn = "5 ಕೆಂಪು, 8 ಬಿಳಿ, 4 ಹಸಿರು ಗೋಲಿಗಳಿವೆ. (i) ಕೆಂಪು, (ii) ಹಸಿರು ಅಲ್ಲದ ಗೋಲಿ ಬರುವ ಸಂಭವನೀಯತೆ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "(i) 5/17, (ii) 13/17",
            markingPoints = listOf("Total n(S) = 17 (0.5M)", "P(Red)=5/17 (0.75M)", "P(Not green)=13/17 (0.75M)"),
            requiredMethod = "Favourable / Total outcomes",
            commonMistakes = "Wrong sum 5+8+4."
        ),
        PyqQuestion(
            id = "arch_2026_mqp2_ch3", yearLabel = "2026-27 Model Paper", examNumber = "81E MQP-02",
            chapterNumber = 3, topic = "Linear Equations (Graph)", conceptTag = "Graphical Pair Intersection (4M)",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "Solve graphically: x + 2y = 6 and x + y = 5.",
            questionKn = "ನಕ್ಷೆಯ ವಿಧಾನದಿಂದ ಬಿಡಿಸಿ: x + 2y = 6 ಮತ್ತು x + y = 5.",
            officialAnswer = "x = 4, y = 1",
            markingPoints = listOf("Two 3-point tables (2M)", "Axes + Scale (1M)", "Intersection (4, 1) (1M)"),
            requiredMethod = "Graphing linear equations",
            commonMistakes = "Omitting scale or integer coordinates."
        ),
        PyqQuestion(
            id = "arch_2026_mqp2_ch11", yearLabel = "2026-27 Model Paper", examNumber = "81E MQP-02",
            chapterNumber = 11, topic = "Trigonometry", conceptTag = "Standard Angles & Trig Identities",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "Prove that (1 + tan²A) / (1 + cot²A) = tan²A.",
            questionKn = "(1 + tan²A) / (1 + cot²A) = tan²A ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "tan²A",
            markingPoints = listOf("Numerator = sec²A, Denominator = cosec²A (1.5M)", "(1/cos²A)/(1/sin²A) = sin²A/cos²A = tan²A (1.5M)"),
            requiredMethod = "Pythagorean identities",
            commonMistakes = "Confusing sec²A with 1/sin²A."
        ),
        PyqQuestion(
            id = "arch_2026_mqp2_ch12", yearLabel = "2026-27 Model Paper", examNumber = "81E MQP-02",
            chapterNumber = 12, topic = "Applications of Trigonometry", conceptTag = "Elevation & Depression Diagrams",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.CHALLENGING,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "From a point on a bridge across a river, the angles of depression of the banks on opposite sides are 30° and 45°. If the bridge is 3 m above the banks, find the width of the river.",
            questionKn = "ನದಿಯ ಮೇಲಿನ 3 m ಎತ್ತರದ ಸೇತುವೆಯಿಂದ ಎದುರು ದಂಡೆಗಳ ಅವನತ ಕೋನಗಳು 30° ಮತ್ತು 45° ಆದರೆ ನದಿಯ ಅಗಲ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "3(√3 + 1) m",
            markingPoints = listOf("Labelled diagram (1M)", "tan 30° = 3/AD ⇒ AD=3√3 m (1M)", "tan 45° = 3/DB ⇒ DB=3 m (1M)", "Width = 3(√3+1) m (1M)"),
            requiredMethod = "Two adjacent right triangles",
            commonMistakes = "Subtracting AD - DB instead of adding."
        ),
        PyqQuestion(
            id = "arch_2026_mqp2_ch15", yearLabel = "2026-27 Model Paper", examNumber = "81E MQP-02",
            chapterNumber = 15, topic = "Surface Areas & Volumes", conceptTag = "Combined Solids & Frustum Volume",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.OFFICIAL_MODEL_PAPER,
            questionEn = "A solid is in the shape of a cone standing on a hemisphere with both radii equal to 1 cm and height of cone equal to its radius. Find volume in terms of π.",
            questionKn = "1 cm ತ್ರಿಜ್ಯದ ಅರ್ಧಗೋಳದ ಮೇಲೆ ಅದೇ ತ್ರಿಜ್ಯ ಮತ್ತು ಎತ್ತರದ ಶಂಕುವನ್ನು ಇರಿಸಲಾಗಿದೆ. ಘನಫಲವನ್ನು π ರೂಪದಲ್ಲಿ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "π cm³",
            markingPoints = listOf("V = (1/3)πr²h + (2/3)πr³ (1.5M)", "r=1, h=1 ⇒ π/3 + 2π/3 = π cm³ (2.5M)"),
            requiredMethod = "Combined cone + hemisphere volume",
            commonMistakes = "Substituting π = 22/7 when asked 'in terms of π'."
        ),
        PyqQuestion(
            id = "arch_2026_ch9", yearLabel = "2026 SSLC Main", examNumber = "KSEAB 2026-81E",
            chapterNumber = 9, topic = "Polynomials", conceptTag = "Zeroes & Coefficients Relation",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the zeroes of p(x) = x² - 3.",
            questionKn = "p(x) = x² - 3 ಬಹುಪದೋಕ್ತಿಯ ಶೂನ್ಯತೆಗಳನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "√3 and -√3",
            markingPoints = listOf("x² - 3 = (x - √3)(x + √3) = 0 (1M)", "Zeroes: √3, -√3 (1M)"),
            requiredMethod = "Difference of squares",
            commonMistakes = "Writing only +√3."
        ),
        PyqQuestion(
            id = "arch_2026_ch5", yearLabel = "2026 SSLC Main", examNumber = "KSEAB 2026-81E",
            chapterNumber = 5, topic = "Areas Related to Circles", conceptTag = "Sector Area & Arc Length",
            marks = 1, format = QuestionFormat.VSA_1M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Write the formula to find the length of an arc of a sector of angle p° and radius R.",
            questionKn = "p° ಕೋನ ಮತ್ತು R ತ್ರಿಜ್ಯವಿರುವ ತ್ರಿಜ್ಯಾಂತರ ಖಂಡದ ಕಂಸದ ಉದ್ದದ ಸೂತ್ರ ಬರೆಯಿರಿ.",
            officialAnswer = "(p / 360°) × 2πR",
            markingPoints = listOf("(p / 360) × 2πR (1M)"),
            requiredMethod = "Arc length formula",
            commonMistakes = "Using πR² instead of 2πR."
        )
    )
}
