package com.example

import com.example.data.ksmke.AiTeachingLibrary
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.ksmke.OfficialSourceLibrary
import com.example.data.ksmke.PyqAnalyticsEngine
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
    fun ksmkeSyllabus_hasAll15ChaptersAnd80MarkExamPaper() {
        assertEquals(15, KsmkeSyllabusRepository.chapters.size)
        val fullPaper = KsmkeSyllabusRepository.buildExamPaper(ExamMode.FULL_BOARD_80)
        assertEquals(38, fullPaper.size)
        assertEquals(80, fullPaper.sumOf { it.marks })
    }

    @Test
    fun ksmkeDetailedTopicsAndChapterPapers_coverAll15ChaptersAndSourceMapping() {
        KsmkeSyllabusRepository.chapters.forEach { ch ->
            assertTrue("Chapter ${ch.chapterNumber} missing detailedTopics", ch.detailedTopics.isNotEmpty())
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
            markingPoints = listOf("Formula", "Substitute", "Answer"),
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
    fun openWeightCatalogAndJsonParser_excludePaidEnginesAndSupportQwen() {
        val models = OpenWeightModelCatalog.models
        assertTrue(models.any { it.id.contains("qwen", ignoreCase = true) })
        assertTrue(models.any { it.id.contains("deepseek", ignoreCase = true) })
        assertTrue(models.any { it.id.contains("llama", ignoreCase = true) })
        assertTrue(models.any { it.supportsVision })
        assertFalse(models.any { it.id.contains("gemini", ignoreCase = true) || it.id.contains("gpt-", ignoreCase = true) })
        assertFalse(OpenWeightProvider.LOCAL_OLLAMA.requiresApiKey)

        val jsonSlice = OpenWeightAiClient.extractJsonString(
            "<think>reasoning steps</think>\n```json\n{\"questions\": [{\"marks\": 2}]}\n```"
        )
        assertEquals("{\"questions\": [{\"marks\": 2}]}", jsonSlice)
    }
}
