package com.example.data.ksmke

import com.example.domain.OfficialSourceRecord
import com.example.domain.OfficialSourceType

/**
 * OFFICIAL SOURCE LIBRARY (Library 1 of Dual-Library Architecture)
 *
 * Strictly stores original document provenance, KTBS textbook editions,
 * KSEAB question paper/key-answer references, and DIKSHA Karnataka Class 10 resources.
 * Kept architecturally separate from the AI Teaching Library (AiTeachingLibrary.kt).
 */
object OfficialSourceLibrary {

    val officialDocuments: List<OfficialSourceRecord> = listOf(
        OfficialSourceRecord(
            id = "ktbs_math_part1_en",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS Class 10 Mathematics Part 1 (English Medium)",
            languageMedium = "English",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "Chapters 1–7 (AP, Triangles, Linear Pairs, Circles, Areas Related to Circles, Constructions, Coordinate Geometry)",
            provenanceAndPermissionNote = "Karnataka Textbook Society prescribed syllabus index & page verification registry.",
            verificationState = "Page-Indexed (pp. 1–154)"
        ),
        OfficialSourceRecord(
            id = "ktbs_math_part2_en",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS Class 10 Mathematics Part 2 (English Medium)",
            languageMedium = "English",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "Chapters 8–15 (Real Numbers, Polynomials, Quadratic Equations, Trigonometry, Heights & Distances, Statistics, Probability, Surface Areas & Volumes)",
            provenanceAndPermissionNote = "Karnataka Textbook Society prescribed syllabus index & page verification registry.",
            verificationState = "Page-Indexed (pp. 155–342)"
        ),
        OfficialSourceRecord(
            id = "ktbs_math_kn",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS 10ನೇ ತರಗತಿ ಗಣಿತ ಭಾಗ 1 & 2 (ಕನ್ನಡ ಮಾಧ್ಯಮ)",
            languageMedium = "Kannada (ಕನ್ನಡ)",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "ಅಧ್ಯಾಯಗಳು 1–15 (ಸಂಪೂರ್ಣ ಪಠ್ಯಕ್ರಮ)",
            provenanceAndPermissionNote = "Official Kannada medium terminology, theorem statements, and exercise mapping.",
            verificationState = "Bilingual Terminologies Synced"
        ),
        OfficialSourceRecord(
            id = "kseab_pyq_archive_2015_2026",
            sourceType = OfficialSourceType.KSEAB_BOARD_PAPER,
            title = "KSEAB SSLC Mathematics Papers & Official Key Answers (81E Series)",
            languageMedium = "English & Kannada",
            editionOrYear = "2015 – 2026 Main, Supplementary & Preparatory",
            chaptersCovered = "All 15 Chapters • Subject Code 81E",
            provenanceAndPermissionNote = "Public examination papers and official key scheme points used for factual recurrence classification.",
            verificationState = "Verified Blueprint Archive"
        ),
        OfficialSourceRecord(
            id = "kseab_mqp_2026_27",
            sourceType = OfficialSourceType.KSEAB_BOARD_PAPER,
            title = "KSEAB 2026-27 Model Question Papers (80 Marks, 38 Questions)",
            languageMedium = "English & Kannada",
            editionOrYear = "2026-27 Cycle (MQP-01 & MQP-02)",
            chaptersCovered = "Full 80M / 38Q / 195-min Blueprint",
            provenanceAndPermissionNote = "Official model pattern used strictly for format alignment and marking weights.",
            verificationState = "Active Exam Pattern"
        ),
        OfficialSourceRecord(
            id = "diksha_ka_grade10_math",
            sourceType = OfficialSourceType.DIKSHA_RESOURCE,
            title = "DIKSHA Karnataka Grade 10 Mathematics Energized Textbook Modules",
            languageMedium = "English & Kannada",
            editionOrYear = "NROER / DIKSHA CC-BY-4.0",
            chaptersCovered = "Interactive QR-linked Geometry, Constructions, Trigonometry & Statistics Modules",
            provenanceAndPermissionNote = "Open-licensed reusable state/national digital learning modules mapped by chapter and topic.",
            verificationState = "Topic-Tagged (26 Modules)"
        )
    )

    data class TopicSourceMapping(
        val sourcePageRef: String,
        val exerciseRef: String,
        val dikshaModuleTitle: String
    )

