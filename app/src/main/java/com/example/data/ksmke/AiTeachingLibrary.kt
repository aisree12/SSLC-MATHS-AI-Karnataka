package com.example.data.ksmke

/**
 * AI TEACHING LIBRARY (Library 2 of Dual-Library Architecture)
 *
 * Strictly separated from the Official Source Library (OfficialSourceLibrary.kt).
 * Stores original bilingual pedagogical explanations, 6-layer KSMKE indexing by
 * chapter/topic/page/difficulty, and patient AI teaching sequences.
 */
object AiTeachingLibrary {

    data class KsmkeLayerSpec(
        val layerNumber: Int,
        val name: String,
        val teachingRole: String
    )

    data class IndexedConceptNode(
        val topicNumber: String,
        val chapterNumber: Int,
        val conceptTitle: String,
        val pageRef: String,
        val difficultyBand: String,
        val recommendedTeachingHook: String
    )

    val sixLayerEngine: List<KsmkeLayerSpec> = listOf(
        KsmkeLayerSpec(1, "Textbook Layer", "Curated 29-topic KTBS study notes, all 5 core SSLC theorem proofs, and page/exercise reference index (full verbatim pages pending external corpus)."),
        KsmkeLayerSpec(2, "Mathematics Layer", "Exact formulas, derivations, proofs, and deterministic MathVerificationEngine checks."),
        KsmkeLayerSpec(3, "Examination Layer", "2015–2026 KSEAB 81E curated PYQ bank (61+ items) and 80M/38Q 2026-27 Model Question Paper blueprint."),
        KsmkeLayerSpec(4, "Marking Layer", "IndependentMarkingEngine 5-pillar KSEAB step scoring (Given, Formula, Steps, Answer, Units)."),
        KsmkeLayerSpec(5, "Psychology Layer", "Encouraging non-judgmental feedback, confidence scoring, and exam calm coaching."),
        KsmkeLayerSpec(6, "Methodology Layer", "10-step Socratic bilingual (English + Kannada) explanation and concept repair drills.")
    )

    val teachingPrinciples: List<String> = listOf(
        "Never invent fake historical exam years; label generated variants clearly as AI Practice.",
        "Acknowledge the student's correct step first before pointing out the first arithmetic or sign error.",
        "Teach concepts with an intuitive example before introducing abstract formulas.",
        "Always check final units (cm, cm², cm³, units) because missing units cost 0.5 board marks."
    )

    fun buildIndexedCatalog(): List<IndexedConceptNode> {
        return KsmkeSyllabusRepository.chapters.flatMap { ch ->
            ch.detailedTopics.map { topic ->
                val diff = when (ch.boardMarksWeightage) {
                    in 7..10 -> "High-Weightage Core"
                    in 5..6 -> "Standard Board Tier"
                    else -> "Quick-Scoring Tier"
                }
                IndexedConceptNode(
                    topicNumber = topic.topicNumber,
                    chapterNumber = ch.chapterNumber,
                    conceptTitle = topic.topicTitleEn,
                    pageRef = topic.sourcePageRef.ifBlank { ch.pageRange },
                    difficultyBand = diff,
                    recommendedTeachingHook = topic.coreConcepts.firstOrNull().orEmpty()
                )
            }
        }
    }
}
