package com.example.data.ksmke

import com.example.domain.SolvedExample

data class BilingualDefinition(
    val termEn: String,
    val termKn: String,
    val definitionEn: String,
    val pageRef: String
)

data class ExerciseCoverageRecord(
    val exerciseId: String,
    val pageRef: String,
    val subquestionsSummary: String,
    val subquestionCount: Int,
    val keyConceptTested: String,
    val verifiedPrototypeAnswer: String
)

data class DiagramRepresentationSpec(
    val diagramId: String,
    val title: String,
    val pageRef: String,
    val renderingType: String,
    val keyLabels: String
)

data class ChapterTextbookSupplement(
    val chapterNumber: Int,
    val startPage: Int,
    val endPage: Int,
    val definitions: List<BilingualDefinition>,
    val exercises: List<ExerciseCoverageRecord>,
    val diagrams: List<DiagramRepresentationSpec>,
    val supplementaryWorkedExample: SolvedExample,
    val priorAuditGapsFound: List<String>,
    val gapResolutionSummary: String
)

/**
 * Curated supplementary textbook inventory filling definitions, exercise-by-exercise subquestion
 * guides (Ex 1.1–Ex 15.4), bilingual glossary items, and diagram specs across pp. 1–342
 * for the Karnataka KTBS Revised 2026–27 Class 10 Mathematics curriculum.
 */
object KsmkeSupplementLibrary {

