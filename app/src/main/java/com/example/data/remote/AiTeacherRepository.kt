package com.example.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.domain.ChapterQuestionPaper
import com.example.domain.DifficultyLevel
import com.example.domain.MathVerificationEngine
import com.example.domain.PyqQuestion
import com.example.domain.QuestionFormat
import com.example.domain.QuestionSource
import com.example.domain.TargetedPaperQuestion
import com.example.domain.TeachingLanguage
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout
import org.json.JSONObject

private const val TAG = "AiTeacherRepo"

class AiTeacherRepository {

    private val tutorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            systemInstruction = content {
                text(
                    """
                    You are "Guruji", a patient, warm, encouraging Karnataka SSLC Class 10 Mathematics AI Teacher.
                    Your student is preparing for the KSEAB Karnataka SSLC Class 10 Board Exam (80 Marks, 38 Questions).
                    
                    CORE PERSONALITY RULES:
                    - Patient, disciplined, encouraging, and NEVER humiliating. Never call a student weak or slow.
                    - If a student makes a mistake, first acknowledge what part of their thinking is right, then gently fix the exact step.
                    - Never promise 100/100; give realistic exam readiness coaching.
                    - Never invent a fake past-paper year. Distinguish KSEAB syllabus concepts vs practice problems.
                    
                    TEACHING SEQUENCE (Follow for conceptual questions like "Why do we use the quadratic formula?"):
                    1. Explain the core concept simply.
                    2. Show a tiny relatable example.
                    3. Explain why simpler methods sometimes get stuck.
                    4. Introduce the formula/theorem clearly.
                    5. Show a clean step-by-step board-exam presentation (Given -> Formula -> Substitution -> Calculation -> Highlighted Answer).
                    6. End with ONE short practice question and invite the student to try it!
                    
                    Keep formatting clean, crisp, and mobile-readable (no noisy LaTeX dollars; use clean Unicode math like x², √, Δ, ∠, △, π, θ).
                    """.trimIndent()
                )
            }
        )
    }

    private val practiceJsonSchema = Schema.obj(
        properties = mapOf(
            "questions" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "questionEn" to Schema.string(description = "Clear Karnataka SSLC Class 10 board-format question in English"),
                        "questionKn" to Schema.string(description = "Kannada translation of the question"),
                        "marks" to Schema.integer(description = "Marks allocated: 1, 2, 3, or 4"),
                        "officialAnswer" to Schema.string(description = "Final exact answer with units"),
                        "markingPoints" to Schema.array(
                            items = Schema.string(),
                            description = "3 to 4 step-by-step board marking points"
                        ),
                        "commonMistakes" to Schema.string(description = "One common mistake students make on this question")
                    )
                ),
                description = "List of generated practice questions"
            )
        )
    )

    private val generatorModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = practiceJsonSchema
            }
        )
    }

    private val chapterPaperSchema = Schema.obj(
        properties = mapOf(
            "paperTitle" to Schema.string(description = "Title for the AI PYQ-targeted chapter question paper"),
            "pyqBlueprintSummary" to Schema.string(description = "Concise summary explaining past KSEAB board pattern and topic probability weights for this chapter"),
            "questions" to Schema.array(
                items = Schema.obj(
                    properties = mapOf(
                        "sectionLabel" to Schema.string(description = "Section I (1M MCQ), Section II (1M VSA), Section III (2M), Section IV (3M), or Section V (4M)"),
                        "marks" to Schema.integer(description = "Marks: 1, 2, 3, or 4"),
                        "probabilityBadge" to Schema.string(description = "Historical pattern likelihood label like '91% High-Yield Pattern' or '84% PYQ Variant'"),
                        "pyqTargetNote" to Schema.string(description = "Which recurring PYQ concept this newly invented practice question targets without inventing fake exam years"),
                        "questionEn" to Schema.string(description = "Question text in English"),
                        "questionKn" to Schema.string(description = "Kannada translation of the question"),
                        "mcqOptions" to Schema.array(
                            items = Schema.string(),
                            description = "4 option strings if MCQ, otherwise empty array []"
                        ),
                        "correctMcqIndex" to Schema.integer(description = "0-based correct option index if MCQ, otherwise -1"),
                        "officialAnswer" to Schema.string(description = "Exact final answer with units"),
                        "stepByStepSolution" to Schema.array(
                            items = Schema.string(),
                            description = "Complete step-by-step board marking solution lines"
                        ),
                        "commonMistake" to Schema.string(description = "Specific examiner warning / common student error")
                    )
                ),
                description = "6 to 8 graded chapterwise questions from 1M MCQs up to 3M/4M"
            )
        )
    )

    private val chapterPaperModel by lazy {
        Firebase.ai.generativeModel(
            modelName = "gemini-3.8-flash",
            generationConfig = generationConfig {
                responseMimeType = "application/json"
                responseSchema = chapterPaperSchema
            }
        )
    }

    fun streamTutorReply(
        studentMessage: String,
        chapterNumber: Int?,
        language: TeachingLanguage,
        historySummary: String
    ): Flow<String> = flow {
        val chapterContext = chapterNumber?.let {
            val ch = KsmkeSyllabusRepository.getChapter(it)
            "Active Chapter: Ch ${ch.chapterNumber} - ${ch.titleEn} (${ch.titleKn}). Key formulas: ${ch.keyFormulas.joinToString("; ")}."
        } ?: "General Karnataka SSLC Class 10 Mathematics."

        val langInstruction = when (language) {
            TeachingLanguage.EN -> "Respond in clear, simple English."
            TeachingLanguage.KN -> "Respond primarily in clear Kannada (ಕನ್ನಡ), keeping mathematical terms in brackets where helpful."
            TeachingLanguage.BILINGUAL -> "Respond in clear English followed by a concise Kannada (ಕನ್ನಡ) explanation summary so the student learns both."
        }

        val prompt = buildString {
            appendLine(chapterContext)
            appendLine(langInstruction)
            if (historySummary.isNotBlank()) {
                appendLine("Recent conversation context:\n$historySummary")
            }
            appendLine("Student says: $studentMessage")
        }

        val accumulated = StringBuilder()
        withTimeout(60_000L) {
            tutorModel.generateContentStream(prompt).collect { chunk ->
                val part = chunk.text.orEmpty()
                if (part.isNotEmpty()) {
                    accumulated.append(part)
                    emit(accumulated.toString())
                }
            }
        }
        if (accumulated.isBlank()) {
            throw IllegalStateException("AI Teacher returned an empty response.")
        }
    }

    suspend fun evaluateAnswerPresentation(
        questionText: String,
        expectedAnswer: String,
        maxMarks: Int,
        studentSteps: String,
        language: TeachingLanguage
    ): String {
        val langHint = if (language == TeachingLanguage.KN) "Write feedback in Kannada and English." else "Write concise English feedback."
        val prompt = """
            You are a Karnataka SSLC Board Examiner and kind Marks Maximisation Coach.
            Question ($maxMarks Marks): $questionText
            Official Key Answer: $expectedAnswer
            Student's Written Steps:
            $studentSteps
            
            Evaluate strictly on the 5 KSEAB Board Answer-Writing Pillars:
            1. Given data / equation stated
            2. Correct formula / theorem named
            3. Accurate substitution
            4. Clean step-by-step calculation & units
            5. Final answer highlighted
            
            Give:
            - Estimated Marks: X / $maxMarks
            - What was done well (encouraging!)
            - Any step or sign where marks could be lost
            - Ideal 5-line presentation model.
            $langHint
        """.trimIndent()

        return withTimeout(60_000L) {
            val response = tutorModel.generateContent(prompt)
            response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Empty evaluation response from AI.")
        }
    }

    suspend fun generatePracticeQuestions(
        chapterNumber: Int,
        marks: Int,
        count: Int = 3
    ): List<PyqQuestion> {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val prompt = """
            Generate $count fresh Karnataka SSLC Class 10 Mathematics practice questions for:
            Chapter ${ch.chapterNumber}: ${ch.titleEn} (${ch.titleKn}).
            Marks per question: $marks.
            Match KSEAB 2026-27 board standard. Do NOT claim any historical exam year.
        """.trimIndent()

        val response = withTimeout(60_000L) {
            generatorModel.generateContent(prompt)
        }
        val rawJson = response.text ?: throw IllegalStateException("Empty JSON from AI generator.")
        val root = JSONObject(rawJson)
        val array = root.getJSONArray("questions")
        val format = when (marks) {
            1 -> QuestionFormat.VSA_1M
            2 -> QuestionFormat.SA_2M
            3 -> QuestionFormat.LA_3M
            else -> QuestionFormat.LA_4M
        }
        return (0 until array.length()).map { idx ->
            val obj = array.getJSONObject(idx)
            val stepsArr = obj.getJSONArray("markingPoints")
            val steps = (0 until stepsArr.length()).map { stepsArr.getString(it) }
            PyqQuestion(
                id = "ai_${chapterNumber}_${System.currentTimeMillis()}_$idx",
                yearLabel = "AI Practice",
                examNumber = "AI-GENERATED",
                chapterNumber = chapterNumber,
                topic = ch.titleEn,
                marks = obj.optInt("marks", marks).coerceIn(1, 5),
                format = format,
                difficulty = DifficultyLevel.MEDIUM,
                source = QuestionSource.AI_PRACTICE,
                questionEn = obj.getString("questionEn"),
                questionKn = obj.optString("questionKn", ch.titleKn),
                officialAnswer = obj.getString("officialAnswer"),
                markingPoints = steps,
                requiredMethod = "KSEAB step-wise board method",
                commonMistakes = obj.optString("commonMistakes", "Check signs and final units.")
            )
        }
    }

    suspend fun generateDetailedTopicNotes(
        chapterNumber: Int,
        language: TeachingLanguage
    ): String {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val topicList = ch.detailedTopics.joinToString("\n") { "- Topic ${it.topicNumber}: ${it.topicTitleEn}" }
        val langNote = when (language) {
            TeachingLanguage.KN -> "Write headings in English & Kannada, explanations clearly in Kannada with math terms."
            TeachingLanguage.BILINGUAL -> "Write clear English notes with key Kannada terms and summary points."
            TeachingLanguage.EN -> "Write clear, thorough English study notes."
        }
        val prompt = """
            Prepare EXHAUSTIVE, topic-wise Karnataka SSLC Class 10 Board Exam detailed notes for:
            Chapter ${ch.chapterNumber}: ${ch.titleEn} (${ch.titleKn}) [${ch.textbookEdition}, ${ch.pageRange}].
            Topics to cover completely without skipping ANY concept, derivation, or step:
            $topicList
            
            For EVERY topic include:
            1. Complete definitions & textbook theory (zero shortcuts).
            2. Every formula, theorem statement, proof outline, and special case.
            3. A fresh step-by-step worked board example showing Given -> Formula -> Substitution -> Exact Calculation -> Highlighted Answer.
            4. Common SSLC board exam traps & examiner marking tips.
            $langNote
            Use crisp plain formatting and Unicode math symbols (x², √, Δ, ∠, △, π, θ). Do NOT use LaTeX dollar signs.
        """.trimIndent()

        return withTimeout(60_000L) {
            val response = tutorModel.generateContent(prompt)
            response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("AI returned empty detailed notes.")
        }
    }

    suspend fun generateChapterQuestionPaperAi(
        chapterNumber: Int,
        language: TeachingLanguage
    ): ChapterQuestionPaper {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val chapterPyqs = KsmkeSyllabusRepository.pyqQuestions
            .filter { it.chapterNumber == chapterNumber }
            .joinToString("\n") { "- [${it.yearLabel}, ${it.marks}M, ${it.topic}]: ${it.questionEn}" }

        val prompt = """
            You are a Karnataka SSLC Board Mathematics paper setter.
            Invent a BRAND-NEW, original 7-question chapterwise targeted question paper for:
            Chapter ${ch.chapterNumber}: ${ch.titleEn} (${ch.titleKn}).
            
            Reference historical PYQs for probability & topic weighting (DO NOT copy verbatim and DO NOT claim any fake historical exam year; mark every question as AI-targeted practice based on past frequency):
            $chapterPyqs
            
            Required Structure (Total ~16–20 Marks):
            - 2 questions in "Section I • Multiple Choice (1 Mark Each)" (marks=1, 4 distinct mcqOptions, correctMcqIndex 0..3)
            - 1 question in "Section II • Very Short Answer (1 Mark)" (marks=1, mcqOptions=[], correctMcqIndex=-1)
            - 2 questions in "Section III • Short Answer (2 Marks)" (marks=2, mcqOptions=[], correctMcqIndex=-1)
            - 1 question in "Section IV • Detailed Problem Solving (3 Marks)" (marks=3, mcqOptions=[], correctMcqIndex=-1)
            - 1 question in "Section V • Long Answer / Proof / Application (4 Marks)" (marks=4, mcqOptions=[], correctMcqIndex=-1)
            
            Ensure every numerical answer is 100% mathematically verified and every step in stepByStepSolution has clear KSEAB mark splits.
            Preferred explanation language: ${language.label}. Use Unicode math symbols, no LaTeX dollars.
        """.trimIndent()

        val response = withTimeout(60_000L) {
            chapterPaperModel.generateContent(prompt)
        }
        val rawJson = response.text ?: throw IllegalStateException("Empty JSON for AI chapter paper.")
        val root = JSONObject(rawJson)
        val qArr = root.getJSONArray("questions")
        val questions = (0 until qArr.length()).map { idx ->
            val obj = qArr.getJSONObject(idx)
            val optsArr = obj.optJSONArray("mcqOptions")
            val opts = if (optsArr != null) (0 until optsArr.length()).map { optsArr.getString(it) } else emptyList()
            val rawAnsIdx = obj.optInt("correctMcqIndex", -1)
            val ansIdx = if (opts.isNotEmpty() && rawAnsIdx in opts.indices) rawAnsIdx else null
            val stepsArr = obj.optJSONArray("stepByStepSolution")
            val steps = if (stepsArr != null) (0 until stepsArr.length()).map { stepsArr.getString(it) } else emptyList()
            TargetedPaperQuestion(
                id = "ai_cp_${chapterNumber}_${System.currentTimeMillis()}_$idx",
                questionNumber = idx + 1,
                sectionLabel = obj.optString("sectionLabel", "Practice Section"),
                marks = obj.optInt("marks", 2).coerceIn(1, 5),
                probabilityBadge = obj.optString("probabilityBadge", "High-Yield AI Target"),
                pyqTargetNote = obj.optString("pyqTargetNote", "Targets recurring KSEAB chapter blueprint"),
                isAiInvented = true,
                questionEn = obj.getString("questionEn"),
                questionKn = obj.optString("questionKn", ch.titleKn),
                mcqOptions = opts,
                correctMcqIndex = ansIdx,
                officialAnswer = obj.getString("officialAnswer"),
                stepByStepSolution = steps,
                commonMistake = obj.optString("commonMistake", "Double check formula substitution and units.")
            )
        }
        val totalMarks = questions.sumOf { it.marks }
        return ChapterQuestionPaper(
            chapterNumber = ch.chapterNumber,
            chapterTitleEn = ch.titleEn,
            chapterTitleKn = ch.titleKn,
            paperTitle = root.optString("paperTitle", "AI PYQ-Targeted Paper • Ch ${ch.chapterNumber}"),
            totalMarks = totalMarks,
            durationMinutes = (totalMarks * 2.2).toInt().coerceIn(25, 60),
            pyqBlueprintSummary = root.optString(
                "pyqBlueprintSummary",
                "AI-invented practice paper weighted by historical KSEAB frequency patterns."
            ),
            generatedByAi = true,
            questions = questions
        )
    }

    suspend fun checkQuestionAndAnswer(
        question: TargetedPaperQuestion,
        studentMcqIndex: Int?,
        studentWrittenSteps: String,
        studentUnableToSolve: Boolean,
        language: TeachingLanguage
    ): String {
        val mcqLabel = if (question.mcqOptions.isNotEmpty() && studentMcqIndex != null) {
            val chosen = question.mcqOptions.getOrNull(studentMcqIndex) ?: "None"
            val isCorrect = studentMcqIndex == question.correctMcqIndex
            "MCQ Selection: '$chosen' (${if (isCorrect) "CORRECT option" else "INCORRECT option — correct is '${question.mcqOptions.getOrNull(question.correctMcqIndex ?: -1) ?: question.officialAnswer}'"})"
        } else "No MCQ selected."

        val langInstruction = when (language) {
            TeachingLanguage.KN -> "Explain clearly in Kannada (ಕನ್ನಡ) with English math symbols."
            TeachingLanguage.BILINGUAL -> "Explain clearly in English and add a short Kannada summary."
            TeachingLanguage.EN -> "Explain clearly in encouraging English."
        }

        val prompt = if (studentUnableToSolve || (studentWrittenSteps.isBlank() && studentMcqIndex == null)) {
            """
            The student is UNABLE TO SOLVE this Karnataka SSLC Maths question and needs Guruji's patient help.
            Question (${question.marks} Marks): ${question.questionEn}
            Official Key Answer: ${question.officialAnswer}
            Expected Marking Steps: ${question.stepByStepSolution.joinToString(" | ")}
            
            Teach the student kindly from scratch:
            1. What the question is asking (in 1 simple sentence).
            2. Exact formula or theorem needed and WHY.
            3. Complete line-by-line solution (Given -> Formula -> Substitution -> Calculation -> Highlighted Answer).
            4. One quick tip to avoid the common mistake (${question.commonMistake}).
            $langInstruction
            """.trimIndent()
        } else {
            """
            Integrated Question & Answer Check for Karnataka SSLC Maths (${question.marks} Marks):
            Question: ${question.questionEn}
            Official Key Answer: ${question.officialAnswer}
            Official Marking Steps: ${question.stepByStepSolution.joinToString(" | ")}
            Student's MCQ Choice: $mcqLabel
            Student's Written Steps / Answer:
            ${studentWrittenSteps.ifBlank { "(Only MCQ option chosen; no written steps provided)" }}
            
            Perform a complete Q & A check:
            1. Verdict: Correct / Partially Correct / Needs Correction + Estimated Score out of ${question.marks}.
            2. Praise what the student got right first.
            3. Point out the exact step, sign, formula, or unit mistake (if any) and show how to fix it.
            4. Give the clean KSEAB full-mark solution so the student can verify every step.
            $langInstruction
            """.trimIndent()
        }

        return withTimeout(60_000L) {
            val response = tutorModel.generateContent(prompt)
            response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("AI returned empty Q&A check response.")
        }
    }

    suspend fun analyzeScannedSolution(
        bitmap: Bitmap,
        linkedQuestionText: String?,
        expectedAnswer: String?,
        studentUnableToSolve: Boolean,
        chapterNumber: Int?,
        language: TeachingLanguage
    ): String {
        val chapterHint = chapterNumber?.let {
            val ch = KsmkeSyllabusRepository.getChapter(it)
            "Active Chapter Context: Ch ${ch.chapterNumber} - ${ch.titleEn} (${ch.titleKn})."
        } ?: "Karnataka SSLC Class 10 Mathematics."

        val langInstruction = when (language) {
            TeachingLanguage.KN -> "Respond clearly in Kannada (ಕನ್ನಡ) with key math terms in English."
            TeachingLanguage.BILINGUAL -> "Respond clearly in English followed by a concise Kannada summary."
            TeachingLanguage.EN -> "Respond clearly in warm, encouraging English."
        }

        val promptText = buildString {
            appendLine(chapterHint)
            if (!linkedQuestionText.isNullOrBlank()) {
                appendLine("Linked Question: $linkedQuestionText")
            }
            if (!expectedAnswer.isNullOrBlank()) {
                appendLine("Expected Official Answer: $expectedAnswer")
            }
            if (studentUnableToSolve) {
                appendLine(
                    """
                    TASK: The student scanned this question because they are UNABLE TO SOLVE it (or stuck partway).
                    1. First transcribe clearly what mathematical question or partial work you read in the image.
                    2. Explain the core concept & formula simply.
                    3. Give the complete step-by-step KSEAB Board solution from start to highlighted final answer.
                    4. Mention the #1 common mistake students make here so they never lose marks.
                    """.trimIndent()
                )
            } else {
                appendLine(
                    """
                    TASK: Analyze the student's scanned handwritten solution carefully!
                    1. Transcribe the question & each step written by the student.
                    2. Check EVERY step mathematically: praise what is correct first.
                    3. If ANY step, sign, formula, or unit is wrong, pinpoint the exact line where it went wrong and explain WHY gently.
                    4. Show the corrected complete step-by-step board presentation and final verified answer.
                    5. If the image is unclear/blurry, tell the student politely what could not be read cleanly.
                    """.trimIndent()
                )
            }
            appendLine(langInstruction)
        }

        return withTimeout(60_000L) {
            val response = tutorModel.generateContent(
                content {
                    image(bitmap)
                    text(promptText)
                }
            )
            response.text?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("AI returned empty image analysis.")
        }
    }

    /**
     * Deterministic offline teaching fallback using KSMKE + MathVerificationEngine
     * when the student is studying offline without network connectivity.
     */
    fun buildOfflineKsmkeResponse(
        studentMessage: String,
        chapterNumber: Int?,
        language: TeachingLanguage
    ): String {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber ?: 10)
        val quadMatch = Regex("(-?\\d+)x\\^?2\\s*([+-]\\s*\\d+)x\\s*([+-]\\s*\\d+)")
            .find(studentMessage.replace(" ", ""))
        val mathCheck = if (quadMatch != null) {
            val a = quadMatch.groupValues[1].toDoubleOrNull() ?: 1.0
            val b = quadMatch.groupValues[2].toDoubleOrNull() ?: 0.0
            val c = quadMatch.groupValues[3].toDoubleOrNull() ?: 0.0
            val sol = MathVerificationEngine.solveQuadratic(a, b, c)
            "\n\n[Mathematics Engine Verification]\n${sol.steps.joinToString("\n")}\nFinal Answer: ${sol.finalAnswer}"
        } else ""

        val header = if (language == TeachingLanguage.KN) {
            "ಆಫ್‌ಲೈನ್ KSMKE ಮಾರ್ಗದರ್ಶಿ • ಅಧ್ಯಾಯ ${ch.chapterNumber}: ${ch.titleKn}\n${ch.summaryKn}"
        } else {
            "Offline KSMKE Lesson • Chapter ${ch.chapterNumber}: ${ch.titleEn} (${ch.titleKn})\n${ch.summaryEn}"
        }

        val example = ch.solvedExamples.firstOrNull()
        val stepsText = example?.let {
            "\n\nSolved Board Example (${it.title}):\n${it.problem}\n" +
                it.markingSteps.joinToString("\n") +
                "\nFinal Answer: ${it.finalAnswer}\nExaminer Tip: ${it.examinerTip}"
        }.orEmpty()

        return "$header\n\nKey Formulas:\n• ${ch.keyFormulas.joinToString("\n• ")}$stepsText$mathCheck\n\nTry answering: ${ch.starterQuestion}"
    }

    fun formatAiError(error: Throwable): String {
        if (error is CancellationException && error !is TimeoutCancellationException) {
            throw error
        }
        Log.e(TAG, "AI Teacher error", error)
        return when (error) {
            is TimeoutCancellationException ->
                "AI Teacher timed out after 60 seconds. Check your internet or tap 'Use Offline KSMKE Guide'."
            else ->
                error.localizedMessage ?: "Unable to reach Firebase AI Logic right now."
        }
    }
}
