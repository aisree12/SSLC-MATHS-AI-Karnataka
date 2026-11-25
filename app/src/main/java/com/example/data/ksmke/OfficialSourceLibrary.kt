package com.example.data.ksmke

import com.example.domain.ChapterCorpusAudit
import com.example.domain.CorpusAuditCriterion
import com.example.domain.CorpusCriterionStatus
import com.example.domain.OfficialSourceRecord
import com.example.domain.OfficialSourceType
import com.example.domain.SyllabusCertificationState

/**
 * OFFICIAL SOURCE LIBRARY (Library 1 of Dual-Library Architecture)
 *
 * Strictly stores original document provenance, KTBS textbook editions,
 * KSEAB question paper/key-answer references, DIKSHA Karnataka Class 10 resources,
 * and an HONEST 6-criterion corpus certification audit (distinguishing curated summaries
 * and syllabus page indexes from full verbatim textbook ingestion).
 */
object OfficialSourceLibrary {

    val officialDocuments: List<OfficialSourceRecord> = listOf(
        OfficialSourceRecord(
            id = "ktbs_math_part1_en",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS Class 10 Mathematics Part 1 (English Medium)",
            languageMedium = "English",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "Official 2026–27 chapters 1–7: Real Numbers (pp. 1–9), Polynomials (10–23), Pair of Linear Equations in Two Variables (24–37), Quadratic Equations (38–47), Arithmetic Progressions (48–71), Triangles (72–96), Coordinate Geometry (98–111). English section headings and exercise IDs are indexed.",
            provenanceAndPermissionNote = "Reference index only (copyrighted full KTBS PDF pages are NOT bundled verbatim; app holds curated topic notes & proofs).",
            verificationState = "Chapter/Section Index + Exercise IDs (Full Source Audit Pending)"
        ),
        OfficialSourceRecord(
            id = "ktbs_math_part2_en",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS Class 10 Mathematics Part 2 (English Medium)",
            languageMedium = "English",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "Official 2026–27 Part 2: Introduction to Trigonometry, Some Applications of Trigonometry, Circles, Areas Related to Circles, Surface Areas and Volumes, Statistics, Probability.",
            provenanceAndPermissionNote = "Reference index only (copyrighted full KTBS PDF pages are NOT bundled verbatim; app holds curated topic notes & formulas).",
            verificationState = "Index + Curated Notes (Full Verbatim Corpus Pending)"
        ),
        OfficialSourceRecord(
            id = "ktbs_math_kn",
            sourceType = OfficialSourceType.KTBS_TEXTBOOK,
            title = "KTBS 10ನೇ ತರಗತಿ ಗಣಿತ ಭಾಗ 1 & 2 (ಕನ್ನಡ ಮಾಧ್ಯಮ)",
            languageMedium = "Kannada (ಕನ್ನಡ)",
            editionOrYear = "KSEAB / KTBS Revised 2026-27",
            chaptersCovered = "All 14 Kannada chapter titles and printed page ranges are verified from the two Kannada contents pages; section-title and exercise-by-exercise parity remain pending.",
            provenanceAndPermissionNote = "Kannada chapter titles and printed page ranges are indexed; exact Kannada section names, exercise numbering, examples, diagrams and full prose parity remain to be compared.",
            verificationState = "Kannada Chapter Titles + Page Ranges Verified (Section/Exercise Parity Pending)"
        ),
        OfficialSourceRecord(
            id = "kseab_pyq_archive_2015_2026",
            sourceType = OfficialSourceType.KSEAB_BOARD_PAPER,
            title = "KSEAB SSLC Mathematics Papers & Official Key Answers (81E Series)",
            languageMedium = "English & Kannada",
            editionOrYear = "2015 – 2026 Main, Supplementary & Preparatory (Curated Subset)",
            chaptersCovered = "Legacy tagged questions are remapped to official chapter IDs; legacy Constructions-only prompts are retained in a separate archive.",
            provenanceAndPermissionNote = "Curated question references and marking-step data are used for recurrence statistics; permission to redistribute source paper text is not established.",
            verificationState = "Curated remapped item bank (not a full decade archive)"
        ),
        OfficialSourceRecord(
            id = "kseab_mqp_2026_27",
            sourceType = OfficialSourceType.KSEAB_BOARD_PAPER,
            title = "KSEAB 2026-27 Model Question Papers (80 Marks, 38 Questions)",
            languageMedium = "English & Kannada",
            editionOrYear = "2026-27 Cycle (MQP Blueprint Aligned)",
            chaptersCovered = "Full 80M / 38Q / 195-min Blueprint",
            provenanceAndPermissionNote = "Official model pattern used strictly for format alignment and marking weights.",
            verificationState = "Blueprint Format Aligned"
        ),
        OfficialSourceRecord(
            id = "diksha_ka_grade10_math",
            sourceType = OfficialSourceType.DIKSHA_RESOURCE,
            title = "DIKSHA Karnataka Grade 10 Mathematics Energized Textbook Modules",
            languageMedium = "English & Kannada",
            editionOrYear = "NROER / DIKSHA CC-BY-4.0",
            chaptersCovered = "Interactive QR-linked Geometry, Constructions, Trigonometry & Statistics Modules",
            provenanceAndPermissionNote = "Open-licensed state/national topic tags mapped per topic; video/HTML5 assets require online DIKSHA portal access.",
            verificationState = "Catalog Tagged (29 Topic Refs)"
        )
    )

