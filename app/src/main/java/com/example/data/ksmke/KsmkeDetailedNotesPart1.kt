package com.example.data.ksmke

import com.example.domain.SolvedExample
import com.example.domain.TopicDetailedNote

internal object KsmkeDetailedNotesPart1 {

    val byChapter: Map<Int, List<TopicDetailedNote>> = mapOf(
        1 to listOf(
            TopicDetailedNote(
                topicNumber = "1.1",
                topicTitleEn = "Introduction & Identification of Arithmetic Progressions",
                topicTitleKn = "ಸಮಾಂತರ ಶ್ರೇಢಿಗಳ ಪರಿಚಯ ಮತ್ತು ಗುರುತಿಸುವಿಕೆ",
                coreConcepts = listOf(
                    "A sequence is an ordered list of numbers following a definite rule.",
                    "An Arithmetic Progression (AP) is a list of numbers in which each term is obtained by adding a fixed number 'd' to the preceding term, except the first term.",
                    "General form of an AP: a, a + d, a + 2d, a + 3d, ..., a + (n - 1)d.",
                    "The fixed number 'd' is called the common difference; it can be positive, negative, or zero."
                ),
                formulasAndRules = listOf(
                    "First term: a = a₁",
                    "Common difference: d = a₂ - a₁ = a₃ - a₂ = aₖ₊₁ - aₖ",
                    "Condition to be an AP: (a₂ - a₁) = (a₃ - a₂) for all consecutive terms"
                ),
                derivationsOrMethods = listOf(
                    "Step 1: Compute d₁ = a₂ - a₁ and d₂ = a₃ - a₂.",
                    "Step 2: If d₁ = d₂, the sequence is an AP with common difference d = d₁.",
                    "Step 3: Add d repeatedly to generate subsequent terms: a₄ = a₃ + d, a₅ = a₄ + d."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Verify AP & Find Next Three Terms",
                        problem = "Check whether 4, 10, 16, 22, ... forms an AP. If yes, write the next two terms.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Let a₁ = 4, a₂ = 10, a₃ = 16, a₄ = 22.",
                            "Step 2 (0.5M): a₂ - a₁ = 10 - 4 = 6; a₃ - a₂ = 16 - 10 = 6; a₄ - a₃ = 22 - 16 = 6.",
                            "Step 3 (0.5M): Since aₖ₊₁ - aₖ is constant (= 6), it is an AP with d = 6.",
                            "Step 4 (0.5M): Next two terms: a₅ = 22 + 6 = 28, a₆ = 28 + 6 = 34."
                        ),
                        finalAnswer = "Yes, d = 6; next terms are 28 and 34",
                        examinerTip = "Check at least two consecutive differences before concluding it is an AP."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Calculating d = a₁ - a₂ instead of a₂ - a₁ in decreasing APs.",
                    "Testing only a₂ - a₁ without checking a₃ - a₂."
                )
            ),
            TopicDetailedNote(
                topicNumber = "1.2",
                topicTitleEn = "nth Term of an AP (aₙ) & Finding Term from the End",
                topicTitleKn = "ಸಮಾಂತರ ಶ್ರೇಢಿಯ n-ನೇ ಪದ (aₙ)",
                coreConcepts = listOf(
                    "The nth term aₙ (also called general term) connects position n with value aₙ.",
                    "If an AP has m terms, the mth term aₘ represents the last term l.",
                    "To check if a number K belongs to an AP, solve aₙ = K for n; K belongs to the AP only if n is a positive integer."
                ),
                formulasAndRules = listOf(
                    "nth term from start: aₙ = a + (n - 1)d",
                    "nth term from last (reverse AP): l - (n - 1)d",
                    "Arithmetic Mean of a and c (three numbers a, b, c in AP): b = (a + c) / 2"
                ),
                derivationsOrMethods = listOf(
                    "Derivation pattern: a₁ = a + 0·d, a₂ = a + 1·d, a₃ = a + 2·d ⇒ By induction, aₙ = a + (n - 1)d.",
                    "Reverse AP method: Reverse the AP so first term becomes l and common difference becomes -d, then apply l + (n - 1)(-d)."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Two-Condition Term Problem (Board Favourite)",
                        problem = "Determine the AP whose 3rd term is 5 and the 7th term is 9.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): a₃ = a + 2d = 5 ...(1)",
                            "Step 2 (0.5M): a₇ = a + 6d = 9 ...(2)",
                            "Step 3 (1.0M): Subtract (1) from (2): 4d = 4 ⇒ d = 1",
                            "Step 4 (1.0M): Substitute d = 1 in (1): a + 2(1) = 5 ⇒ a = 3. Hence AP is 3, 4, 5, 6, ..."
                        ),
                        finalAnswer = "AP: 3, 4, 5, 6, ...",
                        examinerTip = "Always write 'a₃ = a + 2d' cleanly; do not write a + 3d."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Confusing position n with term value aₙ.",
                    "Accepting fractional or negative n when checking if a number is a term of the AP."
                )
            ),
            TopicDetailedNote(
                topicNumber = "1.3",
                topicTitleEn = "Sum of First n Terms of an AP (Sₙ) & Word Problems",
                topicTitleKn = "ಮೊದಲ n ಪದಗಳ ಮೊತ್ತ (Sₙ) ಮತ್ತು ವಾಕ್ಯ ರೂಪದ ಲೆಕ್ಕಗಳು",
                coreConcepts = listOf(
                    "Gauss pairing trick pairs first and last terms: Sₙ has n/2 pairs each summing to (a + l).",
                    "Use Sₙ = (n/2)[2a + (n-1)d] when a, d, n are known.",
                    "Use Sₙ = (n/2)(a + l) when first term a and last term l are known.",
                    "The relation aₙ = Sₙ - Sₙ₋₁ recovers any nth term directly from a sum formula."
                ),
                formulasAndRules = listOf(
                    "Sₙ = (n / 2)[2a + (n - 1)d]",
                    "Sₙ = (n / 2)(a + l) where l = aₙ",
                    "Sum of first n positive integers: Sₙ = n(n + 1) / 2",
                    "Term from sum: aₙ = Sₙ - Sₙ₋₁ (for n ≥ 2), a₁ = S₁"
                ),
                derivationsOrMethods = listOf(
                    "Step 1: Write S = a + (a+d) + ... + (a+(n-1)d).",
                    "Step 2: Write S in reverse: S = [a+(n-1)d] + [a+(n-2)d] + ... + a.",
                    "Step 3: Add term-by-term: 2S = n[2a + (n-1)d] ⇒ Sₙ = (n/2)[2a + (n-1)d]."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Finding n When Sum Sₙ is Given",
                        problem = "How many terms of the AP 9, 17, 25, ... must be taken to give a sum of 636?",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Given a = 9, d = 17 - 9 = 8, Sₙ = 636.",
                            "Step 2 (1.0M): 636 = (n/2)[2(9) + (n - 1)8] = n[9 + 4n - 4] = n(4n + 5)",
                            "Step 3 (1.0M): 4n² + 5n - 636 = 0 ⇒ (4n + 53)(n - 12) = 0",
                            "Step 4 (0.5M): Since n must be a positive integer, n = 12 (reject -53/4)."
                        ),
                        finalAnswer = "n = 12 terms",
                        examinerTip = "Divide by 2 inside the bracket [2a + (n-1)d] early whenever 2a and d are even."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Forgetting the '2' in 2a inside Sₙ = (n/2)[2a + (n-1)d].",
                    "Writing Sₙ instead of aₙ when a problem asks for the 'nth term'."
                )
            )
        ),
        2 to listOf(
            TopicDetailedNote(
                topicNumber = "2.1",
                topicTitleEn = "Similar Figures & Basic Proportionality Theorem (Thales Theorem)",
                topicTitleKn = "ಸಮರೂಪ ಆಕೃತಿಗಳು ಮತ್ತು ಮೂಲ ಸಮಾನುಪಾತತೆಯ ಪ್ರಮೇಯ",
                coreConcepts = listOf(
                    "Two polygons of the same number of sides are similar if (i) their corresponding angles are equal and (ii) their corresponding sides are in the same ratio (proportional).",
                    "All congruent figures are similar, but all similar figures need not be congruent.",
                    "Basic Proportionality Theorem (BPT / Thales Theorem) and its Converse are top-priority 4-mark board proof topics."
                ),
                formulasAndRules = listOf(
                    "BPT: In △ABC, if DE || BC, then AD/DB = AE/EC",
                    "Corollaries: AD/AB = AE/AC and DB/AB = EC/AC",
                    "Converse of BPT: If AD/DB = AE/EC, then DE || BC"
                ),
                derivationsOrMethods = listOf(
                    "4M Board Proof Checklist: (1) Statement, (2) Neat Labelled Figure, (3) Data & To Prove, (4) Construction (join BE, CD; EN ⊥ AB, DM ⊥ AC), (5) Equal base-between-parallels area equality ⇒ AD/DB = AE/EC."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "BPT Diagonal Ratio Proof (Trapezium)",
                        problem = "ABCD is a trapezium with AB || DC and diagonals intersect at O. Show that AO/BO = CO/DO.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Draw OE || AB (and hence OE || DC) meeting AD at E.",
                            "Step 2 (1.0M): In △DAB, EO || AB ⇒ DE/EA = DO/OB ...(1) by BPT.",
                            "Step 3 (1.0M): In △ADC, EO || DC ⇒ AE/ED = AO/OC ⇒ DE/EA = OC/AO ...(2).",
                            "Step 4 (0.5M): From (1) & (2): AO/OC = OB/OD ⇒ AO/BO = CO/DO."
                        ),
                        finalAnswer = "Hence proved AO/BO = CO/DO",
                        examinerTip = "Keep vertex correspondence order identical when writing ratios."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Writing AD/AB = AE/EC (mixing part-to-whole with part-to-part).",
                    "Omitting perpendicular heights EN and DM in the BPT proof."
                )
            ),
            TopicDetailedNote(
                topicNumber = "2.2",
                topicTitleEn = "Criteria for Similarity (AAA/AA, SSS, SAS) & Area Ratio Theorem",
                topicTitleKn = "ತ್ರಿಭುಜಗಳ ಸಮರೂಪತೆಯ ನಿರ್ಧಾರಕ ಗುಣಗಳು",
                coreConcepts = listOf(
                    "AAA (or AA) Criterion: If two angles of one triangle are respectively equal to two angles of another, the triangles are similar.",
                    "SSS Criterion: If sides of one triangle are proportional to sides of another, corresponding angles are equal and triangles are similar.",
                    "SAS Criterion: If one angle equals one angle and the sides INCLUDING these angles are proportional, the triangles are similar."
                ),
                formulasAndRules = listOf(
                    "△ABC ~ △DEF ⇒ AB/DE = BC/EF = AC/DF and ∠A = ∠D, ∠B = ∠E, ∠C = ∠F",
                    "Area Ratio Theorem: ar(△ABC)/ar(△DEF) = (AB/DE)² = (BC/EF)² = (AC/DF)²",
                    "Perimeter Ratio: Perimeter(△ABC)/Perimeter(△DEF) = AB/DE"
                ),
                derivationsOrMethods = listOf(
                    "Step 1: Identify two triangles containing the required unknown lengths.",
                    "Step 2: Prove AA similarity (look for common angles, vertically opposite angles, or alternate interior angles from parallel lines).",
                    "Step 3: Match vertices in exact angle order: if ∠A = ∠P and ∠B = ∠Q, write △ABC ~ △PQR."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Shadow & Pole Similarity (2M/3M)",
                        problem = "A vertical pole of length 6 m casts a shadow 4 m long on the ground and at the same time a tower casts a shadow 28 m long. Find the height of the tower.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): At the same time, Sun's elevation angle is equal ⇒ △ABC ~ △PQR by AA similarity.",
                            "Step 2 (0.5M): Ratio of corresponding sides: Height(pole)/Height(tower) = Shadow(pole)/Shadow(tower)",
                            "Step 3 (1.0M): 6 / h = 4 / 28 ⇒ h = (6 × 28) / 4 = 6 × 7 = 42 m"
                        ),
                        finalAnswer = "42 m",
                        examinerTip = "Briefly mention WHY the two triangles are similar (90° ground angle + equal sun elevation)."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Comparing sides in wrong vertex order when writing similarity ratios.",
                    "Forgetting to square the side ratio when finding the ratio of AREAS of similar triangles."
                )
            )
        ),
        3 to listOf(
            TopicDetailedNote(
                topicNumber = "3.1",
                topicTitleEn = "Standard Form & Graphical Consistency Conditions",
                topicTitleKn = "ರೇಖಾತ್ಮಕ ಸಮೀಕರಣಗಳ ಆದರ್ಶ ರೂಪ ಮತ್ತು ನಕ್ಷಾ ವಿಧಾನ",
                coreConcepts = listOf(
                    "General form of a pair of linear equations: a₁x + b₁y + c₁ = 0 and a₂x + b₂y + c₂ = 0.",
                    "A pair is Consistent if it has at least one solution (intersecting or coincident lines).",
                    "A pair is Inconsistent if it has no solution (parallel lines)."
                ),
                formulasAndRules = listOf(
                    "a₁/a₂ ≠ b₁/b₂ ⇒ Intersecting lines, Exactly 1 unique solution (Consistent)",
                    "a₁/a₂ = b₁/b₂ = c₁/c₂ ⇒ Coincident lines, Infinitely many solutions (Dependent Consistent)",
                    "a₁/a₂ = b₁/b₂ ≠ c₁/c₂ ⇒ Parallel lines, No solution (Inconsistent)"
                ),
                derivationsOrMethods = listOf(
                    "4-Mark Graph Solution Method:",
                    "Step 1: Rewrite both equations as y = f(x).",
                    "Step 2: Prepare a 3-point integer table (x, y) for each line.",
                    "Step 3: Draw X'OX and YOY' axes, mark Scale on top-right (Scale: X-axis 1 cm = 1 unit, Y-axis 1 cm = 1 unit).",
                    "Step 4: Plot both lines cleanly, circle their intersection point P(x, y), and state x = ..., y = ...."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Graph Table & Intersection",
                        problem = "Solve graphically: x + y = 5 and x - y = 1.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Table 1 (y = 5 - x): (0,5), (2,3), (5,0).",
                            "Step 2 (1.0M): Table 2 (y = x - 1): (0,-1), (1,0), (3,2).",
                            "Step 3 (1.0M): Plot lines with axes labelled & Scale written.",
                            "Step 4 (1.0M): Lines intersect at (3, 2) ⇒ x = 3, y = 2."
                        ),
                        finalAnswer = "x = 3, y = 2",
                        examinerTip = "Verify the intersection algebraically in rough work so your plotted lines intersect cleanly at exact integer coordinates."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Rewriting one equation as ax + by = c and the other as ax + by + c = 0 before comparing c₁/c₂ (keep constants on the SAME side!).",
                    "Missing arrowheads on axes or missing Scale on the 4-mark graph."
                )
            ),
            TopicDetailedNote(
                topicNumber = "3.2",
                topicTitleEn = "Algebraic Methods: Substitution, Elimination & Word Problems",
                topicTitleKn = "ಆದೇಶ ವಿಧಾನ, ವರ್ಜಿಸುವ ವಿಧಾನ ಮತ್ತು ವಾಕ್ಯ ಲೆಕ್ಕಗಳು",
                coreConcepts = listOf(
                    "Substitution Method: Express one variable in terms of the other from Eq (1) and substitute into Eq (2).",
                    "Elimination Method: Multiply equations by suitable constants to make coefficients of x (or y) numerically equal, then add (opposite signs) or subtract (same signs).",
                    "Common board word problems: Two-digit number (10x + y), fraction (x/y), upstream/downstream speed (u ± v), age problems."
                ),
                formulasAndRules = listOf(
                    "Two-digit number with tens digit x, units digit y: 10x + y; reversed = 10y + x",
                    "Downstream speed = u + v; Upstream speed = u - v (u = boat speed, v = stream speed)"
                ),
                derivationsOrMethods = listOf(
                    "Elimination Rule: Same Sign ⇒ Subtract (SSS); Opposite Sign ⇒ Add.",
                    "Always substitute the found variable back into BOTH original equations mentally to verify."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Two-Digit Number Board Problem",
                        problem = "The sum of digits of a two-digit number is 9. Nine times this number is twice the number obtained by reversing the digits. Find the number.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Let tens digit = x, units digit = y. Number = 10x + y, Reversed = 10y + x.",
                            "Step 2 (0.5M): x + y = 9 ...(1)",
                            "Step 3 (1.0M): 9(10x + y) = 2(10y + x) ⇒ 90x + 9y = 20y + 2x ⇒ 88x - 11y = 0 ⇒ 8x - y = 0 ...(2)",
                            "Step 4 (1.0M): Add (1) & (2): 9x = 9 ⇒ x = 1, y = 8. Number = 18."
                        ),
                        finalAnswer = "18",
                        examinerTip = "Check: 9 × 18 = 162 and 2 × 81 = 162. Verified!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Writing a two-digit number as xy instead of 10x + y.",
                    "Forgetting to change signs of ALL terms in the second equation during subtraction."
                )
            )
        ),
        4 to listOf(
            TopicDetailedNote(
                topicNumber = "4.1",
                topicTitleEn = "Secant vs Tangent & Perpendicularity Theorem (Theorem 4.1)",
                topicTitleKn = "ವೃತ್ತದ ಸ್ಪರ್ಶಕ ಮತ್ತು ಛೇದಕ ರೇಖೆ (ಪ್ರಮೇಯ 4.1)",
                coreConcepts = listOf(
                    "A secant intersects a circle at two distinct points; a tangent intersects a circle at exactly ONE point (point of contact).",
                    "Only one tangent can be drawn at a given point on a circle; at most two parallel tangents can exist for a circle (at ends of a diameter).",
                    "Theorem 4.1: The tangent at any point of a circle is perpendicular to the radius through the point of contact."
                ),
                formulasAndRules = listOf(
                    "OP ⊥ XY at point of contact P ⇒ ∠OPY = 90°",
                    "Number of tangents: Inside circle = 0; On circle = 1; Outside circle = 2"
                ),
                derivationsOrMethods = listOf(
                    "Take any other point Q on tangent XY (Q ≠ P). Since Q lies outside the circle, OQ > radius OP. Since OP is the shortest distance from O to XY, OP ⊥ XY."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Finding Tangent Segment Length",
                        problem = "A tangent PQ at a point P of a circle of radius 5 cm meets a line through centre O at a point Q so that OQ = 12 cm. Find PQ.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): By Theorem 4.1, OP ⊥ PQ ⇒ ∠OPQ = 90°.",
                            "Step 2 (0.5M): In right △OPQ, OQ² = OP² + PQ²",
                            "Step 3 (1.0M): 12² = 5² + PQ² ⇒ PQ² = 144 - 25 = 119 ⇒ PQ = √119 cm"
                        ),
                        finalAnswer = "√119 cm",
                        examinerTip = "Identify the 90° angle at the point of contact P first, NOT at the external point Q!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Treating tangent PQ as hypotenuse instead of OQ (distance from centre)."
                )
            ),
            TopicDetailedNote(
                topicNumber = "4.2",
                topicTitleEn = "Equal Tangents from an External Point (Theorem 4.2 — Guaranteed 3M Proof)",
                topicTitleKn = "ಬಾಹ್ಯ ಬಿಂದುವಿನಿಂದ ಎಳೆದ ಸ್ಪರ್ಶಕಗಳ ಪ್ರಮೇಯ (3 ಅಂಕ)",
                coreConcepts = listOf(
                    "Theorem 4.2: The lengths of tangents drawn from an external point to a circle are equal (PQ = PR).",
                    "Line joining centre O to external point P bisects both ∠QPR (between tangents) and ∠QOR (between radii).",
                    "Quadrilateral OQPR is cyclic because ∠OQP + ∠ORP = 90° + 90° = 180°, so ∠QOR + ∠QPR = 180°."
                ),
                formulasAndRules = listOf(
                    "PQ = PR (RHS Congruence: △OQP ≅ △ORP)",
                    "∠QPR + ∠QOR = 180°",
                    "Tangents + inscribed quadrilateral property: AB + CD = AD + BC (circumscribed quad)"
                ),
                derivationsOrMethods = listOf(
                    "3M Proof Structure: Data (circle centre O, external point P, tangents PQ & PR) -> To Prove (PQ = PR) -> Construction (join OP, OQ, OR) -> Proof using RHS congruence (∠Q=∠R=90°, OP=OP, OQ=OR) -> PQ=PR by CPCT."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Circumscribed Quadrilateral Property",
                        problem = "A quadrilateral ABCD is drawn to circumscribe a circle. Prove that AB + CD = AD + BC.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Tangents from external points are equal: AP = AS, BP = BQ, CR = CQ, DR = DS.",
                            "Step 2 (1.0M): Add all four: (AP + BP) + (CR + DR) = (AS + DS) + (BQ + CQ)",
                            "Step 3 (1.0M): Therefore AB + CD = AD + BC. Hence proved."
                        ),
                        finalAnswer = "AB + CD = AD + BC",
                        examinerTip = "Put AP & BP on the LHS and AS & DS on the RHS so terms group neatly into sides."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Writing SAS instead of RHS in Theorem 4.2 proof.",
                    "Writing angles ∠Q and ∠R equal without citing Theorem 4.1 (radius ⊥ tangent)."
                )
            )
        ),
        5 to listOf(
            TopicDetailedNote(
                topicNumber = "5.1",
                topicTitleEn = "Perimeter & Area of Circle, Sector & Arc Length",
                topicTitleKn = "ವೃತ್ತದ ತ್ರಿಜ್ಯಾಂತರ ಖಂಡದ ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಕಂಸದ ಉದ್ದ",
                coreConcepts = listOf(
                    "A sector is the region bounded by two radii and the corresponding arc.",
                    "Major sector angle = 360° - θ when minor sector angle is θ.",
                    "Clock hand sweep: Minute hand sweeps 360° in 60 min ⇒ 6° per minute; Hour hand sweeps 0.5° per minute."
                ),
                formulasAndRules = listOf(
                    "Length of arc: l = (θ / 360°) × 2πr",
                    "Area of sector: A = (θ / 360°) × πr² = ½ × l × r",
                    "Perimeter of sector: l + 2r",
                    "Area of quadrant (θ = 90°): (1/4)πr²"
                ),
                derivationsOrMethods = listOf(
                    "Step 1: Identify radius r and central angle θ.",
                    "Step 2: Use π = 22/7 unless π = 3.14 is explicitly stated in the question.",
                    "Step 3: Reduce θ/360° fraction before multiplying."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Clock Minute Hand Swept Area",
                        problem = "Length of the minute hand of a clock is 14 cm. Find the area swept by it in 5 minutes.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): r = 14 cm; Angle in 60 min = 360° ⇒ in 5 min, θ = (360/60)×5 = 30°.",
                            "Step 2 (0.5M): Area = (θ / 360°) × πr²",
                            "Step 3 (1.0M): Area = (30/360) × (22/7) × 14 × 14 = (1/12) × 44 × 14 = 154/3 cm² ≈ 51.33 cm²"
                        ),
                        finalAnswer = "154/3 cm² (51.33 cm²)",
                        examinerTip = "Keep exact fraction 154/3 cm² alongside decimal for full safety."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Forgetting to add + 2r when asked for PERIMETER of a sector.",
                    "Using 3.14 when 22/7 cancels cleanly with r = 7 or 14."
                )
            ),
            TopicDetailedNote(
                topicNumber = "5.2",
                topicTitleEn = "Area of Segment & Shaded Combined Figures",
                topicTitleKn = "ವೃತ್ತಖಂಡದ ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಛಾಯೆ ಮಾಡಿದ ಭಾಗಗಳ ವಿಸ್ತೀರ್ಣ",
                coreConcepts = listOf(
                    "A segment is the region between a chord and its corresponding arc.",
                    "Area of minor segment = Area of sector OAPB - Area of △OAB.",
                    "Area of major segment = πr² - Area of minor segment."
                ),
                formulasAndRules = listOf(
                    "Area(△OAB) when θ = 90°: ½ r²",
                    "Area(△OAB) when θ = 60° (equilateral): (√3 / 4) r²",
                    "General Area(△OAB): ½ r² sin θ"
                ),
                derivationsOrMethods = listOf(
                    "For θ = 120°, drop OM ⊥ AB to form two 30°-60°-90° right triangles: OM = r/2, AB = 2(r√3/2) = r√3 ⇒ Area(△OAB) = (√3/4)r²."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Segment with Right Angle Chord",
                        problem = "A chord of a circle of radius 10 cm subtends a right angle at the centre. Find the area of the corresponding minor segment (π = 3.14).",
                        markingSteps = listOf(
                            "Step 1 (0.5M): r = 10 cm, θ = 90°.",
                            "Step 2 (1.0M): Sector Area = (90/360) × 3.14 × 10 × 10 = 78.5 cm².",
                            "Step 3 (0.5M): Triangle Area = ½ × 10 × 10 = 50 cm².",
                            "Step 4 (1.0M): Minor Segment = 78.5 - 50 = 28.5 cm²."
                        ),
                        finalAnswer = "28.5 cm²",
                        examinerTip = "Read carefully whether π = 3.14 is given in the question."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Subtracting sector from circle instead of triangle from sector."
                )
            )
        ),
        6 to listOf(
            TopicDetailedNote(
                topicNumber = "6.1",
                topicTitleEn = "Division of a Line Segment & Construction of Similar Triangles",
                topicTitleKn = "ರೇಖಾಖಂಡದ ವಿಭಜನೆ ಮತ್ತು ಸಮರೂಪ ತ್ರಿಭುಜಗಳ ರಚನೆ",
                coreConcepts = listOf(
                    "To divide segment AB in ratio m : n, draw an acute ray AX and mark (m + n) equal arcs.",
                    "To construct a triangle similar to △ABC with scale factor p/q: mark max(p, q) equal arcs on acute ray BX.",
                    "If p < q (proper fraction like 3/5), the new triangle lies INSIDE △ABC; if p > q (improper fraction like 7/5), it lies OUTSIDE △ABC."
                ),
                formulasAndRules = listOf(
                    "Denominator Rule: ALWAYS join the qth (denominator) arc B_q to vertex C first!",
                    "Numerator Rule: Draw line parallel to B_qC from B_p (numerator arc)."
                ),
                derivationsOrMethods = listOf(
                    "Construction Steps (3M/4M):",
                    "1. Construct base △ABC accurately using ruler and compass.",
                    "2. Draw ray BX making an acute angle with BC opposite vertex A.",
                    "3. Mark N = max(p, q) equidistant points B₁, B₂, ..., B_N on BX.",
                    "4. Join B_q to C; copy ∠BB_qC at B_p using compass arcs to get B_pC' || B_qC.",
                    "5. From C', draw C'A' || CA meeting BA (or extended BA) at A'."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Similar Triangle with Scale Factor 5/3 (> 1)",
                        problem = "Construct a triangle of sides 4 cm, 5 cm, 6 cm and then a triangle similar to it whose sides are 5/3 of the corresponding sides.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Draw BC = 6 cm, AB = 4 cm, AC = 5 cm.",
                            "Step 2 (1.0M): Draw acute ray BX, mark 5 equal points B₁..B₅. Join B₃ (denominator!) to C.",
                            "Step 3 (1.0M): Draw B₅C' || B₃C meeting extended BC at C', and C'A' || CA meeting extended BA at A'."
                        ),
                        finalAnswer = "△A'BC' is the required enlarged similar triangle",
                        examinerTip = "Keep all parallel-angle copying compass arcs clearly visible!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Joining B₅ instead of B₃ (denominator) to C.",
                    "Drawing C'A' without matching the equal alternate/corresponding angle arc."
                )
            ),
            TopicDetailedNote(
                topicNumber = "6.2",
                topicTitleEn = "Construction of Tangents to a Circle (3M Board Question)",
                topicTitleKn = "ವೃತ್ತಕ್ಕೆ ಸ್ಪರ್ಶಕಗಳ ರಚನೆ",
                coreConcepts = listOf(
                    "Tangents from an external point P use Thales' semicircle theorem: angle in a semicircle is 90°, so radius ⊥ tangent automatically.",
                    "If the angle between two tangents is given as θ (e.g., 60°), construct the angle between the two radii at the centre as (180° - θ) (e.g., 120°)."
                ),
                formulasAndRules = listOf(
                    "External point P at distance d from O: Bisect OP to find midpoint M; draw auxiliary circle of radius OM.",
                    "Angle-between-tangents method: Central angle ∠AOB = 180° - θ, then draw 90° perpendiculars at A and B."
                ),
                derivationsOrMethods = listOf(
                    "1. Draw circle of radius r with centre O. Mark P such that OP = d.",
                    "2. Construct perpendicular bisector of OP; mark midpoint M.",
                    "3. With centre M and radius MO, draw dotted circle intersecting given circle at Q and R.",
                    "4. Join PQ and PR — these are the two required tangents."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Tangents Inclined at 60°",
                        problem = "Draw a pair of tangents to a circle of radius 5 cm which are inclined to each other at an angle of 60°.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Draw circle of radius 5 cm and radius OA.",
                            "Step 2 (1.0M): Construct ∠AOB = 180° - 60° = 120° at centre O.",
                            "Step 3 (1.0M): Construct 90° angles at A and B; extend rays to meet at P. ∠APB = 60°."
                        ),
                        finalAnswer = "PA and PB inclined at 60°",
                        examinerTip = "Use compass (60° & 120° arcs) rather than protractor to earn full marks."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Measuring distance OP from the circle edge instead of from centre O.",
                    "Drawing 60° at the centre instead of 180° - 60° = 120°."
                )
            )
        ),
        7 to listOf(
            TopicDetailedNote(
                topicNumber = "7.1",
                topicTitleEn = "Distance Formula & Collinearity / Triangle Classification",
                topicTitleKn = "ದೂರ ಸೂತ್ರ ಮತ್ತು ರೇಖಾಗತ ಬಿಂದುಗಳು",
                coreConcepts = listOf(
                    "Distance between P(x₁, y₁) and Q(x₂, y₂) is derived from Pythagoras theorem in the Cartesian plane.",
                    "Three points A, B, C are collinear if AB + BC = AC (sum of smaller two distances equals largest).",
                    "Point on X-axis is (x, 0); point on Y-axis is (0, y)."
                ),
                formulasAndRules = listOf(
                    "PQ = √[(x₂ - x₁)² + (y₂ - y₁)²]",
                    "Distance of P(x, y) from Origin O(0, 0): √(x² + y²)",
                    "Equidistant condition: PA = PB ⇒ PA² = PB² (squares avoid square roots)"
                ),
                derivationsOrMethods = listOf(
                    "For 'Prove square/rhombus/rectangle': find all 4 side lengths AB, BC, CD, DA AND both diagonals AC, BD.",
                    "Square: all 4 sides equal AND AC = BD. Rhombus: all 4 sides equal AND AC ≠ BD."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Equidistant Point on X-Axis",
                        problem = "Find the point on the X-axis which is equidistant from (2, -5) and (-2, 9).",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Let point on X-axis be P(x, 0). Given PA = PB ⇒ PA² = PB².",
                            "Step 2 (1.0M): (x - 2)² + (0 + 5)² = (x + 2)² + (0 - 9)²",
                            "Step 3 (1.0M): x² - 4x + 4 + 25 = x² + 4x + 4 + 81 ⇒ -8x = 56 ⇒ x = -7",
                            "Step 4 (0.5M): Required point is (-7, 0)."
                        ),
                        finalAnswer = "(-7, 0)",
                        examinerTip = "Write (-7, 0) as an ordered pair, not just x = -7."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Writing (0, x) instead of (x, 0) for a point on the X-axis.",
                    "Making sign errors inside (y₂ - y₁)² when y₁ is negative."
                )
            ),
            TopicDetailedNote(
                topicNumber = "7.2",
                topicTitleEn = "Section Formula, Midpoint & Area of a Triangle",
                topicTitleKn = "ಭಾಗ ಪ್ರಮಾಣ ಸೂತ್ರ, ಮಧ್ಯಬಿಂದು ಮತ್ತು ತ್ರಿಭುಜದ ವಿಸ್ತೀರ್ಣ",
                coreConcepts = listOf(
                    "Section Formula gives coordinates of P(x, y) dividing AB internally in ratio m₁ : m₂.",
                    "To find the ratio in which a point or axis divides AB, assume ratio k : 1 for simpler single-variable algebra.",
                    "Points of trisection divide AB into ratios 1 : 2 and 2 : 1."
                ),
                formulasAndRules = listOf(
                    "Section Formula: P(x, y) = ((m₁x₂ + m₂x₁)/(m₁ + m₂), (m₁y₂ + m₂y₁)/(m₁ + m₂))",
                    "Midpoint Formula (1:1): M = ((x₁ + x₂)/2, (y₁ + y₂)/2)",
                    "Centroid of △ABC: G = ((x₁ + x₂ + x₃)/3, (y₁ + y₂ + y₃)/3)",
                    "Area of △ABC = ½ |x₁(y₂ - y₃) + x₂(y₃ - y₁) + x₃(y₁ - y₂)|; Collinear ⇔ Area = 0"
                ),
                derivationsOrMethods = listOf(
                    "Parallelogram 4th vertex trick: Diagonals bisect each other ⇒ Midpoint(AC) = Midpoint(BD)."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Section Formula Application (3M)",
                        problem = "Find the coordinates of the point which divides the join of (-1, 7) and (4, -3) in the ratio 2 : 3.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): x₁ = -1, y₁ = 7, x₂ = 4, y₂ = -3, m₁ = 2, m₂ = 3.",
                            "Step 2 (1.0M): x = [2(4) + 3(-1)] / (2 + 3) = (8 - 3)/5 = 5/5 = 1.",
                            "Step 3 (1.0M): y = [2(-3) + 3(7)] / (2 + 3) = (-6 + 21)/5 = 15/5 = 3.",
                            "Step 4 (0.5M): Required point is P(1, 3)."
                        ),
                        finalAnswer = "(1, 3)",
                        examinerTip = "Use cross-multiplication arrows m₁ → (x₂, y₂) and m₂ → (x₁, y₁) so you never swap them."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Taking m₁x₁ + m₂x₂ instead of m₁x₂ + m₂x₁.",
                    "Leaving area of triangle negative (always apply absolute value |...|)."
                )
            )
        ),
        8 to listOf(
            TopicDetailedNote(
                topicNumber = "8.1",
                topicTitleEn = "Euclid's Division Lemma & Fundamental Theorem of Arithmetic",
                topicTitleKn = "ಯೂಕ್ಲಿಡ್ ಭಾಗಾಕಾರ ಅನುಪ್ರಮೇಯ ಮತ್ತು ಅಂಕಗಣಿತದ ಮೂಲ ಪ್ರಮೇಯ",
                coreConcepts = listOf(
                    "Euclid's Division Lemma: Given positive integers a and b, there exist unique integers q and r such that a = bq + r, 0 ≤ r < b.",
                    "Fundamental Theorem of Arithmetic: Every composite number can be expressed (factorised) as a product of primes in a unique way, apart from the order of factors.",
                    "HCF = Product of smallest powers of common prime factors; LCM = Product of greatest powers of all prime factors involved."
                ),
                formulasAndRules = listOf(
                    "Euclid's Lemma: a = bq + r, 0 ≤ r < b",
                    "HCF(a, b) × LCM(a, b) = a × b (valid strictly for 2 positive integers!)",
                    "Composite Check: Factor out common term; e.g., 7×11×13 + 13 = 13(77 + 1) = 13×78 has > 2 factors"
                ),
                derivationsOrMethods = listOf(
                    "Euclid's Algorithm for HCF(a, b): Apply a = bq + r repeatedly replacing (a, b) with (b, r) until r = 0. Last non-zero divisor is HCF."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "HCF & LCM by Prime Factorisation",
                        problem = "Find the HCF and LCM of 12, 15 and 21 by prime factorisation method.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): 12 = 2² × 3¹, 15 = 3¹ × 5¹, 21 = 3¹ × 7¹",
                            "Step 2 (0.5M): HCF(12, 15, 21) = 3¹ = 3 (common smallest power)",
                            "Step 3 (0.5M): LCM(12, 15, 21) = 2² × 3¹ × 5¹ × 7¹ = 4 × 3 × 5 × 7 = 420"
                        ),
                        finalAnswer = "HCF = 3, LCM = 420",
                        examinerTip = "Note that for 3 numbers, HCF × LCM ≠ product of 3 numbers!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Confusing HCF (smallest power of common primes) with LCM (largest power of all primes)."
                )
            ),
            TopicDetailedNote(
                topicNumber = "8.2",
                topicTitleEn = "Proving Irrationality (√2, √3, √5, a + b√p) & Decimal Expansions",
                topicTitleKn = "ಅಭಾಗಲಬ್ಧ ಸಂಖ್ಯೆಗಳ ಸಾಧನೆ ಮತ್ತು ದಶಮಾಂಶ ವಿಸ್ತರಣೆ",
                coreConcepts = listOf(
                    "Theorem: If prime p divides a², then p divides a (where a is a positive integer).",
                    "Proof by Contradiction assumes the number is rational (p/q with coprime p, q; q ≠ 0) and arrives at a common factor > 1 or irrational = rational.",
                    "A rational number x = p/q (in lowest form) has a terminating decimal expansion iff q = 2ⁿ · 5ᵐ."
                ),
                formulasAndRules = listOf(
                    "Terminating decimal: Denominator q = 2ⁿ × 5ᵐ (terminates after max(n, m) decimal places)",
                    "Non-terminating repeating decimal: q has any prime factor other than 2 or 5",
                    "Composite irrational proof (e.g. 3 + 2√5): Isolate √5 = (p - 3q)/(2q) ⇒ Irrational = Rational (contradiction)"
                ),
                derivationsOrMethods = listOf(
                    "For √p proofs (2M/3M): Always declare 'p and q are coprime integers, q ≠ 0', square to show p divides numerator², deduce p divides numerator, substitute back to show p divides denominator, state contradiction."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Prove 3 + 2√5 is Irrational (2M)",
                        problem = "Prove that 3 + 2√5 is irrational (given √5 is irrational).",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Assume 3 + 2√5 is rational ⇒ 3 + 2√5 = a/b (a, b coprime integers, b ≠ 0).",
                            "Step 2 (1.0M): Rearrange: 2√5 = a/b - 3 = (a - 3b)/b ⇒ √5 = (a - 3b) / (2b).",
                            "Step 3 (0.5M): RHS (a - 3b)/(2b) is rational since a, b are integers, contradicting √5 is irrational. Hence 3 + 2√5 is irrational."
                        ),
                        finalAnswer = "Hence proved 3 + 2√5 is irrational",
                        examinerTip = "Never square both sides for 3 + 2√5 — simply isolate √5 on the LHS!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Forgetting to reduce p/q to lowest form before checking q = 2ⁿ·5ᵐ for terminating decimals."
                )
            )
        )
    )
}