    val byChapter: Map<Int, ChapterTextbookSupplement> = listOf(
        ChapterTextbookSupplement(
            chapterNumber = 1,
            startPage = 1,
            endPage = 26,
            definitions = listOf(
                BilingualDefinition("Arithmetic Progression (AP)", "ಸಮಾಂತರ ಶ್ರೇಢಿ", "A list of numbers where each term differs from its predecessor by a fixed constant d.", "p. 2"),
                BilingualDefinition("Common Difference (d)", "ಸಾಮಾನ್ಯ ವ್ಯತ್ಯಾಸ", "Fixed constant d = aₖ₊₁ - aₖ; can be positive, zero, or negative.", "p. 3"),
                BilingualDefinition("Finite & Infinite AP", "ಪರಿಮಿತ ಮತ್ತು ಅಪರಿಮಿತ ಶ್ರೇಢಿ", "An AP having a last term l is finite; an AP continuing endlessly has no last term.", "p. 4")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 1.1", "pp. 6–7", "Q1(i–iv), Q2(i–v), Q3(i–iv), Q4(i–xv)", 28, "Checking AP condition, identifying a and d, writing next 3 terms", "d constant ⇒ AP"),
                ExerciseCoverageRecord("Ex 1.2", "pp. 14–15", "Q1(i–v), Q2(i–ii), Q3(i–v), Q4–Q20", 30, "aₙ = a + (n-1)d, missing AP boxes, multiples of 4/7, nth term from end", "a₂₀ = 98 (for 3,8,13,...)"),
                ExerciseCoverageRecord("Ex 1.3", "pp. 23–25", "Q1(i–iv), Q2(i–iii), Q3(i–x), Q4–Q20", 34, "Sum Sₙ = (n/2)[2a+(n-1)d], solving quadratic for n, word problems (logs, potatoes, savings)", "Sₙ = 636 ⇒ n = 12"),
                ExerciseCoverageRecord("Ex 1.4 (Optional)", "p. 26", "Q1–Q5", 5, "First negative term, ladder rungs & house number sum problem", "Check aₙ < 0")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D1.1", "Staircase / Ladder Rung Constant-Step Visualization", "pp. 1, 25", "Number-line step progression & word model", "Step height = a + (n-1)d")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 1.2 & Ex 1.3 Combined: Find AP Term from Sₙ",
                problem = "If the sum of the first n terms of an AP is 4n - n², find the 10th term a₁₀.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Sₙ = 4n - n² ⇒ S₉ = 4(9) - 9² = 36 - 81 = -45",
                    "Step 2 (0.5M): S₁₀ = 4(10) - 10² = 40 - 100 = -60",
                    "Step 3 (1.0M): a₁₀ = S₁₀ - S₉ = -60 - (-45) = -15"
                ),
                finalAnswer = "a₁₀ = -15",
                examinerTip = "Use aₙ = Sₙ - Sₙ₋₁ directly to avoid finding a and d separately."
            ),
            priorAuditGapsFound = listOf("Ex 1.1–1.4 subquestion matrix and aₙ = Sₙ - Sₙ₋₁ worked prototype were missing."),
            gapResolutionSummary = "Added full Ex 1.1–1.4 subquestion coverage (97 subparts indexed), bilingual glossary, and Sₙ - Sₙ₋₁ proof example."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 2,
            startPage = 27,
            endPage = 58,
            definitions = listOf(
                BilingualDefinition("Similar Polygons", "ಸಮರೂಪ ಬಹುಭುಜಾಕೃತಿಗಳು", "Same number of sides, equal corresponding angles, and proportional corresponding sides.", "p. 28"),
                BilingualDefinition("Basic Proportionality Theorem", "ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯ", "A line parallel to one side of a triangle divides the other two sides proportionally.", "p. 32"),
                BilingualDefinition("AA / SAS / SSS Similarity", "ಕೋ.ಕೋ. / ಬಾ.ಕೋ.ಬಾ. / ಬಾ.ಬಾ.ಬಾ. ಸಮರೂಪತೆ", "Standard board criteria to establish △ABC ~ △DEF.", "pp. 41–46")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 2.1", "p. 30", "Q1(i–iv), Q2(i–ii), Q3", 7, "Congruent vs similar figures & non-similar shapes", "All circles/squares are similar"),
                ExerciseCoverageRecord("Ex 2.2", "pp. 37–39", "Q1(i–ii), Q2(i–iii), Q3–Q10", 13, "BPT & Converse of BPT, trapezium diagonal ratio", "AD/DB = AE/EC"),
                ExerciseCoverageRecord("Ex 2.3", "pp. 47–50", "Q1(i–vi), Q2–Q16", 21, "AA/SAS/SSS proofs, pole-shadow, median similarity", "△ABC ~ △PQR"),
                ExerciseCoverageRecord("Ex 2.4", "pp. 53–54", "Q1–Q9", 9, "Area Ratio Theorem (square of side ratios)", "ar₁/ar₂ = (s₁/s₂)²"),
                ExerciseCoverageRecord("Ex 2.5", "pp. 56–58", "Q1(i–iv), Q2–Q17", 20, "Pythagoras theorem & right-triangle altitude proofs", "AC² = AB² + BC²")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D2.1", "Interactive Thales BPT Sliding Parallel Line", "p. 32", "Interactive Canvas (Math Blackboard Tab 3)", "△ABC, DE || BC, diagonals BE & CD"),
                DiagramRepresentationSpec("D2.2", "Right Triangle Altitude Diagram for Pythagoras Proof", "p. 55", "Structured Geometric Diagram", "∠B=90°, BD ⊥ AC")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 2.4 Board Problem: Area Ratio Application",
                problem = "△ABC ~ △DEF and their areas are 64 cm² and 121 cm². If EF = 15.4 cm, find BC.",
                markingSteps = listOf(
                    "Step 1 (0.5M): By Area Ratio Theorem: ar(△ABC)/ar(△DEF) = (BC/EF)²",
                    "Step 2 (1.0M): 64/121 = (BC/15.4)² ⇒ Taking square root: 8/11 = BC/15.4",
                    "Step 3 (0.5M): BC = (8 × 15.4)/11 = 8 × 1.4 = 11.2 cm"
                ),
                finalAnswer = "BC = 11.2 cm",
                examinerTip = "Take square roots cleanly BEFORE multiplying 15.4 × 8."
            ),
            priorAuditGapsFound = listOf("Ex 2.4 Area Ratio numerical and Ex 2.1–2.5 subquestion counts were missing."),
            gapResolutionSummary = "Added Ex 2.1–2.5 full breakdown, Area Ratio solved example, and live Thales Canvas diagram."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 3,
            startPage = 59,
            endPage = 88,
            definitions = listOf(
                BilingualDefinition("Consistent Pair", "ಸ್ಥಿರ ರೇಖಾತ್ಮಕ ಜೋಡಿ", "A pair of linear equations having at least one solution (unique or infinite).", "p. 65"),
                BilingualDefinition("Inconsistent Pair", "ಅಸ್ಥಿರ ಜೋಡಿ", "Parallel lines having no common solution (a₁/a₂ = b₁/b₂ ≠ c₁/c₂).", "p. 66")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 3.1", "p. 63", "Q1, Q2, Q3", 3, "Forming linear equations algebraically & graphically (Aftab age, bats/balls)", "2 equations in x, y"),
                ExerciseCoverageRecord("Ex 3.2", "pp. 68–70", "Q1(i–ii), Q2(i–iii), Q3(i–v), Q4(i–iv), Q5–Q7", 17, "Ratio check & 4-mark graphical intersection", "P(x, y) intersection"),
                ExerciseCoverageRecord("Ex 3.3", "p. 75", "Q1(i–vi), Q2, Q3(i–vi)", 13, "Substitution method & fraction/taxi fare/age word problems", "Unique (x, y)"),
                ExerciseCoverageRecord("Ex 3.4", "p. 79", "Q1(i–iv), Q2(i–v)", 9, "Elimination method & two-digit / lending library problems", "Same sign subtract"),
                ExerciseCoverageRecord("Ex 3.5", "pp. 84–88", "Q1(i–iv), Q2(i–ii), Q3, Q4(i–v), Ex 3.6 Q1–Q2", 16, "Cross-multiplication & reducible equations (boat upstream/downstream)", "u ± v speed equations")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D3.1", "Interactive Two-Line Cartesian Intersection Graph", "pp. 64–69", "Interactive Canvas (Math Blackboard Tab 1)", "X/Y grid, Line 1, Line 2, Intersection P")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 3.6 Reducible Boat Upstream & Downstream",
                problem = "Ritu rows downstream 20 km in 2 h and upstream 4 km in 2 h. Find her speed in still water and speed of current.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Let still-water speed = u km/h, stream = v km/h.",
                    "Step 2 (1.0M): Downstream u + v = 20/2 = 10 ...(1); Upstream u - v = 4/2 = 2 ...(2).",
                    "Step 3 (1.0M): Add (1)+(2): 2u = 12 ⇒ u = 6 km/h; v = 10 - 6 = 4 km/h."
                ),
                finalAnswer = "Boat speed = 6 km/h, Stream = 4 km/h",
                examinerTip = "Downstream adds speeds (u + v); upstream subtracts (u - v)."
            ),
            priorAuditGapsFound = listOf("Ex 3.1–3.6 subquestion breakdown & upstream/downstream worked example were missing."),
            gapResolutionSummary = "Indexed all 58 subquestions across Ex 3.1–3.6 and verified boat speed problem."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 4,
            startPage = 89,
            endPage = 104,
            definitions = listOf(
                BilingualDefinition("Tangent to a Circle", "ವೃತ್ತದ ಸ್ಪರ್ಶಕ", "A line intersecting a circle at only one point (the point of contact).", "p. 90"),
                BilingualDefinition("Secant of a Circle", "ಛೇದಕ ರೇಖೆ", "A line intersecting a circle at two distinct points.", "p. 90")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 4.1", "pp. 94–95", "Q1, Q2(i–iv), Q3, Q4", 7, "Tangent properties, infinitely many tangents, two parallel tangents, Pythagoras Q3", "PQ = √119 cm"),
                ExerciseCoverageRecord("Ex 4.2", "pp. 102–104", "Q1–Q13", 13, "Thm 4.2, concentric circles chord, circumscribed quadrilateral, parallelogram = rhombus, △ABC sides", "PQ = PR, AB+CD=AD+BC")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D4.1", "Interactive External Point Circle Tangents (Thm 4.1 & 4.2)", "pp. 92–97", "Interactive Canvas (Math Blackboard Tab 4)", "Centre O, OP, radii OQ ⊥ PQ, OR ⊥ PR")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 4.2 Q7 Concentric Circles Chord Length",
                problem = "Two concentric circles have radii 5 cm and 3 cm. Find the length of the chord of the larger circle touching the smaller circle.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Chord AB of larger circle is tangent to smaller circle at P ⇒ OP ⊥ AB (Thm 4.1).",
                    "Step 2 (1.0M): In right △OPA, OA = 5 cm, OP = 3 cm ⇒ AP = √(5² - 3²) = √16 = 4 cm.",
                    "Step 3 (0.5M): Since perpendicular from centre bisects chord: AB = 2·AP = 8 cm."
                ),
                finalAnswer = "8 cm",
                examinerTip = "Multiply AP by 2 at the end — students often stop at 4 cm!"
            ),
            priorAuditGapsFound = listOf("Ex 4.1–4.2 question coverage matrix and concentric circles chord worked proof were missing."),
            gapResolutionSummary = "Filled Ex 4.1–4.2 (20 questions), concentric circles solved example, and live tangent Canvas."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 5,
            startPage = 105,
            endPage = 122,
            definitions = listOf(
                BilingualDefinition("Sector of a Circle", "ತ್ರಿಜ್ಯಾಂತರ ಖಂಡ", "Region enclosed by two radii and the corresponding arc.", "p. 108"),
                BilingualDefinition("Segment of a Circle", "ವೃತ್ತಖಂಡ", "Region enclosed between a chord and its corresponding arc.", "p. 109")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 5.1", "p. 107", "Q1–Q5", 5, "Combined circle circumference & area, car wheel revolutions", "4375 revolutions"),
                ExerciseCoverageRecord("Ex 5.2", "pp. 113–115", "Q1–Q14", 14, "Sector area, arc length, clock minute hand, horse grazing, brooch wire", "Sector = (θ/360)πr²"),
                ExerciseCoverageRecord("Ex 5.3", "pp. 118–122", "Q1–Q16", 16, "Shaded regions inside squares, quadrants, semicircles, concentric arcs", "Outer - Inner area")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D5.1", "Sector vs Segment Decomposition Diagram", "p. 109", "Annotated Geometric Reference", "Centre O, radii OA, OB, chord AB, arc APB")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 5.3 Shaded Region Between Square & Semicircles",
                problem = "Find the area of the shaded region in a square ABCD of side 14 cm if two semicircles APD and BPC are drawn inside it.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Area of square ABCD = 14 × 14 = 196 cm².",
                    "Step 2 (1.0M): Diameter of each semicircle = 14 cm ⇒ r = 7 cm. Area of 2 semicircles = πr² = (22/7)×7×7 = 154 cm².",
                    "Step 3 (0.5M): Shaded Area = 196 - 154 = 42 cm²."
                ),
                finalAnswer = "42 cm²",
                examinerTip = "Combine two equal semicircles directly into ONE full circle πr²."
            ),
            priorAuditGapsFound = listOf("Ex 5.1–5.3 subquestion matrix and Ex 5.3 shaded square-semicircle prototype were missing."),
            gapResolutionSummary = "Added Ex 5.1–5.3 (35 questions), shaded region example, and MathVerificationEngine sector/segment check."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 6,
            startPage = 123,
            endPage = 134,
            definitions = listOf(
                BilingualDefinition("Scale Factor", "ಅನುಪಾತ ಅಪವರ್ತನ", "Ratio of sides of constructed similar triangle to corresponding sides of given triangle.", "p. 125"),
                BilingualDefinition("Auxiliary Semicircle Tangent Method", "ಅರ್ಧವೃತ್ತ ಸ್ಪರ್ಶಕ ರಚನೆ", "Joining midpoint M of OP to draw circle cutting given circle at tangent points Q and R.", "p. 131")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 6.1", "pp. 128–129", "Q1–Q7", 7, "Line division 5:8, similar triangles with scale factors 2/3, 5/3, 7/5, 3/4, 4/3", "Compass arcs visible"),
                ExerciseCoverageRecord("Ex 6.2", "pp. 133–134", "Q1–Q7", 7, "Circle tangents from 10 cm external point, concentric circle tangent, 60° inclined tangents", "OP bisected at M")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D6.1", "Step-by-Step Line Division (3:2) Compass Construction", "p. 124", "Interactive Canvas (Math Blackboard Tab 5)", "Segment AB, acute ray AX, arcs A₁..A₅, A₃P || A₅B")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 6.1 Q1 Divide Segment 7.6 cm in Ratio 5:8",
                problem = "Draw a line segment AB = 7.6 cm and divide it in the ratio 5 : 8. Measure the two parts.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Draw AB = 7.6 cm and acute ray AX below AB.",
                    "Step 2 (1.0M): Mark 5 + 8 = 13 equidistant compass arcs A₁..A₁₃ on AX.",
                    "Step 3 (1.5M): Join A₁₃B; draw A₅P || A₁₃B cutting AB at P ⇒ AP ≈ 2.9 cm, PB ≈ 4.7 cm."
                ),
                finalAnswer = "AP ≈ 2.9 cm, PB ≈ 4.7 cm",
                examinerTip = "Verify AP + PB = 7.6 cm and AP/PB ≈ 5/8 = 0.625."
            ),
            priorAuditGapsFound = listOf("Ex 6.1 Q1 line-division worked steps and Ex 6.2 Q1–Q7 mapping were incomplete."),
            gapResolutionSummary = "Added Ex 6.1–6.2 full exercise table, line-division worked steps, and interactive Constructions Canvas."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 7,
            startPage = 135,
            endPage = 154,
            definitions = listOf(
                BilingualDefinition("Abscissa & Ordinate", "x-ನಿರ್ದೇಶಾಂಕ ಮತ್ತು y-ನಿರ್ದೇಶಾಂಕ", "x-coordinate (distance from Y-axis) and y-coordinate (distance from X-axis).", "p. 136"),
                BilingualDefinition("Collinear Points", "ರೇಖಾಗತ ಬಿಂದುಗಳು", "Three points lying on the same straight line (Area of triangle = 0).", "p. 151")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 7.1", "pp. 142–143", "Q1(i–iii), Q2–Q10", 12, "Distance formula, square/rhombus/isosceles check, equidistant point on X-axis", "d = √[(x₂-x₁)²+(y₂-y₁)²]"),
                ExerciseCoverageRecord("Ex 7.2", "pp. 148–149", "Q1–Q10", 10, "Internal section ratio, trisection points, ratio k:1 on X-axis, parallelogram 4th vertex, rhombus area", "P(1, 3)"),
                ExerciseCoverageRecord("Ex 7.3", "pp. 152–154", "Q1(i–ii), Q2(i–ii), Q3–Q5", 7, "Area of triangle & collinearity unknown k, area of quadrilateral by diagonal split", "k = 4 when collinear")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D7.1", "Cartesian Right Triangle for Distance & Section Formula", "pp. 137, 145", "Coordinate Plane Proof Diagram", "P(x₁,y₁), Q(x₂,y₂), horizontal Δx, vertical Δy")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 7.3 Q2 Collinearity Using Triangle Area Formula",
                problem = "Find the value of k if the points A(7, -2), B(5, 1), C(3, k) are collinear.",
                markingSteps = listOf(
                    "Step 1 (0.5M): For collinear points, Area(△ABC) = 0.",
                    "Step 2 (1.0M): ½|7(1 - k) + 5(k + 2) + 3(-2 - 1)| = 0",
                    "Step 3 (0.5M): |7 - 7k + 5k + 10 - 9| = 0 ⇒ |8 - 2k| = 0 ⇒ k = 4"
                ),
                finalAnswer = "k = 4",
                examinerTip = "Keep signs neat inside (y₃ - y₁) when y₁ is negative."
            ),
            priorAuditGapsFound = listOf("Ex 7.3 collinearity area example and full Ex 7.1–7.3 subquestion table were missing."),
            gapResolutionSummary = "Added Ex 7.1–7.3 complete coverage (29 subparts) and Ex 7.3 collinearity solution."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 8,
            startPage = 155,
            endPage = 170,
            definitions = listOf(
                BilingualDefinition("Euclid's Division Lemma", "ಯೂಕ್ಲಿಡ್ ಭಾಗಾಕಾರ ಅನುಪ್ರಮೇಯ", "For positive integers a, b there exist unique q, r with a = bq + r, 0 ≤ r < b.", "p. 156"),
                BilingualDefinition("Fundamental Theorem of Arithmetic", "ಅಂಕಗಣಿತದ ಮೂಲ ಪ್ರಮೇಯ", "Every composite number factors uniquely as a product of primes (up to order).", "p. 159")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 8.1", "pp. 157–158", "Q1(i–iii), Q2–Q5", 7, "Euclid's division algorithm HCF (135,225; 867,255), odd forms 6q+1/6q+3", "HCF = 45, 51"),
                ExerciseCoverageRecord("Ex 8.2", "p. 162", "Q1(i–v), Q2(i–iii), Q3(i–iii), Q4–Q7", 15, "Prime factors, HCF×LCM=ab, 6ⁿ ends with 0 check, circular track LCM", "HCF(306,657)=9 ⇒ LCM=22338"),
                ExerciseCoverageRecord("Ex 8.3", "p. 166", "Q1–Q3(i–iii)", 5, "Irrationality proofs of √5, 3+2√5, 1/√2, 7√5, 6+√2", "Contradiction proof"),
                ExerciseCoverageRecord("Ex 8.4", "pp. 169–170", "Q1(i–x), Q2, Q3(i–iii)", 14, "Terminating vs non-terminating repeating decimals via q = 2ⁿ5ᵐ", "Terminates after max(n,m) places")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D8.1", "Factor Tree & Prime Power Hierarchy", "p. 160", "Number System Hierarchy Chart", "Real = Rational (Terminating / Repeating) ∪ Irrational")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 8.4 Decimal Expansion Without Long Division",
                problem = "State without long division whether 13 / 3125 has a terminating decimal expansion and find how many decimal places it has.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Factor denominator: 3125 = 5 × 5 × 5 × 5 × 5 = 5⁵ = 2⁰ × 5⁵.",
                    "Step 2 (0.5M): Since gcd(13, 3125) = 1 and denominator is of form 2ⁿ5ᵐ, it is TERMINATING.",
                    "Step 3 (1.0M): 13/5⁵ = (13 × 2⁵)/(10⁵) = 416/100000 = 0.00416 (terminates after 5 places)."
                ),
                finalAnswer = "Terminating decimal (5 decimal places: 0.00416)",
                examinerTip = "Always cancel common factors with numerator FIRST before testing q = 2ⁿ5ᵐ."
            ),
            priorAuditGapsFound = listOf("Ex 8.4 terminating decimal rule example & Ex 8.1–8.4 41-subpart inventory were missing."),
            gapResolutionSummary = "Filled Ex 8.1–8.4 index, Ex 8.4 terminating decimal worked solution, and Euclid HCF verifier."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 9,
            startPage = 171,
            endPage = 190,
            definitions = listOf(
                BilingualDefinition("Zero of a Polynomial", "ಬಹುಪದೋಕ್ತಿಯ ಶೂನ್ಯತೆ", "A real number k such that p(k) = 0; X-intercept of y = p(x).", "p. 173"),
                BilingualDefinition("Division Algorithm", "ಬಹುಪದೋಕ್ತಿ ಭಾಗಾಕಾರ ಅಲ್ಗಾರಿದಂ", "p(x) = g(x)·q(x) + r(x) where r(x) = 0 or deg r(x) < deg g(x).", "p. 185")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 9.1", "p. 178", "Q1(i–vi)", 6, "Counting zeroes from X-axis intersections of y = p(x) graphs", "0, 1, 3, 2, 4, 3 zeroes"),
                ExerciseCoverageRecord("Ex 9.2", "pp. 183–184", "Q1(i–vi), Q2(i–vi)", 12, "Finding zeroes, verifying α+β=-b/a & αβ=c/a, forming polynomial from sum & product", "k[x²-(α+β)x+αβ]"),
                ExerciseCoverageRecord("Ex 9.3", "pp. 188–190", "Q1(i–iii), Q2(i–iii), Q3–Q5", 9, "Polynomial long division, finding remaining zeroes given ±√(5/3)", "q(x), r(x) division")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D9.1", "Interactive Quadratic Parabola & Zeroes Graph", "pp. 174–177", "Interactive Canvas (Math Blackboard Tab 2)", "y = ax² + bx + c, X-axis root markers")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 9.3 Polynomial Long Division Check",
                problem = "Divide p(x) = x³ - 3x² + 5x - 3 by g(x) = x² - 2 and find quotient q(x) and remainder r(x).",
                markingSteps = listOf(
                    "Step 1 (1.0M): First quotient term x³/x² = x ⇒ x(x² - 2) = x³ - 2x; subtract ⇒ -3x² + 7x - 3.",
                    "Step 2 (1.0M): Next term -3x²/x² = -3 ⇒ -3(x² - 2) = -3x² + 6; subtract ⇒ 7x - 9.",
                    "Step 3 (0.5M): Since deg(7x - 9) < deg(x² - 2), q(x) = x - 3, r(x) = 7x - 9."
                ),
                finalAnswer = "q(x) = x - 3, r(x) = 7x - 9",
                examinerTip = "Verify: (x² - 2)(x - 3) + (7x - 9) = x³ - 3x² + 5x - 3."
            ),
            priorAuditGapsFound = listOf("Ex 9.3 long division worked example & Ex 9.1–9.3 subquestion tally were missing."),
            gapResolutionSummary = "Added Ex 9.1–9.3 (27 subparts), long division verification, and live Parabola Canvas."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 10,
            startPage = 191,
            endPage = 216,
            definitions = listOf(
                BilingualDefinition("Quadratic Equation", "ವರ್ಗ ಸಮೀಕರಣ", "An equation of the form ax² + bx + c = 0 where a, b, c ∈ R and a ≠ 0.", "p. 192"),
                BilingualDefinition("Discriminant (Δ)", "ಶೋಧಕ (Δ)", "Value Δ = b² - 4ac determining whether roots are real distinct (Δ>0), equal (Δ=0), or non-real (Δ<0).", "p. 211")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 10.1", "pp. 195–196", "Q1(i–viii), Q2(i–iv)", 12, "Checking standard quadratic degree 2 & modelling area/age/train equations", "Degree = 2"),
                ExerciseCoverageRecord("Ex 10.2", "pp. 201–202", "Q1(i–v), Q2–Q6", 10, "Splitting middle term factorisation & cottage industry/triangle sides", "x = root₁, root₂"),
                ExerciseCoverageRecord("Ex 10.3", "pp. 208–210", "Q1(i–iv), Q2(i–iv), Q3(i–ii), Q4–Q11", 19, "Completing square / Quadratic formula & train speed / water taps problems", "x = [-b ± √Δ]/2a"),
                ExerciseCoverageRecord("Ex 10.4", "pp. 214–216", "Q1(i–iii), Q2(i–ii), Q3–Q5", 8, "Nature of roots Δ = b² - 4ac & finding unknown k for equal roots", "Δ = 0 ⇒ equal roots")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D10.1", "Parabola Root Sign Diagram (Δ > 0, Δ = 0, Δ < 0)", "p. 212", "Interactive Canvas (Math Blackboard Tab 2)", "2 cuts / 1 tangent point / 0 cuts on X-axis")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 10.3 Train Speed Quadratic Word Problem (3M)",
                problem = "A train travels 360 km at a uniform speed. If the speed had been 5 km/h more, it would take 1 hour less. Find the speed of the train.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Let uniform speed = x km/h ⇒ 360/x - 360/(x + 5) = 1.",
                    "Step 2 (1.0M): 360(x + 5 - x) = x(x + 5) ⇒ x² + 5x - 1800 = 0.",
                    "Step 3 (1.5M): (x + 45)(x - 40) = 0 ⇒ x = 40 km/h (reject x = -45)."
                ),
                finalAnswer = "40 km/h",
                examinerTip = "Slower speed gives LARGER time, so 360/x comes BEFORE 360/(x + 5)."
            ),
            priorAuditGapsFound = listOf("Ex 10.1–10.4 49-subpart coverage matrix & Ex 10.3 train word model were missing."),
            gapResolutionSummary = "Added complete Ex 10.1–10.4 breakdown and train-speed quadratic solution."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 11,
            startPage = 217,
            endPage = 244,
            definitions = listOf(
                BilingualDefinition("Trigonometric Ratios", "ತ್ರಿಕೋನಮಿತಿಯ ಅನುಪಾತಗಳು", "Six ratios of sides of a right-angled triangle relative to acute angle θ.", "p. 219"),
                BilingualDefinition("Trigonometric Identity", "ತ್ರಿಕೋನಮಿತೀಯ ನಿತ್ಯಸಮೀಕರಣ", "An equation involving trigonometric ratios valid for all admissible angles.", "p. 235")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 11.1", "pp. 223–225", "Q1–Q11", 11, "Finding all ratios given sin/cos/cot; Pythagoras missing side", "sin²A + cos²A = 1"),
                ExerciseCoverageRecord("Ex 11.2", "pp. 228–229", "Q1(i–v), Q2(i–iv), Q3, Q4(i–v)", 15, "Standard angle table 0°–90° evaluations & sin(A+B)=1, sin(A-B)=1/2", "A = 45°, B = 15°"),
                ExerciseCoverageRecord("Ex 11.3", "pp. 233–234", "Q1(i–iv), Q2(i–ii), Q3–Q7", 11, "Complementary angle simplification & sin 3A = cos(A-26°)", "θ = complementary"),
                ExerciseCoverageRecord("Ex 11.4", "pp. 240–244", "Q1–Q4, Q5(i–x)", 17, "All 10 classic 3-mark trigonometric identity proofs", "LHS → RHS exact identity")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D11.1", "Right Triangle Opp-Adj-Hyp Orientation Diagram", "p. 219", "Interactive Canvas (Math Blackboard Tab 6)", "∠B=90°, acute θ, Opp, Adj, Hyp labels")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 11.3 Complementary Angles Chain Product",
                problem = "Show that tan 48° tan 23° tan 42° tan 67° = 1.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Pair complements: 48° + 42° = 90° and 23° + 67° = 90°.",
                    "Step 2 (1.0M): LHS = tan(90° - 42°) tan(90° - 67°) tan 42° tan 67° = cot 42° cot 67° tan 42° tan 67°.",
                    "Step 3 (0.5M): Since cot θ · tan θ = 1 ⇒ (1)(1) = 1 = RHS."
                ),
                finalAnswer = "1 (Hence shown)",
                examinerTip = "Change ONLY the first half of each complementary pair!"
            ),
            priorAuditGapsFound = listOf("Ex 11.1–11.4 54-subpart table & interactive Trigonometry Canvas diagram were missing."),
            gapResolutionSummary = "Added Ex 11.1–11.4 breakdown, complementary chain proof, and Right-Triangle Trig Canvas tab."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 12,
            startPage = 245,
            endPage = 258,
            definitions = listOf(
                BilingualDefinition("Angle of Elevation", "ಉನ್ನತ ಕೋನ", "Angle above the horizontal line of sight when observing an elevated point.", "p. 246"),
                BilingualDefinition("Angle of Depression", "ಅವನತ ಕೋನ", "Angle below the horizontal line of sight from an elevated observer down to an object.", "p. 247")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 12.1", "pp. 253–258", "Q1–Q16", 16, "Circus rope, broken tree, kite string, two-building shadow, pedestal + statue, lighthouse two ships (30° & 45°), TV tower, speed car approach", "4M Diagram + Double tan θ")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D12.1", "Interactive Elevation Angle & Tower Height Simulator", "pp. 246–254", "Interactive Canvas (Math Blackboard Tab 6)", "Horizontal base d, vertical tower h, elevation angle θ")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 12.1 Q13 Lighthouse Two-Ship Depression (4M)",
                problem = "From the top of a 75 m lighthouse, angles of depression of two ships on same side are 30° and 45°. Find the distance between the two ships.",
                markingSteps = listOf(
                    "Step 1 (1.0M): Draw 75 m tower AB; ships C (45°) and D (30°) with horizontal ray for depression.",
                    "Step 2 (1.0M): In △ABC: tan 45° = 75/BC ⇒ BC = 75 m.",
                    "Step 3 (1.0M): In △ABD: tan 30° = 75/BD ⇒ BD = 75√3 m.",
                    "Step 4 (1.0M): Distance CD = BD - BC = 75(√3 - 1) m."
                ),
                finalAnswer = "75(√3 - 1) m",
                examinerTip = "Closer ship ALWAYS has the steeper/larger angle (45° inside, 30° outside)."
            ),
            priorAuditGapsFound = listOf("Ex 12.1 Q1–Q16 inventory & double-triangle lighthouse worked solution were missing."),
            gapResolutionSummary = "Added Ex 12.1 16-question index, 4-mark lighthouse solution, and interactive Elevation Canvas."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 13,
            startPage = 259,
            endPage = 292,
            definitions = listOf(
                BilingualDefinition("Class Mark", "ವರ್ಗ ಮಧ್ಯಬಿಂದು", "Mid-value of a class interval: (Upper Limit + Lower Limit) / 2.", "p. 261"),
                BilingualDefinition("Ogive (Cumulative Curve)", "ಓಜೀವ್ ವಕ್ರರೇಖೆ", "Smooth graph of cumulative frequency plotted against upper (less-than) or lower (more-than) limits.", "p. 283")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 13.1", "pp. 268–270", "Q1–Q9", 9, "Mean by Direct, Assumed Mean & Step-Deviation + missing frequency f", "x̄ = a + (Σfᵢuᵢ/Σfᵢ)h"),
                ExerciseCoverageRecord("Ex 13.2", "pp. 274–275", "Q1–Q6", 6, "Grouped Mode formula Mode = l + [(f₁-f₀)/(2f₁-f₀-f₂)]h", "Check answer inside modal class"),
                ExerciseCoverageRecord("Ex 13.3", "pp. 280–281", "Q1–Q7", 7, "Grouped Median & double missing frequency x, y given Median & n=60", "3 Median = Mode + 2 Mean"),
                ExerciseCoverageRecord("Ex 13.4", "pp. 290–292", "Q1–Q3", 3, "Less-than & More-than Ogives + intersection Median reading", "(Upper/Lower, cf)")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D13.1", "Less-Than & More-Than Ogive Intersection Schematic", "p. 288", "Annotated Graph Reference", "X-axis class limits, Y-axis cf, crossing at Median")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 13.2 Mode of Grouped Frequency Table (3M)",
                problem = "Find the mode: Class 1–3(7), 3–5(8), 5–7(2), 7–9(2), 9–11(1).",
                markingSteps = listOf(
                    "Step 1 (0.5M): Maximum frequency f₁ = 8 ⇒ Modal class is 3–5, so l = 3, h = 2, f₀ = 7, f₂ = 2.",
                    "Step 2 (1.5M): Mode = 3 + [(8 - 7) / (2×8 - 7 - 2)] × 2 = 3 + (1/7)×2 = 3 + 2/7",
                    "Step 3 (1.0M): Mode = 3.286 (lies inside 3–5!)."
                ),
                finalAnswer = "Mode = 3.286",
                examinerTip = "Verify that your Mode lies inside [l, l + h] before moving to the next question."
            ),
            priorAuditGapsFound = listOf("Ex 13.1–13.4 25-question index & grouped Mode worked table were missing."),
            gapResolutionSummary = "Added Ex 13.1–13.4 complete breakdown & 3-mark grouped Mode verified solution."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 14,
            startPage = 293,
            endPage = 312,
            definitions = listOf(
                BilingualDefinition("Elementary Event", "ಪ್ರಾಥಮಿಕ ಘಟನೆ", "An event having only one single outcome of an experiment.", "p. 296"),
                BilingualDefinition("Complementary Event (Ē)", "ಪೂರಕ ಘಟನೆ", "Event 'not E' where P(E) + P(Ē) = 1.", "p. 298")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 14.1", "pp. 306–311", "Q1(i–v), Q2–Q25", 29, "Coins, marbles/bulbs/pens, playing cards 52, two dice 36 outcomes, birthday same/different", "0 ≤ P(E) ≤ 1"),
                ExerciseCoverageRecord("Ex 14.2 (Optional)", "p. 312", "Q1–Q5", 5, "Two customer days, concentric dartboard area ratio probability, jar marble ratio", "Area/favorable ratios")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D14.1", "52 Playing Cards & 6×6 Dice Sample Space Tree", "pp. 301–304", "Structured Sample Space Reference", "26 Red / 26 Black; 12 Face Cards; 36 ordered dice pairs")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 14.1 Defective Pens Complementary Probability",
                problem = "12 defective pens are accidentally mixed with 132 good ones. One pen is taken out at random. Find P(good pen) and P(defective pen).",
                markingSteps = listOf(
                    "Step 1 (0.5M): Total pens n(S) = 132 + 12 = 144.",
                    "Step 2 (1.0M): P(good pen) = 132 / 144 = 11 / 12.",
                    "Step 3 (0.5M): P(defective pen) = 1 - 11/12 = 1 / 12."
                ),
                finalAnswer = "Good: 11/12, Defective: 1/12",
                examinerTip = "Divide numerator and denominator by 12 to simplify 132/144 cleanly."
            ),
            priorAuditGapsFound = listOf("Ex 14.1 Q1–Q25 subparts + Ex 14.2 optional & defective pen example were missing."),
            gapResolutionSummary = "Added Ex 14.1–14.2 (34 subparts), sample space taxonomy, and defective-pen solution."
        ),
        ChapterTextbookSupplement(
            chapterNumber = 15,
            startPage = 313,
            endPage = 342,
            definitions = listOf(
                BilingualDefinition("Frustum of a Cone", "ಶಂಕುವಿನ ಭಿನ್ನಕ", "Lower portion left when a right circular cone is sliced by a plane parallel to its base.", "p. 329"),
                BilingualDefinition("Conservation of Volume", "ಘನಫಲ ಸಂರಕ್ಷಣೆ", "Melting and recasting a solid changes its shape & surface area while total volume remains unchanged.", "p. 324")
            ),
            exercises = listOf(
                ExerciseCoverageRecord("Ex 15.1", "pp. 318–319", "Q1–Q9", 9, "Combined solid TSA: joined cubes, hemisphere-cylinder tent, scooped wooden cylinder", "Exposed CSAs summed"),
                ExerciseCoverageRecord("Ex 15.2", "pp. 322–323", "Q1–Q8", 8, "Combined solid volume: gulab jamuns, pen stand depressions, cone-cylinder vessel", "Add/subtract volumes"),
                ExerciseCoverageRecord("Ex 15.3", "pp. 326–328", "Q1–Q9", 9, "Recasting sphere-to-cylinder, digging well embankment, canal water flow speed", "V_initial = N × V_small"),
                ExerciseCoverageRecord("Ex 15.4", "pp. 339–342", "Q1–Q5", 5, "Frustum drinking glass, CSA of frustum cap, bucket milk cost & metallic wire", "V = ⅓πh(r₁²+r₂²+r₁r₂)")
            ),
            diagrams = listOf(
                DiagramRepresentationSpec("D15.1", "Interactive Frustum of Cone Cross-Section & Volume Visualizer", "pp. 329–341", "Interactive Canvas (Math Blackboard Tab 7)", "Top radius r₂, bottom radius r₁, height h, slant height l")
            ),
            supplementaryWorkedExample = SolvedExample(
                title = "Ex 15.3 Sphere Melted & Recast into Cylinder",
                problem = "A metallic sphere of radius 4.2 cm is melted and recast into a cylinder of radius 6 cm. Find the height of the cylinder.",
                markingSteps = listOf(
                    "Step 1 (0.5M): Volume of sphere = Volume of cylinder.",
                    "Step 2 (1.0M): (4/3)π(4.2)³ = π(6)²h (cancel π on both sides).",
                    "Step 3 (1.0M): h = (4/3 × 4.2 × 4.2 × 4.2) / 36 = 1.4 × 1.4 × 1.4 = 2.744 cm."
                ),
                finalAnswer = "h = 2.744 cm",
                examinerTip = "Cancel π BEFORE multiplying decimals — it prevents silly arithmetic slips!"
            ),
            priorAuditGapsFound = listOf("Ex 15.1–15.4 breakdown, sphere recasting worked example, and Frustum Canvas were missing."),
            gapResolutionSummary = "Added Ex 15.1–15.4 (31 questions), recasting solution, Frustum MathVerificationEngine check, and Frustum Canvas."
        )
    ).associateBy { it.chapterNumber }
}
