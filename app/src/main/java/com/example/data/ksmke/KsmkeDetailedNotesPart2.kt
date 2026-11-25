package com.example.data.ksmke

import com.example.domain.SolvedExample
import com.example.domain.TopicDetailedNote

internal object KsmkeDetailedNotesPart2 {

    val byChapter: Map<Int, List<TopicDetailedNote>> = mapOf(
        9 to listOf(
            TopicDetailedNote(
                topicNumber = "9.1",
                topicTitleEn = "Degree, Zeroes & Geometrical Meaning of a Polynomial",
                topicTitleKn = "ಬಹುಪದೋಕ್ತಿಯ ಮಹತ್ತಮ ಘಾತ, ಶೂನ್ಯತೆಗಳು ಮತ್ತು ರೇಖಾಗಣಿತದ ಅರ್ಥ",
                coreConcepts = listOf(
                    "Degree of a polynomial p(x) is the highest power of x with non-zero coefficient (must be a non-negative integer!).",
                    "Linear polynomial (degree 1): ax + b (max 1 zero); Quadratic (degree 2): ax² + bx + c (max 2 zeroes); Cubic (degree 3): max 3 zeroes.",
                    "Geometrical Meaning: Number of zeroes of y = p(x) equals the number of times the graph cuts (intersects) the X-axis."
                ),
                formulasAndRules = listOf(
                    "Zero of linear p(x) = ax + b is x = -b / a",
                    "A real number k is a zero of p(x) iff p(k) = 0",
                    "Parabola y = ax² + bx + c opens UPWARDS if a > 0 and DOWNWARDS if a < 0"
                ),
                derivationsOrMethods = listOf(
                    "Graph Reading Rule (1M MCQ): Count ONLY points where the curve intersects or touches the X-axis (ignore Y-axis intersections)."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Verify Zeroes & Find Unknown Constant",
                        problem = "If x = 2 is a zero of p(x) = 3x² - kx + 4, find k.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Since x = 2 is a zero, p(2) = 0.",
                            "Step 2 (0.5M): 3(2)² - k(2) + 4 = 0 ⇒ 12 - 2k + 4 = 0",
                            "Step 3 (0.5M): 16 - 2k = 0 ⇒ 2k = 16 ⇒ k = 8"
                        ),
                        finalAnswer = "k = 8",
                        examinerTip = "Check exponents carefully: expressions like √x + 2 or 1/x + 3 are NOT polynomials."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Counting Y-axis intersections as zeroes of p(x).",
                    "Mixing up leading coefficient with degree of polynomial."
                )
            ),
            TopicDetailedNote(
                topicNumber = "9.2",
                topicTitleEn = "Zeroes–Coefficients Relationship & Division Algorithm",
                topicTitleKn = "ಶೂನ್ಯತೆಗಳು ಮತ್ತು ಗುಣಾಂಕಗಳ ನಡುವಿನ ಸಂಬಂಧ ಹಾಗೂ ಭಾಗಾಕಾರ ಅಲ್ಗಾರಿದಂ",
                coreConcepts = listOf(
                    "For quadratic polynomial p(x) = ax² + bx + c (a ≠ 0) with zeroes α and β: α + β = -b/a and αβ = c/a.",
                    "Division Algorithm: For polynomials p(x) and g(x) ≠ 0, p(x) = g(x)·q(x) + r(x) where r(x) = 0 or deg r(x) < deg g(x)."
                ),
                formulasAndRules = listOf(
                    "Sum of zeroes: α + β = -b/a = -(coeff of x)/(coeff of x²)",
                    "Product of zeroes: αβ = c/a = (constant term)/(coeff of x²)",
                    "Forming quadratic polynomial: k[x² - (α + β)x + αβ]",
                    "Cubic polynomial ax³ + bx² + cx + d: α + β + γ = -b/a, αβ + βγ + γα = c/a, αβγ = -d/a"
                ),
                derivationsOrMethods = listOf(
                    "Symmetric identity trick: α² + β² = (α + β)² - 2αβ; 1/α + 1/β = (α + β)/(αβ)."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Find Zeroes & Verify Coefficients (3M)",
                        problem = "Find the zeroes of p(x) = x² - 2x - 8 and verify the relationship between zeroes and coefficients.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): x² - 2x - 8 = x² - 4x + 2x - 8 = (x - 4)(x + 2) = 0 ⇒ α = 4, β = -2.",
                            "Step 2 (1.0M): Sum: α + β = 4 + (-2) = 2; Formula -b/a = -(-2)/1 = 2 (Verified).",
                            "Step 3 (1.0M): Product: αβ = 4 × (-2) = -8; Formula c/a = -8/1 = -8 (Verified)."
                        ),
                        finalAnswer = "Zeroes: 4, -2 (Verified)",
                        examinerTip = "Write BOTH parts: finding zeroes AND showing LHS = RHS for sum and product."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Using + (α + β)x instead of - (α + β)x when forming the quadratic polynomial.",
                    "Forgetting to arrange terms in descending powers of x before long division."
                )
            )
        ),
        10 to listOf(
            TopicDetailedNote(
                topicNumber = "10.1",
                topicTitleEn = "Standard Form, Factorisation & Completing the Square",
                topicTitleKn = "ವರ್ಗ ಸಮೀಕರಣಗಳ ಆದರ್ಶ ರೂಪ ಮತ್ತು ಅಪವರ್ತನ ವಿಧಾನ",
                coreConcepts = listOf(
                    "Standard form of a quadratic equation: ax² + bx + c = 0 where a, b, c are real numbers and a ≠ 0.",
                    "Roots of ax² + bx + c = 0 are the zeroes of the quadratic polynomial ax² + bx + c.",
                    "Factorisation (splitting the middle term) splits b into p + q such that p × q = a × c."
                ),
                formulasAndRules = listOf(
                    "Standard Form: ax² + bx + c = 0 (a ≠ 0)",
                    "Zero Product Rule: If (px - r)(qx - s) = 0, then x = r/p or x = s/q",
                    "Middle-term split check: p + q = b and p · q = a · c"
                ),
                derivationsOrMethods = listOf(
                    "1. Clear fractions and brackets to bring equation into ax² + bx + c = 0.",
                    "2. Compute product a·c; find integer pair (p, q) whose sum is b.",
                    "3. Factor by grouping into two linear factors and equate each to zero."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Factorisation by Splitting Middle Term",
                        problem = "Find the roots of 6x² - x - 2 = 0 by factorisation.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Product a·c = 6 × (-2) = -12; Sum b = -1 ⇒ split -x as -4x + 3x.",
                            "Step 2 (1.0M): 6x² - 4x + 3x - 2 = 2x(3x - 2) + 1(3x - 2) = (3x - 2)(2x + 1) = 0",
                            "Step 3 (0.5M): 3x - 2 = 0 or 2x + 1 = 0 ⇒ x = 2/3 or x = -1/2"
                        ),
                        finalAnswer = "x = 2/3, -1/2",
                        examinerTip = "Substitute x = 2/3 back mentally to ensure LHS = 0."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Cancelling variable x from both sides of x² = 5x (losing root x = 0!). Always write x(x - 5) = 0."
                )
            ),
            TopicDetailedNote(
                topicNumber = "10.2",
                topicTitleEn = "Quadratic Formula (Sridharacharya Formula), Discriminant & Nature of Roots",
                topicTitleKn = "ಶ್ರೀಧರಾಚಾರ್ಯ ಸೂತ್ರ, ಶೋಧಕ ಮತ್ತು ಮೂಲಗಳ ಸ್ವಭಾವ",
                coreConcepts = listOf(
                    "When factorisation is difficult (e.g., irrational roots), use the Quadratic Formula.",
                    "Discriminant Δ = b² - 4ac governs the nature of roots completely without even solving for x.",
                    "Real & Equal roots occur when Δ = 0 (frequently asked for finding unknown k!)."
                ),
                formulasAndRules = listOf(
                    "Discriminant: Δ = b² - 4ac",
                    "Quadratic Formula: x = [-b ± √(b² - 4ac)] / (2a)",
                    "Δ > 0 ⇒ Two distinct real roots",
                    "Δ = 0 ⇒ Two equal real roots (each equals -b / 2a)",
                    "Δ < 0 ⇒ No real roots (imaginary)"
                ),
                derivationsOrMethods = listOf(
                    "Derivation by Completing the Square: ax² + bx + c = 0 ⇒ x² + (b/a)x = -c/a ⇒ (x + b/2a)² = (b² - 4ac)/(4a²) ⇒ x = [-b ± √(b² - 4ac)]/(2a)."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Find k for Equal Roots (Guaranteed PYQ Type)",
                        problem = "Find the value of k for which 2x² + kx + 3 = 0 has two equal real roots.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Compare with ax² + bx + c = 0: a = 2, b = k, c = 3.",
                            "Step 2 (0.5M): For equal real roots, Δ = b² - 4ac = 0.",
                            "Step 3 (1.0M): k² - 4(2)(3) = 0 ⇒ k² = 24 ⇒ k = ±√24 = ±2√6."
                        ),
                        finalAnswer = "k = ±2√6",
                        examinerTip = "Always include BOTH + and - (±) when taking square root of k²!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Squaring negative b incorrectly: (-b)² is ALWAYS positive.",
                    "Dividing only √Δ by 2a instead of the whole numerator (-b ± √Δ)."
                )
            )
        ),
        11 to listOf(
            TopicDetailedNote(
                topicNumber = "11.1",
                topicTitleEn = "Trigonometric Ratios & Standard Angles (0° to 90°)",
                topicTitleKn = "ತ್ರಿಕೋನಮಿತಿಯ ಅನುಪಾತಗಳು ಮತ್ತು ವಿಶಿಷ್ಟ ಕೋನಗಳ ಬೆಲೆಗಳು",
                coreConcepts = listOf(
                    "In right △ABC (∠B = 90°) for acute angle θ at A: Opposite = BC, Adjacent = AB, Hypotenuse = AC.",
                    "Reciprocal pairs: sin θ · cosec θ = 1, cos θ · sec θ = 1, tan θ · cot θ = 1.",
                    "Standard angles (0°, 30°, 45°, 60°, 90°) follow the √0/2, √1/2, √2/2, √3/2, √4/2 pattern for sine."
                ),
                formulasAndRules = listOf(
                    "sin θ = Opp/Hyp, cos θ = Adj/Hyp, tan θ = Opp/Adj = sin θ / cos θ",
                    "sin 0°=0, sin 30°=1/2, sin 45°=1/√2, sin 60°=√3/2, sin 90°=1",
                    "cos is reverse of sin: cos 0°=1, cos 30°=√3/2, cos 45°=1/√2, cos 60°=1/2, cos 90°=0",
                    "tan 0°=0, tan 30°=1/√3, tan 45°=1, tan 60°=√3, tan 90°=Not defined"
                ),
                derivationsOrMethods = listOf(
                    "Given one ratio (e.g., sin A = 3/5), draw right triangle with Opp = 3k, Hyp = 5k, compute Adj = 4k by Pythagoras theorem, then read all other ratios."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Evaluating Standard Angle Expression (2M)",
                        problem = "Evaluate: 2 tan²45° + cos²30° - sin²60°.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): tan 45° = 1, cos 30° = √3/2, sin 60° = √3/2.",
                            "Step 2 (0.5M): = 2(1)² + (√3/2)² - (√3/2)² = 2 + 3/4 - 3/4",
                            "Step 3 (0.5M): = 2"
                        ),
                        finalAnswer = "2",
                        examinerTip = "Remember sin²θ means (sin θ)², NOT sin(θ²)."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Taking adjacent side without checking WHICH acute angle (∠A or ∠C) is referenced.",
                    "Swapping tan 30° (1/√3) and tan 60° (√3)."
                )
            ),
            TopicDetailedNote(
                topicNumber = "11.2",
                topicTitleEn = "Complementary Angles & Pythagorean Trigonometric Identities",
                topicTitleKn = "ಪೂರಕ ಕೋನಗಳು ಮತ್ತು ತ್ರಿಕೋನಮಿತೀಯ ನಿತ್ಯಸಮೀಕರಣಗಳು",
                coreConcepts = listOf(
                    "Complementary pairs: sin(90° - θ) = cos θ, tan(90° - θ) = cot θ, sec(90° - θ) = cosec θ.",
                    "Three fundamental identities hold for all valid acute angles θ (0° ≤ θ ≤ 90°).",
                    "For 'Prove that' identity questions (3M): convert everything to sin θ and cos θ OR factor/rationalise LHS cleanly to reach RHS."
                ),
                formulasAndRules = listOf(
                    "Identity 1: sin²θ + cos²θ = 1 (⇒ sin²θ = 1 - cos²θ, cos²θ = 1 - sin²θ)",
                    "Identity 2: 1 + tan²θ = sec²θ (⇒ sec²θ - tan²θ = 1)",
                    "Identity 3: 1 + cot²θ = cosec²θ (⇒ cosec²θ - cot²θ = 1)"
                ),
                derivationsOrMethods = listOf(
                    "Divide AB² + BC² = AC² by AC² to get sin²A + cos²A = 1; divide by AB² for 1 + tan²A = sec²A; divide by BC² for cot²A + 1 = cosec²A."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Board Identity Proof (3M)",
                        problem = "Prove that √[(1 + sin A) / (1 - sin A)] = sec A + tan A.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Multiply numerator & denominator inside root by (1 + sin A): √[(1 + sin A)² / (1 - sin²A)]",
                            "Step 2 (1.0M): Since 1 - sin²A = cos²A, LHS = √[(1 + sin A)² / cos²A] = (1 + sin A) / cos A",
                            "Step 3 (1.0M): Split fraction: 1/cos A + sin A/cos A = sec A + tan A = RHS."
                        ),
                        finalAnswer = "Hence proved sec A + tan A",
                        examinerTip = "Inside square roots, multiply top and bottom by the conjugate of the denominator!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Proving an identity by assuming LHS = RHS from Step 1 instead of transforming LHS -> RHS.",
                    "Writing sec²θ - tan²θ = -1 (it equals +1!)."
                )
            )
        ),
        12 to listOf(
            TopicDetailedNote(
                topicNumber = "12.1",
                topicTitleEn = "Heights & Distances: Angle of Elevation & Angle of Depression",
                topicTitleKn = "ಎತ್ತರ ಮತ್ತು ದೂರಗಳು: ಉನ್ನತ ಕೋನ ಮತ್ತು ಅವನತ ಕೋನ",
                coreConcepts = listOf(
                    "Line of sight: straight line from observer's eye to the object.",
                    "Angle of Elevation: angle ABOVE horizontal line of sight when looking UP.",
                    "Angle of Depression: angle BELOW horizontal line of sight when looking DOWN; equals angle of elevation from the object by alternate interior angles!"
                ),
                formulasAndRules = listOf(
                    "tan θ = Vertical Height / Horizontal Distance (used in 80% of problems)",
                    "sin θ = Vertical Height / Slant Length (kite string, ladder, cable)",
                    "cos θ = Horizontal Base / Slant Length",
                    "Use √3 ≈ 1.732 if decimal answer is requested; otherwise leave in surd form like 10√3 m"
                ),
                derivationsOrMethods = listOf(
                    "4M Board Diagram & Solution Workflow:",
                    "1. Draw neat horizontal ground, vertical tower/building, and horizontal eye-level line for depression angles (1 mark is allotted to the diagram!).",
                    "2. Mark right angle(s) 90° and define variables clearly (Let height AB = h m).",
                    "3. Apply tan θ in the inner/smaller triangle first, then outer/larger triangle.",
                    "4. Eliminate horizontal distance x to solve for h cleanly."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Broken Tree / Pole Wind Problem (3M)",
                        problem = "A tree breaks due to storm and the broken part bends so that the top of the tree touches the ground making an angle 30° with it. Distance from foot to top is 8 m. Find original height of the tree.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Let standing part = AB, broken part = AC touching ground at C with BC = 8 m, ∠ACB = 30°.",
                            "Step 2 (1.0M): tan 30° = AB / 8 ⇒ AB = 8 / √3 m.",
                            "Step 3 (1.0M): cos 30° = 8 / AC ⇒ √3/2 = 8 / AC ⇒ AC = 16 / √3 m.",
                            "Step 4 (0.5M): Total height = AB + AC = 24 / √3 = 8√3 m."
                        ),
                        finalAnswer = "8√3 m",
                        examinerTip = "Original height of a broken tree = Standing vertical part + Slant hypotenuse part!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Placing angle of depression between the slant line and the vertical pole instead of the horizontal line.",
                    "Forgetting to add observer's height or building height when applicable."
                )
            )
        ),
        13 to listOf(
            TopicDetailedNote(
                topicNumber = "13.1",
                topicTitleEn = "Mean, Mode & Median of Grouped Data (Guaranteed 3M Question)",
                topicTitleKn = "ವರ್ಗೀಕೃತ ದತ್ತಾಂಶಗಳ ಸರಾಸರಿ, ಬಹುಲಕ ಮತ್ತು ಮಧ್ಯಾಂಕ",
                coreConcepts = listOf(
                    "Class mark (mid-point) xᵢ = (Upper limit + Lower limit) / 2.",
                    "Direct Mean works best for small numbers; Assumed Mean / Step-Deviation reduces arithmetic errors for large numbers.",
                    "Modal class = class with MAXIMUM frequency f₁; Median class = class whose cumulative frequency (cf) first reaches or exceeds n/2."
                ),
                formulasAndRules = listOf(
                    "Direct Mean: x̄ = Σfᵢxᵢ / Σfᵢ",
                    "Assumed Mean: x̄ = a + (Σfᵢdᵢ / Σfᵢ) where dᵢ = xᵢ - a",
                    "Step-Deviation Mean: x̄ = a + (Σfᵢuᵢ / Σfᵢ) × h where uᵢ = (xᵢ - a)/h",
                    "Mode = l + [(f₁ - f₀) / (2f₁ - f₀ - f₂)] × h",
                    "Median = l + [(n/2 - cf) / f] × h",
                    "Empirical Relation: 3 Median = Mode + 2 Mean"
                ),
                derivationsOrMethods = listOf(
                    "Make class intervals continuous (e.g., 10–19, 20–29 -> 9.5–19.5, 19.5–29.5) before applying Mode or Median formulas!",
                    "Always verify Mode and Median answers lie INSIDE the chosen modal/median class interval [l, l+h]."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Direct Mean Table (3M)",
                        problem = "Find the mean of: 0-10 (f=3), 10-20 (f=5), 20-30 (f=9), 30-40 (f=5), 40-50 (f=3).",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Class marks xᵢ: 5, 15, 25, 35, 45.",
                            "Step 2 (1.0M): fᵢxᵢ values: 15, 75, 225, 175, 135 ⇒ Σfᵢxᵢ = 625; Σfᵢ = 25.",
                            "Step 3 (1.0M): Mean x̄ = Σfᵢxᵢ / Σfᵢ = 625 / 25 = 25."
                        ),
                        finalAnswer = "Mean = 25",
                        examinerTip = "By symmetry around 20-30, mean must be 25 — instant self-check!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Taking cf of the median class itself instead of the PRECEDING class in the Median formula.",
                    "Multiplying only numerator instead of the whole fraction [(f₁-f₀)/(2f₁-f₀-f₂)] by class size h."
                )
            ),
            TopicDetailedNote(
                topicNumber = "13.2",
                topicTitleEn = "Cumulative Frequency Distributions & Ogive Curves",
                topicTitleKn = "ಸಂಚಿತ ಆವೃತ್ತಿ ವಿತರಣೆ ಮತ್ತು ಓಜೀವ್ ರೇಖೆಗಳು",
                coreConcepts = listOf(
                    "Less-than Ogive: Plot (Upper class limit, Cumulative frequency 'less than') -> rising S-curve.",
                    "More-than Ogive: Plot (Lower class limit, Cumulative frequency 'more than') -> falling curve.",
                    "The X-coordinate of the intersection of Less-than and More-than Ogives equals the Median!"
                ),
                formulasAndRules = listOf(
                    "Less-than Ogive points: (upper limit, cumulative sum from top)",
                    "More-than Ogive points: (lower limit, cumulative sum from bottom)",
                    "Graphical Median from single Ogive: Locate n/2 on Y-axis -> draw horizontal line to curve -> drop perpendicular to X-axis."
                ),
                derivationsOrMethods = listOf(
                    "Always write Scale on the graph paper (e.g., X-axis: 1 cm = 10 units, Y-axis: 1 cm = 5 students) and draw a freehand smooth curve, not jagged straight lines."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Convert Table to Less-Than Cumulative Frequency",
                        problem = "Convert 0-10 (5), 10-20 (8), 20-30 (12) to less-than type points.",
                        markingSteps = listOf(
                            "Step 1 (1.0M): Less than 10 -> 5; Less than 20 -> 5 + 8 = 13; Less than 30 -> 13 + 12 = 25.",
                            "Step 2 (1.0M): Ordered pairs to plot: (10, 5), (20, 13), (30, 25)."
                        ),
                        finalAnswer = "(10, 5), (20, 13), (30, 25)",
                        examinerTip = "Use UPPER limits on X-axis for Less-than Ogive, and LOWER limits for More-than Ogive."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Plotting midpoints instead of upper/lower limits on Ogive X-axis."
                )
            )
        ),
        14 to listOf(
            TopicDetailedNote(
                topicNumber = "14.1",
                topicTitleEn = "Theoretical Probability, Complementary Events, Coins, Dice & Cards",
                topicTitleKn = "ಸಂಭವನೀಯತೆ, ಪೂರಕ ಘಟನೆಗಳು, ನಾಣ್ಯ, ದಾಳ ಮತ್ತು ಇಸ್ಪೀಟ್ ಎಲೆಗಳು",
                coreConcepts = listOf(
                    "Probability P(E) = (Number of outcomes favourable to E) / (Total number of all possible outcomes), assuming equally likely outcomes.",
                    "Impossible event has probability 0; Sure/Certain event has probability 1; 0 ≤ P(E) ≤ 1 always.",
                    "Sum of probabilities of all elementary events of an experiment is 1."
                ),
                formulasAndRules = listOf(
                    "P(E) = n(E) / n(S)",
                    "Complementary Event: P(E) + P(not E) = 1 ⇒ P(Ē) = 1 - P(E)",
                    "Coins: 1 coin ⇒ 2 outcomes; 2 coins ⇒ 4 (HH, HT, TH, TT); 3 coins ⇒ 8",
                    "Dice: 1 die ⇒ 6 outcomes; 2 dice ⇒ 36 outcomes",
                    "52 Playing Cards: 26 Red (13 Hearts ♥, 13 Diamonds ♦), 26 Black (13 Spades ♠, 13 Clubs ♣); 12 Face cards (4 Kings, 4 Queens, 4 Jacks); 4 Aces"
                ),
                derivationsOrMethods = listOf(
                    "Always write Sample Space size n(S) first, then explicitly list favourable outcomes E to find n(E), then compute P(E) = n(E)/n(S) in simplest fraction."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Playing Card Probability (2M)",
                        problem = "One card is drawn from a well-shuffled deck of 52 cards. Find the probability of getting (i) a red face card (ii) neither an ace nor a king.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): Total cards n(S) = 52.",
                            "Step 2 (0.75M): (i) Red face cards = 3 (Hearts) + 3 (Diamonds) = 6 ⇒ P = 6/52 = 3/26.",
                            "Step 3 (0.75M): (ii) Aces + Kings = 4 + 4 = 8; remaining = 52 - 8 = 44 ⇒ P = 44/52 = 11/13."
                        ),
                        finalAnswer = "(i) 3/26, (ii) 11/13",
                        examinerTip = "Always simplify fractions (e.g., 6/52 = 3/26) for full marks."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Counting Ace as a face card (only Jack, Queen, King are face cards — 12 total, 6 red, 6 black).",
                    "Confusing 'at least one head' with 'exactly one head'."
                )
            )
        ),
        15 to listOf(
            TopicDetailedNote(
                topicNumber = "15.1",
                topicTitleEn = "Surface Area & Volume of Combinations of Solids",
                topicTitleKn = "ಘನಾಕೃತಿಗಳ ಸಂಯೋಜನೆಯ ಮೇಲ್ಮೈ ವಿಸ್ತೀರ್ಣ ಮತ್ತು ಘನಫಲ",
                coreConcepts = listOf(
                    "When two solids are joined (e.g., cone on hemisphere, tent = cylinder + cone), their joined circular base is HIDDEN — add ONLY exposed Curved Surface Areas (CSAs)!",
                    "Total Volume of a combined solid = Sum of individual volumes (unless a cavity/hemisphere is scooped out, in which case subtract cavity volume but ADD cavity CSA to surface area!)."
                ),
                formulasAndRules = listOf(
                    "Cuboid: LSA = 2h(l + b), TSA = 2(lb + bh + hl), V = lbh",
                    "Cube: LSA = 4a², TSA = 6a², V = a³",
                    "Cylinder: CSA = 2πrh, TSA = 2πr(r + h), V = πr²h",
                    "Cone: l = √(r² + h²), CSA = πrl, TSA = πr(l + r), V = (1/3)πr²h",
                    "Sphere: SA = 4πr², V = (4/3)πr³",
                    "Hemisphere: CSA = 2πr², TSA = 3πr², V = (2/3)πr³"
                ),
                derivationsOrMethods = listOf(
                    "Scooped-out Solid Rule: Surface area INCREASES by the curved surface of the scooped cavity, while Volume DECREASES by the cavity volume."
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Wooden Cylinder with Scooped Hemispheres",
                        problem = "A wooden article was made by scooping out a hemisphere from each end of a solid cylinder of height 10 cm and base radius 3.5 cm. Find its TSA.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): r = 3.5 cm, h = 10 cm.",
                            "Step 2 (1.0M): Exposed TSA = CSA(cylinder) + 2 × CSA(hemisphere) = 2πrh + 4πr² = 2πr(h + 2r)",
                            "Step 3 (1.5M): = 2 × (22/7) × 3.5 × (10 + 7) = 22 × 17 = 374 cm²"
                        ),
                        finalAnswer = "374 cm²",
                        examinerTip = "Factor out π and r before substituting decimals — (22/7)×3.5 simplifies immediately to 11!"
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Subtracting CSA when a hemisphere is scooped out (exposed inner surface ADDS area!).",
                    "Forgetting to halve diameter to get radius r = d/2."
                )
            ),
            TopicDetailedNote(
                topicNumber = "15.2",
                topicTitleEn = "Conversion of Solids & Frustum of a Cone",
                topicTitleKn = "ಘನಾಕೃತಿಗಳ ರೂಪಾಂತರ ಮತ್ತು ಶಂಕುವಿನ ಭಿನ್ನಕ",
                coreConcepts = listOf(
                    "When a metallic solid is melted and recast into another shape, TOTAL VOLUME REMAINS CONSTANT.",
                    "Frustum of a cone (bucket/glass shape) is formed by slicing off the top small cone parallel to the base."
                ),
                formulasAndRules = listOf(
                    "Recasting Rule: Volume of original solid = N × Volume of each small recast solid",
                    "Frustum Slant Height: l = √[h² + (r₁ - r₂)²]",
                    "Frustum CSA: π(r₁ + r₂)l",
                    "Frustum TSA: π(r₁ + r₂)l + πr₁² + πr₂² (open bucket omits top circle!)",
                    "Frustum Volume: (1/3)πh(r₁² + r₂² + r₁r₂)"
                ),
                derivationsOrMethods = listOf(
                    "Recast Shortcut: Equate π formulas algebraically and cancel π on BOTH sides before doing multiplication!"
                ),
                workedExamples = listOf(
                    SolvedExample(
                        title = "Bucket Frustum Capacity (4M/5M)",
                        problem = "A drinking glass is in the shape of a frustum of a cone of height 14 cm. The diameters of its two circular ends are 4 cm and 2 cm. Find its capacity.",
                        markingSteps = listOf(
                            "Step 1 (0.5M): h = 14 cm, r₁ = 4/2 = 2 cm, r₂ = 2/2 = 1 cm.",
                            "Step 2 (1.0M): Volume = (1/3)πh(r₁² + r₂² + r₁r₂)",
                            "Step 3 (1.5M): V = (1/3) × (22/7) × 14 × (2² + 1² + 2×1) = (44/3) × 7 = 308/3 cm³ = 102⅔ cm³"
                        ),
                        finalAnswer = "102⅔ cm³ (≈ 102.67 cm³)",
                        examinerTip = "Check whether radius or diameter is given before plugging into r₁ and r₂."
                    )
                ),
                examMistakesToAvoid = listOf(
                    "Using (r₁ + r₂)² instead of r₁² + r₂² + r₁r₂ in Frustum Volume.",
                    "Writing cm² for volume instead of cm³."
                )
            )
        )
    )
}
