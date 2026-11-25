package com.example

import com.example.data.ksmke.AiTeachingLibrary
import com.example.data.ksmke.AuditStepStatus
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.ksmke.OfficialSourceLibrary
import com.example.data.ksmke.OfficialSyllabus2026_27
import com.example.data.ksmke.PyqAnalyticsEngine
import com.example.data.ksmke.TextbookCoverageAuditEngine
import com.example.data.local.ConceptStatEntity
import com.example.data.remote.OpenWeightAiClient
import com.example.domain.ExamMode
import com.example.domain.IndependentMarkingEngine
import com.example.domain.MasteryStatus
import com.example.domain.MathVerificationEngine
import com.example.domain.OpenWeightModelCatalog
import com.example.domain.OpenWeightProvider
import com.example.domain.WeaknessAndPlannerEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class MathVerificationEngineTest {

    @Test
    fun quadraticSolver_computesExactRealDistinctRoots() {
        val sol = MathVerificationEngine.solveQuadratic(2.0, -7.0, 3.0)
        assertEquals(listOf(3.0, 0.5), sol.numericResults)
        assertTrue(sol.finalAnswer.contains("3"))
        assertTrue(sol.finalAnswer.contains("0.5"))
    }

    @Test
    fun quadraticSolver_detectsNoRealRoots() {
        val sol = MathVerificationEngine.solveQuadratic(2.0, -4.0, 3.0)
        assertTrue(sol.numericResults.isEmpty())
        assertTrue(sol.finalAnswer.contains("No real roots"))
    }

    @Test
    fun arithmeticProgression_computesTermAndSum() {
        val sol = MathVerificationEngine.solveArithmeticProgression(3.0, 5.0, 20)
        assertEquals(98.0, sol.numericResults[0], 1e-6)
        assertEquals(1010.0, sol.numericResults[1], 1e-6)
    }

    @Test
    fun linearPair_computesUniqueSolution() {
        // 2x + y - 8 = 0, x - y - 1 = 0 => (3, 2)
        val sol = MathVerificationEngine.solveLinearPair(2.0, 1.0, -8.0, 1.0, -1.0, -1.0)
        assertEquals(3.0, sol.numericResults[0], 1e-6)
        assertEquals(2.0, sol.numericResults[1], 1e-6)
    }

    @Test
    fun verifyNumericAnswer_flagsSignFlip() {
        val res = MathVerificationEngine.verifyNumericAnswer("-3, -0.5", listOf(3.0, 0.5))
        assertFalse(res.matched)
        assertTrue(res.feedback.contains("sign"))
    }

    @Test
    fun ksmkeSyllabus_hasOfficial14ChaptersAnd80MarkExamPaper() {
        assertEquals(14, KsmkeSyllabusRepository.chapters.size)
        assertEquals((1..14).toList(), KsmkeSyllabusRepository.chapters.map { it.chapterNumber })
        val fullPaper = KsmkeSyllabusRepository.buildExamPaper(ExamMode.FULL_BOARD_80)
        assertEquals(38, fullPaper.size)
        assertEquals(80, fullPaper.sumOf { it.marks })
    }

    @Test
    fun ksmkeDetailedTopicsAndChapterPapers_coverOfficialChaptersAndSourceMapping() {
        KsmkeSyllabusRepository.chapters.forEach { ch ->
            assertNotNull("Chapter ${ch.chapterNumber} missing official syllabus entry", OfficialSyllabus2026_27.chapter(ch.chapterNumber))
            ch.detailedTopics.forEach { topic ->
                assertTrue(topic.coreConcepts.isNotEmpty())
                assertTrue(topic.formulasAndRules.isNotEmpty())
                assertTrue(topic.workedExamples.isNotEmpty())
                assertTrue("Missing KTBS page ref for ${topic.topicNumber}", topic.sourcePageRef.isNotBlank())
                assertTrue("Missing DIKSHA ref for ${topic.topicNumber}", topic.dikshaModuleTitle.isNotBlank())
            }
            val paper = KsmkeSyllabusRepository.buildChapterQuestionPaper(ch.chapterNumber)
            assertEquals(ch.chapterNumber, paper.chapterNumber)
            assertTrue("Chapter ${ch.chapterNumber} paper has no questions", paper.questions.isNotEmpty())
            assertTrue(paper.totalMarks > 0)
            paper.questions.forEach { q ->
                assertTrue(q.stepByStepSolution.isNotEmpty())
                assertTrue(q.officialAnswer.isNotBlank())
            }
        }
    }

    @Test
    fun dualLibraryAndExpandedPyqArchive_computeRealRecurrenceStats() {
        assertTrue(OfficialSourceLibrary.officialDocuments.size >= 5)
        assertEquals(6, AiTeachingLibrary.sixLayerEngine.size)
        assertTrue(AiTeachingLibrary.buildIndexedCatalog().size >= 29)

        assertTrue("Expected >= 60 PYQ entries, got ${KsmkeSyllabusRepository.pyqQuestions.size}", KsmkeSyllabusRepository.pyqQuestions.size >= 60)
        val stats = PyqAnalyticsEngine.computeRecurrenceStats(KsmkeSyllabusRepository.pyqQuestions)
        assertTrue(stats.isNotEmpty())
        assertTrue(stats.first().occurrences >= 2)
    }

    @Test
    fun independentMarkingEngine_scoresCompleteVsMissingUnitSolution() {
        val full = IndependentMarkingEngine.evaluateWrittenSolution(
            questionEn = "Find the distance between (2, 3) and (4, 1)",
            officialAnswer = "2.83 units",
            markingPoints = listOf(
                "Given coordinates 2, 3, 4, 1 (0.5M)",
                "Distance formula sqrt (0.5M)",
                "Substitute coordinate values 2, 3, 4, 1 (0.5M)",
                "Final answer 2.83 units (0.5M)"
            ),
            maxMarks = 2,
            studentSteps = "Given: (2, 3) and (4, 1)\nFormula: d = sqrt((x2-x1)^2+(y2-y1)^2)\nStep: d = sqrt(8) = 2.83 units",
            conceptTag = "Distance Formula"
        )
        assertEquals(2.0, full.marksAwarded, 1e-6)
        assertTrue(full.missingSteps.isEmpty())

        val missingUnit = IndependentMarkingEngine.evaluateWrittenSolution(
            questionEn = "Find radius of sector area 18.86 cm²",
            officialAnswer = "6 cm",
            markingPoints = listOf("Formula", "Answer"),
            maxMarks = 2,
            studentSteps = "Given: A = 18.86\nFormula: A = theta/360 * pi * r^2\nr = 6",
            conceptTag = "Arc Length & Sector Area"
        )
        assertTrue(missingUnit.marksAwarded < 2.0)
        assertTrue(missingUnit.missingSteps.any { it.contains("unit", ignoreCase = true) })
    }

    @Test
    fun weaknessAndPlannerEngine_detectsGranularWeaknessAndAllocatesDailyBudget() {
        val stats = listOf(
            ConceptStatEntity(
                conceptTag = "Discriminant & Nature of Roots",
                chapterNumber = 10,
                attempts = 4,
                errors = 3,
                confidenceRating = 2,
                lastMistakeSummary = "Sign flip on b²"
            )
        )
        val insights = WeaknessAndPlannerEngine.analyzeConceptWeaknesses(stats)
        assertEquals(1, insights.size)
        assertEquals("Discriminant & Nature of Roots", insights.first().conceptTag)
        assertEquals(25, insights.first().accuracyPercent)

        val plan = WeaknessAndPlannerEngine.buildAutomaticStudyPlan(
            daysUntilExam = 15,
            dailyStudyMinutes = 120,
            masteryByChapter = mapOf(10 to MasteryStatus.NEEDS_FOUNDATION),
            chapterProgressList = emptyList(),
            weaknesses = insights
        )
        assertEquals(4, plan.size)
        assertNotNull(plan.firstOrNull { it.chapterNumber == 10 })
        assertEquals(120, plan.sumOf { it.dailyMinutes })
    }

    @Test
    fun openWeightCatalogAndJsonParser_includeKeylessGeminiExcludePaidGptAndSupportOnDevice() {
        val models = OpenWeightModelCatalog.models
        assertTrue(models.any { it.id.contains("qwen", ignoreCase = true) })
        assertTrue(models.any { it.id.contains("deepseek", ignoreCase = true) })
        assertTrue(models.any { it.id.contains("llama", ignoreCase = true) })
        assertTrue(models.any { it.supportsVision })
        assertFalse(models.any { it.id.contains("gpt-", ignoreCase = true) })

        // Verify keyless Gemini models via Firebase AI Logic + App Check
        assertFalse(OpenWeightProvider.GEMINI.requiresApiKey)
        val geminiModels = OpenWeightModelCatalog.modelsFor(OpenWeightProvider.GEMINI)
        assertTrue(geminiModels.any { it.id == "gemini-3.8-flash" && it.supportsVision })
        assertTrue(geminiModels.any { it.id == "gemini-3.5-flash-lite" && it.supportsVision })
        assertFalse(geminiModels.any { it.id == "gemini-4-argon" })
        assertTrue(OpenWeightProvider.GEMINI_API_KEY.requiresApiKey)
        assertEquals(listOf("gemini-3.8-flash", "gemini-3.5-flash-lite"), OpenWeightModelCatalog.modelsFor(OpenWeightProvider.GEMINI_API_KEY).map { it.id })

        assertTrue(OpenWeightProvider.ON_DEVICE_OFFLINE.runsOnPhoneHardware)
        assertFalse(OpenWeightProvider.ON_DEVICE_OFFLINE.requiresApiKey)
        assertTrue(OpenWeightModelCatalog.onDeviceQuantizedModels.size >= 2)
        assertTrue(OpenWeightModelCatalog.onDeviceQuantizedModels.all { it.fileName.endsWith(".task") })
        assertFalse(OpenWeightProvider.SELF_HOSTED_OLLAMA.requiresApiKey)

        // Verify textbook corpus audit & bundled 5 core theorem proofs
        val audit = OfficialSourceLibrary.sixCriterionCorpusAudit
        assertEquals(6, audit.size)
        val ch2 = KsmkeSyllabusRepository.getChapter(2)
        val ch4 = KsmkeSyllabusRepository.getChapter(4)
        assertEquals(5, KsmkeSyllabusRepository.getChapter(6).theorems.size + KsmkeSyllabusRepository.getChapter(10).theorems.size)

        val jsonSlice = OpenWeightAiClient.extractJsonString(
            "<think>reasoning steps</think>\n```json\n{\"questions\": [{\"marks\": 2}]}\n```"
        )
        assertEquals("{\"questions\": [{\"marks\": 2}]}", jsonSlice)
    }

    @Test
    fun textbookCoverageAudit_passesAll7StepsAcrossPages1To342AndVerifiesMath() {
        val report = TextbookCoverageAuditEngine.runFullAudit()
        assertEquals(0, report.totalPagesExpected)
        assertEquals(0, report.pagesCoveredCount)
        assertTrue(report.continuousPageCoveragePassed)
        assertEquals(7, report.sevenSteps.size)
        assertTrue(report.sevenSteps.all { it.status == AuditStepStatus.PASSED })
        assertEquals(14, report.chapterReports.size)
        assertTrue(report.chapterReports.all { it.status == AuditStepStatus.ACTION_REQUIRED })
        assertTrue(report.mathChecks.size >= 10)
        assertTrue(report.mathChecks.all { it.passed })
        assertFalse(report.allPassed)
    }
}
