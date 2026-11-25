package com.example.data.ksmke

import com.example.domain.DifficultyLevel
import com.example.domain.PyqQuestion
import com.example.domain.QuestionFormat
import com.example.domain.QuestionSource

/**
 * Historical KSEAB SSLC Mathematics Archive (Part 1: 2015–2021 Main, Preparatory & Supplementary 81E Papers).
 */
internal object PyqArchivePart1 {

    val questions: List<PyqQuestion> = listOf(
        PyqQuestion(
            id = "arch_2015_ch1", yearLabel = "2015 SSLC Main", examNumber = "KSEAB 2015-81E",
            chapterNumber = 1, topic = "Arithmetic Progressions", conceptTag = "AP nth Term Formula",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the 12th term of the AP: 5, 9, 13, 17, ...",
            questionKn = "5, 9, 13, 17, ... ಸಮಾಂತರ ಶ್ರೇಢಿಯ 12ನೇ ಪದವನ್ನು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "49",
            markingPoints = listOf("a = 5, d = 4, n = 12 (0.5M)", "a₁₂ = 5 + (12 - 1)4 = 5 + 44 = 49 (1.5M)"),
            requiredMethod = "aₙ = a + (n - 1)d",
            commonMistakes = "Using n instead of (n - 1)."
        ),
        PyqQuestion(
            id = "arch_2015_ch4", yearLabel = "2015 SSLC Main", examNumber = "KSEAB 2015-81E",
            chapterNumber = 4, topic = "Circles (Theorem 4.2)", conceptTag = "External Tangents Equal Proof",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that the lengths of tangents drawn from an external point to a circle are equal.",
            questionKn = "ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ವೃತ್ತಕ್ಕೆ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳ ಉದ್ದಗಳು ಸಮವಾಗಿರುತ್ತವೆ ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "PQ = PR via RHS congruence",
            markingPoints = listOf("Figure, Data, To Prove, Construction (1M)", "RHS congruence of △OQP & △ORP (1.5M)", "PQ = PR by CPCT (0.5M)"),
            requiredMethod = "RHS Congruence",
            commonMistakes = "Writing SAS instead of RHS."
        ),
        PyqQuestion(
            id = "arch_2016_ch2", yearLabel = "2016 SSLC Main", examNumber = "KSEAB 2016-81E",
            chapterNumber = 2, topic = "Triangles (BPT)", conceptTag = "Thales BPT Proof & Application",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "State and prove Basic Proportionality Theorem (Thales Theorem).",
            questionKn = "ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯವನ್ನು ನಿರೂಪಿಸಿ ಮತ್ತು ಸಾಧಿಸಿ.",
            officialAnswer = "AD/DB = AE/EC",
            markingPoints = listOf("Statement (1M)", "Figure, Data, Construction (1M)", "Area ratios (1M)", "Same base & parallels conclusion (1M)"),
            requiredMethod = "Area ratio proof",
            commonMistakes = "Missing perpendicular construction lines EN & DM."
        ),
        PyqQuestion(
            id = "arch_2016_ch10", yearLabel = "2016 SSLC Main", examNumber = "KSEAB 2016-81E",
            chapterNumber = 10, topic = "Quadratic Equations", conceptTag = "Quadratic Formula & Discriminant",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve x² - 5x + 6 = 0 using the quadratic formula.",
            questionKn = "ವರ್ಗ ಸೂತ್ರದಿಂದ ಬಿಡಿಸಿ: x² - 5x + 6 = 0.",
            officialAnswer = "x = 3 or x = 2",
            markingPoints = listOf("a=1, b=-5, c=6; Δ = 25 - 24 = 1 (1M)", "x = (5 ± 1)/2 ⇒ 3, 2 (1M)"),
            requiredMethod = "Quadratic formula",
            commonMistakes = "Sign error in -b."
        ),
        PyqQuestion(
            id = "arch_2017_ch8", yearLabel = "2017 SSLC Main", examNumber = "KSEAB 2017-81E",
            chapterNumber = 8, topic = "Real Numbers", conceptTag = "Irrationality Proof by Contradiction",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Prove that √2 is an irrational number.",
            questionKn = "√2 ಒಂದು ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆ ಎಂದು ಸಾಧಿಸಿ.",
            officialAnswer = "Proved by contradiction",
            markingPoints = listOf("Assume √2 = p/q coprime (0.5M)", "2 divides p² ⇒ p; 2 divides q² ⇒ q (1M)", "Contradiction (0.5M)"),
            requiredMethod = "Proof by contradiction",
            commonMistakes = "Omitting 'p and q are coprime'."
        ),
        PyqQuestion(
            id = "arch_2017_ch13", yearLabel = "2017 SSLC Main", examNumber = "KSEAB 2017-81E",
            chapterNumber = 13, topic = "Statistics", conceptTag = "Grouped Median & Mode Table",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the Mean of: 10-20(2), 20-30(3), 30-40(5), 40-50(7), 50-60(3).",
            questionKn = "ದತ್ತಾಂಶದ ಸರಾಸರಿ ಕಂಡುಹಿಡಿಯಿರಿ: 10-20(2), 20-30(3), 30-40(5), 40-50(7), 50-60(3).",
            officialAnswer = "38",
            markingPoints = listOf("xᵢ = 15,25,35,45,55 (1M)", "Σfᵢxᵢ = 760, Σfᵢ = 20 (1M)", "x̄ = 760/20 = 38 (1M)"),
            requiredMethod = "Direct Mean Formula",
            commonMistakes = "Multiplying lower limit instead of class mark xᵢ."
        ),
        PyqQuestion(
            id = "arch_2018_ch3", yearLabel = "2018 SSLC Main", examNumber = "KSEAB 2018-81E",
            chapterNumber = 3, topic = "Linear Equations (Graph)", conceptTag = "Graphical Pair Intersection (4M)",
            marks = 4, format = QuestionFormat.LA_4M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Solve graphically: x + y = 7 and 3x - y = 1.",
            questionKn = "ನಕ್ಷಾ ವಿಧಾನದಿಂದ ಬಿಡಿಸಿ: x + y = 7 ಮತ್ತು 3x - y = 1.",
            officialAnswer = "x = 2, y = 5",
            markingPoints = listOf("Table 1 & Table 2 (2M)", "Accurate lines + Scale (1M)", "Intersection (2, 5) (1M)"),
            requiredMethod = "Graph plotting",
            commonMistakes = "Missing Scale on graph."
        ),
        PyqQuestion(
            id = "arch_2018_ch7", yearLabel = "2018 SSLC Main", examNumber = "KSEAB 2018-81E",
            chapterNumber = 7, topic = "Coordinate Geometry", conceptTag = "Distance & Section Formula",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the coordinates of midpoint joining (4, -5) and (6, 3).",
            questionKn = "(4, -5) ಮತ್ತು (6, 3) ಬಿಂದುಗಳನ್ನು ಸೇರಿಸುವ ರೇಖಾಖಂಡದ ಮಧ್ಯಬಿಂದು ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "(5, -1)",
            markingPoints = listOf("M = ((x₁+x₂)/2, (y₁+y₂)/2) (0.5M)", "x = (4+6)/2 = 5, y = (-5+3)/2 = -1 (1.5M)"),
            requiredMethod = "Midpoint formula",
            commonMistakes = "Subtracting instead of adding coordinates."
        ),
        PyqQuestion(
            id = "arch_2018_ch11", yearLabel = "2018 SSLC Main", examNumber = "KSEAB 2018-81E",
            chapterNumber = 11, topic = "Trigonometry", conceptTag = "Standard Angles & Trig Identities",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If sin A = 3/5, find cos A and tan A.",
            questionKn = "sin A = 3/5 ಆದರೆ, cos A ಮತ್ತು tan A ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "cos A = 4/5, tan A = 3/4",
            markingPoints = listOf("Opp=3k, Hyp=5k ⇒ Adj=4k (1M)", "cos A = 4/5, tan A = 3/4 (1M)"),
            requiredMethod = "Right triangle Pythagoras + ratios",
            commonMistakes = "Swapping Opp and Adj."
        ),
        PyqQuestion(
            id = "arch_2019_ch6", yearLabel = "2019 SSLC Main", examNumber = "KSEAB 2019-81E",
            chapterNumber = 6, topic = "Constructions", conceptTag = "Similar Triangle & Tangent Construction",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Construct a triangle of sides 4 cm, 5 cm, 6 cm and a similar triangle with scale factor 2/3.",
            questionKn = "4 cm, 5 cm, 6 cm ಬಾಹುಗಳ ತ್ರಿಭುಜಕ್ಕೆ 2/3 ರಷ್ಟು ಸಮರೂಪ ತ್ರಿಭುಜ ರಚಿಸಿ.",
            officialAnswer = "Compass construction inside original triangle",
            markingPoints = listOf("Base triangle (1M)", "3 arcs on acute ray, join B₃C (1M)", "Draw B₂C' || B₃C and C'A' || CA (1M)"),
            requiredMethod = "Compass scale-factor construction",
            commonMistakes = "Joining numerator arc B₂ instead of denominator B₃."
        ),
        PyqQuestion(
            id = "arch_2019_ch12", yearLabel = "2019 SSLC Main", examNumber = "KSEAB 2019-81E",
            chapterNumber = 12, topic = "Applications of Trigonometry", conceptTag = "Elevation & Depression Diagrams",
            marks = 3, format = QuestionFormat.LA_3M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "A ladder 15 m long just reaches the top of a vertical wall making an angle of 60° with the wall. Find the height of the wall.",
            questionKn = "15 m ಉದ್ದದ ಏಣಿಯು ಗೋಡೆಯೊಂದಿಗೆ 60° ಕೋನ ಮಾಡುತ್ತಾ ಗೋಡೆಯ ತುದಿಯನ್ನು ತಲುಪುತ್ತದೆ. ಗೋಡೆಯ ಎತ್ತರ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "7.5 m",
            markingPoints = listOf("Diagram with 60° at wall top (1M)", "cos 60° = h/15 ⇒ h = 15×(1/2) = 7.5 m (2M)"),
            requiredMethod = "Right triangle cosine ratio",
            commonMistakes = "Putting 60° on the ground instead of with the wall."
        ),
        PyqQuestion(
            id = "arch_2020_ch5", yearLabel = "2020 SSLC Main", examNumber = "KSEAB 2020-81E",
            chapterNumber = 5, topic = "Areas Related to Circles", conceptTag = "Sector Area & Arc Length",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find the area of a quadrant of a circle whose circumference is 22 cm. (π = 22/7)",
            questionKn = "22 cm ಸುತ್ತಳತೆ ಇರುವ ವೃತ್ತದ ಚತುರ್ಥ ಭಾಗದ ವಿಸ್ತೀರ್ಣ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "77/8 cm² (9.625 cm²)",
            markingPoints = listOf("2πr = 22 ⇒ r = 7/2 cm (1M)", "Area = (1/4)πr² = 77/8 cm² (1M)"),
            requiredMethod = "Circumference -> Radius -> Quadrant area",
            commonMistakes = "Taking diameter = 7 as radius."
        ),
        PyqQuestion(
            id = "arch_2020_ch10", yearLabel = "2020 SSLC Main", examNumber = "KSEAB 2020-81E",
            chapterNumber = 10, topic = "Quadratic Equations", conceptTag = "Discriminant & Equal Roots (Δ = 0)",
            marks = 2, format = QuestionFormat.SA_2M, difficulty = DifficultyLevel.MEDIUM,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "Find k so that x² + kx + 9 = 0 has two equal real roots.",
            questionKn = "x² + kx + 9 = 0 ಸಮೀಕರಣವು ಸಮ ಮೂಲಗಳನ್ನು ಹೊಂದಿದ್ದರೆ k ಬೆಲೆ ಕಂಡುಹಿಡಿಯಿರಿ.",
            officialAnswer = "k = ±6",
            markingPoints = listOf("Δ = k² - 4(1)(9) = 0 (1M)", "k² = 36 ⇒ k = ±6 (1M)"),
            requiredMethod = "b² - 4ac = 0",
            commonMistakes = "Writing only k = 6 instead of ±6."
        ),
        PyqQuestion(
            id = "arch_2021_ch1", yearLabel = "2021 SSLC Exam", examNumber = "KSEAB 2021-81E",
            chapterNumber = 1, topic = "Arithmetic Progressions", conceptTag = "AP nth Term Formula",
            marks = 1, format = QuestionFormat.MCQ, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "If a = 10 and d = 10, then the 4th term of the AP is:",
            questionKn = "a = 10 ಮತ್ತು d = 10 ಆದರೆ, ಸಮಾಂತರ ಶ್ರೇಢಿಯ 4ನೇ ಪದ:",
            mcqOptions = listOf("20", "30", "40", "50"),
            correctMcqIndex = 2,
            officialAnswer = "40",
            markingPoints = listOf("a₄ = 10 + 3(10) = 40 (1M)"),
            requiredMethod = "a + 3d",
            commonMistakes = "Computing a + 4d = 50."
        ),
        PyqQuestion(
            id = "arch_2021_ch9", yearLabel = "2021 SSLC Exam", examNumber = "KSEAB 2021-81E",
            chapterNumber = 9, topic = "Polynomials", conceptTag = "Zeroes & Coefficients Relation",
            marks = 1, format = QuestionFormat.MCQ, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "The sum of the zeroes of the polynomial p(x) = x² - 5x + 6 is:",
            questionKn = "p(x) = x² - 5x + 6 ಬಹುಪದೋಕ್ತಿಯ ಶೂನ್ಯತೆಗಳ ಮೊತ್ತ:",
            mcqOptions = listOf("-5", "5", "6", "-6"),
            correctMcqIndex = 1,
            officialAnswer = "5",
            markingPoints = listOf("α + β = -b/a = -(-5)/1 = 5 (1M)"),
            requiredMethod = "-b/a",
            commonMistakes = "Sign slip (-5)."
        ),
        PyqQuestion(
            id = "arch_2021_ch14", yearLabel = "2021 SSLC Exam", examNumber = "KSEAB 2021-81E",
            chapterNumber = 14, topic = "Probability", conceptTag = "Complementary & Sample Space Probability",
            marks = 1, format = QuestionFormat.MCQ, difficulty = DifficultyLevel.EASY,
            source = QuestionSource.HISTORICAL_BOARD,
            questionEn = "A die is thrown once. The probability of getting a prime number is:",
            questionKn = "ಒಂದು ದಾಳವನ್ನು ಒಮ್ಮೆ ಎಸೆದಾಗ ಅವಿಭಾಜ್ಯ ಸಂಖ್ಯೆ ಬರುವ ಸಂಭವನೀಯತೆ:",
            mcqOptions = listOf("1/6", "1/3", "1/2", "2/3"),
            correctMcqIndex = 2,
            officialAnswer = "1/2",
            markingPoints = listOf("Primes on die = {2, 3, 5} ⇒ 3/6 = 1/2 (1M)"),
            requiredMethod = "P(E) = n(E)/n(S)",
            commonMistakes = "Counting 1 as a prime number."
        )
    )
}
