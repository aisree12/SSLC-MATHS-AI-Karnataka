package com.example

import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.ExamMode
import com.example.domain.MathVerificationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
    fun ksmkeDetailedTopicsAndChapterPapers_coverAll15Chapters() {
        KsmkeSyllabusRepository.chapters.forEach { ch ->
            assertTrue("Chapter ${ch.chapterNumber} missing detailedTopics", ch.detailedTopics.isNotEmpty())
            ch.detailedTopics.forEach { topic ->
                assertTrue(topic.coreConcepts.isNotEmpty())
                assertTrue(topic.formulasAndRules.isNotEmpty())
                assertTrue(topic.workedExamples.isNotEmpty())
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
}