    /**
     * Honest 6-Criterion Textbook Corpus Certification Audit:
     * Explicitly separates what is actually bundled and verifiable in-app from what
     * still requires ingesting the complete licensed KTBS textbook pages & full exercises.
     */
    val sixCriterionCorpusAudit: List<CorpusAuditCriterion> = listOf(
        CorpusAuditCriterion(
            id = "pages_in_corpus",
            title = "1. Printed textbook page coverage",
            status = CorpusCriterionStatus.PENDING_EXTERNAL_CORPUS,
            detail = "Not verified page-by-page. Existing page ranges and curated notes are indexes only; compare every printed page against the licensed English and Kannada source books before claiming coverage."
        ),
        CorpusAuditCriterion(
            id = "examples_exercises",
            title = "2. Textbook examples and exercises",
            status = CorpusCriterionStatus.PARTIAL_CURATED,
            detail = "Curated exercise references and sample worked examples exist. Completeness, exercise numbering, subquestion counts, and answer correctness have not been independently checked against all four books."
        ),
        CorpusAuditCriterion(
            id = "theorems_proofs",
            title = "3. Theorems and proofs",
            status = CorpusCriterionStatus.PARTIAL_CURATED,
            detail = "Selected theorem notes and proof examples are present; full textbook proof inventory and exact section parity remain unverified."
        ),
        CorpusAuditCriterion(
            id = "diagrams_constructions",
            title = "4. Diagrams and constructions",
            status = CorpusCriterionStatus.PARTIAL_CURATED,
            detail = "Selected interactive labs and diagram specifications exist. No source-by-source check confirms every required textbook figure or construction."
        ),
        CorpusAuditCriterion(
            id = "en_kn_parity",
            title = "5. English and Kannada parity",
            status = CorpusCriterionStatus.PENDING_EXTERNAL_CORPUS,
            detail = "Non-empty bilingual fields do not establish translation fidelity. Kannada chapter titles, section names, examples, exercises, and mathematical terminology require direct comparison with the Kannada books."
        ),
        CorpusAuditCriterion(
            id = "editions_permissions",
            title = "6. Source edition, permissions and mathematical checks",
            status = CorpusCriterionStatus.PARTIAL_CURATED,
            detail = "Edition references are recorded. Redistribution permission is not established, and selected deterministic checks do not certify all worked answers or marking schemes."
        )
    )

