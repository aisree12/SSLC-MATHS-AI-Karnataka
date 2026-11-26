package com.example.data.ksmke

import com.example.domain.MathAuditCheckResult
import com.example.domain.MathVerificationEngine

enum class AuditStepStatus(val label: String) {
    PASSED("PASSED"),
    ACTION_REQUIRED("NEEDS SOURCE REVIEW")
}

data class AuditStepSummary(
    val stepNumber: Int,
    val title: String,
    val status: AuditStepStatus,
    val detail: String
)

data class ChapterMissingContentReport(
    val chapterNumber: Int,
    val chapterTitleEn: String,
    val chapterTitleKn: String,
    val startPage: Int,
    val endPage: Int,
    val definitionsCount: Int,
    val formulasCount: Int,
    val theoremsProofsCount: Int,
    val workedExamplesCount: Int,
    val diagramsCount: Int,
    val exerciseSetsCount: Int,
    val subquestionsCoveredCount: Int,
    val englishKannadaSynced: Boolean,
    val mathChecksPassedCount: Int,
    val priorGapsIdentified: List<String>,
    val remediationSummary: String,
    val status: AuditStepStatus
)

data class FullTextbookAuditReport(
    val totalPagesExpected: Int,
    val pagesCoveredCount: Int,
    val continuousPageCoveragePassed: Boolean,
    val sevenSteps: List<AuditStepSummary>,
    val chapterReports: List<ChapterMissingContentReport>,
    val mathChecks: List<MathAuditCheckResult>,
    val allPassed: Boolean
)

/**
 * Evidence-aware coverage report.
 *
 * The English and Kannada books have separate pagination, and pagination restarts
 * in Part 2. The current app contains curated indexes, not a verified page-level
 * transcription of every source. Therefore this engine reports counts and gaps,
 * never certifies textbook coverage from its own metadata.
 */
object TextbookCoverageAuditEngine {
    const val TOTAL_TEXTBOOK_PAGES = 0

    fun runFullAudit(): FullTextbookAuditReport {
        val chapters = KsmkeSyllabusRepository.chapters
        val mathChecks = MathVerificationEngine.verifyAllBundledTextbookExamples()

        val chapterReports = chapters.map { chapter ->
            val official = OfficialSyllabus2026_27.chapter(chapter.chapterNumber)
            val supplement = KsmkeSupplementLibrary.byOfficialChapter[chapter.chapterNumber]
            val pageMatch = Regex("(\\d+)\\s*[–-]\\s*(\\d+)")
                .find(official?.englishPrintedPages.orEmpty())
            val startPage = pageMatch?.groupValues?.get(1)?.toIntOrNull() ?: 0
            val endPage = pageMatch?.groupValues?.get(2)?.toIntOrNull() ?: 0
            val chapterChecks = mathChecks.filter { it.chapterNumber == chapter.chapterNumber }

            ChapterMissingContentReport(
                chapterNumber = chapter.chapterNumber,
                chapterTitleEn = chapter.titleEn,
                chapterTitleKn = chapter.titleKn,
                startPage = startPage,
                endPage = endPage,
                definitionsCount = supplement?.definitions?.size ?: 0,
                formulasCount = (chapter.keyFormulas + chapter.detailedTopics.flatMap { it.formulasAndRules }).distinct().size,
                theoremsProofsCount = chapter.theorems.size,
                workedExamplesCount = chapter.solvedExamples.size + chapter.detailedTopics.sumOf { it.workedExamples.size },
                diagramsCount = supplement?.diagrams?.size ?: 0,
                exerciseSetsCount = supplement?.exercises?.size ?: 0,
                subquestionsCoveredCount = supplement?.exercises?.sumOf { it.subquestionCount } ?: 0,
                englishKannadaSynced = false,
                mathChecksPassedCount = chapterChecks.count { it.passed },
                priorGapsIdentified = supplement?.priorAuditGapsFound.orEmpty(),
                remediationSummary = "Indexed material only. Compare every section, example, exercise, diagram and Kannada translation with the four source books.",
                status = AuditStepStatus.ACTION_REQUIRED
            )
        }

        val definitions = chapterReports.sumOf { it.definitionsCount }
        val formulas = chapterReports.sumOf { it.formulasCount }
        val proofs = chapterReports.sumOf { it.theoremsProofsCount }
        val examples = chapterReports.sumOf { it.workedExamplesCount }
        val diagrams = chapterReports.sumOf { it.diagramsCount }
        val exerciseSets = chapterReports.sumOf { it.exerciseSetsCount }
        val subquestions = chapterReports.sumOf { it.subquestionsCoveredCount }
        val mathPassed = mathChecks.isNotEmpty() && mathChecks.all { it.passed }

        val steps = listOf(
            AuditStepSummary(
                1, "Official 2026–27 English and Kannada editions", AuditStepStatus.ACTION_REQUIRED,
                "The 14 chapter titles and printed page ranges are indexed from both language contents pages. Detailed Kannada terminology and translation parity are not yet verified."
            ),
            AuditStepSummary(
                2, "Printed page-by-page source coverage", AuditStepStatus.ACTION_REQUIRED,
                "Not audited page by page. Part 2 restarts pagination and the Kannada and English books use different printed page ranges; a single continuous page count would be misleading."
            ),
            AuditStepSummary(
                3, "Definitions, formulas, theorems, examples and diagrams", AuditStepStatus.ACTION_REQUIRED,
                "Current app index contains $definitions definitions, $formulas formulas, $proofs theorem/proof records, $examples worked examples and $diagrams diagram records. These counts do not establish source completeness."
            ),
            AuditStepSummary(
                4, "Textbook exercises and subquestions", AuditStepStatus.ACTION_REQUIRED,
                "Current app index contains $exerciseSets exercise records and $subquestions subquestion references. Exact question wording, completeness and answers require source comparison."
            ),
            AuditStepSummary(
                5, "Cross-match with teaching notes, PYQs and chapter papers", AuditStepStatus.ACTION_REQUIRED,
                "Chapter IDs and navigation now use the official 14-chapter sequence; content-level alignment and every legacy PYQ/source reference still require review."
            ),
            AuditStepSummary(
                6, "Chapter-by-chapter missing-content and remediation", AuditStepStatus.ACTION_REQUIRED,
                "All 14 chapters remain open for source review. Existing supplement notes and prior remediation descriptions are not accepted as proof that gaps are closed."
            ),
            AuditStepSummary(
                7, "Independent mathematical verification",
                if (mathPassed) AuditStepStatus.PASSED else AuditStepStatus.ACTION_REQUIRED,
                "${mathChecks.count { it.passed }}/${mathChecks.size} bundled deterministic checks passed. This is a limited regression set, not verification of every textbook solution."
            )
        )

        return FullTextbookAuditReport(
            totalPagesExpected = TOTAL_TEXTBOOK_PAGES,
            pagesCoveredCount = 0,
            continuousPageCoveragePassed = false,
            sevenSteps = steps,
            chapterReports = chapterReports,
            mathChecks = mathChecks,
            allPassed = false
        )
    }
}