    /**
     * Exact page-by-page textbook source mapping & DIKSHA tags indexed by Topic Number (e.g. "1.1").
     */
    val topicSourceIndex: Map<String, TopicSourceMapping> = mapOf(
        "1.1" to TopicSourceMapping("KTBS Part 1, pp. 1–7", "Ex 1.1 (Q1–Q4)", "DIKSHA KA-10: Exploring AP Sequences"),
        "1.2" to TopicSourceMapping("KTBS Part 1, pp. 8–15", "Ex 1.2 (Q1–Q20)", "DIKSHA KA-10: nth Term of an AP"),
        "1.3" to TopicSourceMapping("KTBS Part 1, pp. 16–26", "Ex 1.3 (Q1–Q20)", "DIKSHA KA-10: Sum of First n Terms (Sn)"),
        "2.1" to TopicSourceMapping("KTBS Part 1, pp. 27–39", "Ex 2.1, Ex 2.2 (Q1–Q10)", "DIKSHA KA-10: Thales (BPT) Visual Proof"),
        "2.2" to TopicSourceMapping("KTBS Part 1, pp. 40–58", "Ex 2.3, Ex 2.4, Ex 2.5", "DIKSHA KA-10: Similarity Criteria & Area Ratio"),
        "3.1" to TopicSourceMapping("KTBS Part 1, pp. 59–70", "Ex 3.1, Ex 3.2 (Q1–Q7)", "DIKSHA KA-10: Graphical Solutions & Ratios"),
        "3.2" to TopicSourceMapping("KTBS Part 1, pp. 71–88", "Ex 3.3, Ex 3.4, Ex 3.5", "DIKSHA KA-10: Elimination & Word Models"),
        "4.1" to TopicSourceMapping("KTBS Part 1, pp. 89–95", "Ex 4.1 (Q1–Q4)", "DIKSHA KA-10: Circle Tangent Perpendicularity"),
        "4.2" to TopicSourceMapping("KTBS Part 1, pp. 96–104", "Ex 4.2 (Q1–Q13)", "DIKSHA KA-10: Tangents from External Point"),
        "5.1" to TopicSourceMapping("KTBS Part 1, pp. 105–113", "Ex 5.1, Ex 5.2 (Q1–Q6)", "DIKSHA KA-10: Arc Length & Sector Area"),
        "5.2" to TopicSourceMapping("KTBS Part 1, pp. 114–122", "Ex 5.2 (Q7–Q14), Ex 5.3", "DIKSHA KA-10: Segments & Combined Plane Areas"),
        "6.1" to TopicSourceMapping("KTBS Part 1, pp. 123–129", "Ex 6.1 (Q1–Q7)", "DIKSHA KA-10: Similar Triangle Construction"),
        "6.2" to TopicSourceMapping("KTBS Part 1, pp. 130–134", "Ex 6.2 (Q1–Q7)", "DIKSHA KA-10: Tangent Pair Compass Steps"),
        "7.1" to TopicSourceMapping("KTBS Part 1, pp. 135–143", "Ex 7.1 (Q1–Q10)", "DIKSHA KA-10: Cartesian Distance & Collinearity"),
        "7.2" to TopicSourceMapping("KTBS Part 1, pp. 144–154", "Ex 7.2, Ex 7.3", "DIKSHA KA-10: Section Formula & Triangle Area"),
        "8.1" to TopicSourceMapping("KTBS Part 2, pp. 155–162", "Ex 8.1, Ex 8.2", "DIKSHA KA-10: Euclid's Lemma & Prime Factors"),
        "8.2" to TopicSourceMapping("KTBS Part 2, pp. 163–170", "Ex 8.3, Ex 8.4", "DIKSHA KA-10: Contradiction Proof of Irrationality"),
        "9.1" to TopicSourceMapping("KTBS Part 2, pp. 171–178", "Ex 9.1 (Q1)", "DIKSHA KA-10: Zeroes on Polynomial Graphs"),
        "9.2" to TopicSourceMapping("KTBS Part 2, pp. 179–190", "Ex 9.2, Ex 9.3", "DIKSHA KA-10: α, β Relations & Long Division"),
        "10.1" to TopicSourceMapping("KTBS Part 2, pp. 191–203", "Ex 10.1, Ex 10.2", "DIKSHA KA-10: Quadratic Factorisation"),
        "10.2" to TopicSourceMapping("KTBS Part 2, pp. 204–216", "Ex 10.3, Ex 10.4", "DIKSHA KA-10: Sridharacharya Formula & Δ"),
        "11.1" to TopicSourceMapping("KTBS Part 2, pp. 217–229", "Ex 11.1, Ex 11.2", "DIKSHA KA-10: Trig Ratios & Standard Angles"),
        "11.2" to TopicSourceMapping("KTBS Part 2, pp. 230–244", "Ex 11.3, Ex 11.4", "DIKSHA KA-10: Complementary & Trig Identities"),
        "12.1" to TopicSourceMapping("KTBS Part 2, pp. 245–258", "Ex 12.1 (Q1–Q16)", "DIKSHA KA-10: Elevation & Depression Diagrams"),
        "13.1" to TopicSourceMapping("KTBS Part 2, pp. 259–281", "Ex 13.1, Ex 13.2, Ex 13.3", "DIKSHA KA-10: Grouped Mean, Mode & Median"),
        "13.2" to TopicSourceMapping("KTBS Part 2, pp. 282–292", "Ex 13.4 (Q1–Q3)", "DIKSHA KA-10: Less-Than & More-Than Ogives"),
        "14.1" to TopicSourceMapping("KTBS Part 2, pp. 293–312", "Ex 14.1 (Q1–Q25)", "DIKSHA KA-10: Coins, Dice & Deck Probability"),
        "15.1" to TopicSourceMapping("KTBS Part 2, pp. 313–326", "Ex 15.1, Ex 15.2", "DIKSHA KA-10: Combined Solids SA & Volume"),
        "15.2" to TopicSourceMapping("KTBS Part 2, pp. 327–342", "Ex 15.3, Ex 15.4", "DIKSHA KA-10: Recasting & Frustum of Cone")
    )
}