    data class TopicSourceMapping(
        val sourcePageRef: String,
        val exerciseRef: String,
        val dikshaModuleTitle: String
    )

    /**
     * Reference mapping of KTBS page ranges, exercise numbers, and DIKSHA tags by Topic Number.
     * Note: These are provenance pointers to textbook sections, NOT verbatim copyrighted PDF dumps.
     */
    @Deprecated("Legacy source pointers are not valid for the 2026–27 official chapter sequence")
    val legacyTopicSourceIndex: Map<String, TopicSourceMapping> = mapOf(
        "1.1" to TopicSourceMapping("KTBS Part 1, pp. 1–7", "Ex 1.1 (Q1–Q4)", "DIKSHA KA-10: Exploring AP Sequences"),
        "1.2" to TopicSourceMapping("KTBS Part 1, pp. 8–15", "Ex 1.2 (Q1–Q20)", "DIKSHA KA-10: nth Term of an AP"),
        "1.3" to TopicSourceMapping("KTBS Part 1, pp. 16–26", "Ex 1.3 (Q1–Q20), Ex 1.4", "DIKSHA KA-10: Sum of First n Terms (Sn)"),
        "2.1" to TopicSourceMapping("KTBS Part 1, pp. 27–39", "Ex 2.1, Ex 2.2 (Q1–Q10)", "DIKSHA KA-10: Thales (BPT) Visual Proof"),
        "2.2" to TopicSourceMapping("KTBS Part 1, pp. 40–58", "Ex 2.3, Ex 2.4, Ex 2.5", "DIKSHA KA-10: Similarity Criteria & Area Ratio"),
        "3.1" to TopicSourceMapping("KTBS Part 1, pp. 59–70", "Ex 3.1, Ex 3.2 (Q1–Q7)", "DIKSHA KA-10: Graphical Solutions & Ratios"),
        "3.2" to TopicSourceMapping("KTBS Part 1, pp. 71–88", "Ex 3.3, Ex 3.4, Ex 3.5, Ex 3.6", "DIKSHA KA-10: Elimination & Word Models"),
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
        "14.1" to TopicSourceMapping("KTBS Part 2, pp. 293–312", "Ex 14.1 (Q1–Q25), Ex 14.2", "DIKSHA KA-10: Coins, Dice & Deck Probability"),
        "15.1" to TopicSourceMapping("KTBS Part 2, pp. 313–326", "Ex 15.1, Ex 15.2", "DIKSHA KA-10: Combined Solids SA & Volume"),
        "15.2" to TopicSourceMapping("KTBS Part 2, pp. 327–342", "Ex 15.3, Ex 15.4", "DIKSHA KA-10: Recasting & Frustum of Cone")
    )

    fun auditChapter(chapterNumber: Int): ChapterCorpusAudit {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val supp = KsmkeSupplementLibrary.byOfficialChapter[chapterNumber]
        val exRefs = supp?.exercises?.joinToString(", ") { it.exerciseId }?.let { "Legacy references only: $it" }
            ?: ch.detailedTopics.mapNotNull { legacyTopicSourceIndex[it.topicNumber]?.exerciseRef }.joinToString(", ")
                .ifBlank { "Mapped in KTBS Index" }
        val status = SyllabusCertificationState.PARTIAL_CURATED
        return ChapterCorpusAudit(
            chapterNumber = ch.chapterNumber,
            ktbsPageRange = ch.pageRange,
            topicCount = ch.detailedTopics.size,
            workedExamplesInApp = ch.solvedExamples.size + ch.detailedTopics.sumOf { it.workedExamples.size },
            theoremsInApp = ch.theorems.size,
            exerciseSetsIndexed = exRefs,
            enKnParityNote = "Chapter titles and printed page ranges are indexed from both contents pages; detailed translation fidelity has not been independently audited.",
            sourceEditionAndPermission = ch.textbookEdition,
            certificationState = status
        )
    }
}
