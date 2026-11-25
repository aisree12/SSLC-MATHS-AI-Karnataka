package com.example.data.remote

import android.graphics.Bitmap
import android.util.Log
import com.example.data.ksmke.KsmkeSyllabusRepository
import com.example.data.local.OnDeviceLlmEngine
import com.example.data.local.OnDeviceModelManager
import com.example.domain.ChapterQuestionPaper
import com.example.domain.DifficultyLevel
import com.example.domain.MathVerificationEngine
import com.example.domain.OpenWeightModelCatalog
import com.example.domain.OpenWeightProvider
import com.example.domain.PyqQuestion
import com.example.domain.QuestionFormat
import com.example.domain.QuestionSource
import com.example.domain.TargetedPaperQuestion
import com.example.domain.TeachingLanguage
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withTimeout

private const val TAG = "AiTeacherRepo"

/**
 * SSLC Mathematics AI Teacher Repository powered by:
 * 1. Real Android On-Device Offline AI (MediaPipe LlmInference running downloaded quantised Qwen2.5 .task weights),
 * 2. Free open-weight OpenAI-compatible cloud providers (OpenRouter :free, Groq, Hugging Face, Cerebras), and
 * 3. Deterministic Rule-Based KSMKE + MathVerificationEngine study guide.
 */
class AiTeacherRepository(
    private val openWeightClient: OpenWeightAiClient = OpenWeightAiClient(),
    private val onDeviceModelManager: OnDeviceModelManager? = null,
    private val onDeviceLlmEngine: OnDeviceLlmEngine? = null
) {

    @Volatile
    fun isProviderApiKeyConfigured(provider: OpenWeightProvider): Boolean =
        openWeightClient.isProviderApiKeyConfigured(provider)

    var activeProvider: OpenWeightProvider = OpenWeightProvider.GEMINI_API_KEY

    @Volatile
    var activeModelId: String = OpenWeightModelCatalog.defaultTextModel(OpenWeightProvider.GEMINI_API_KEY).id

    fun isApiKeyConfigured(): Boolean = openWeightClient.isApiKeyConfigured()

    private fun requireReadyOnDeviceModelFile(modelId: String = activeModelId): java.io.File {
        val manager = onDeviceModelManager
            ?: throw IllegalStateException("On-device model manager is not initialized.")
        val spec = OpenWeightModelCatalog.findOnDeviceSpec(modelId)
        return manager.getReadyModelFile(spec.modelId)
            ?: throw IllegalStateException(
                "${spec.displayName} (${spec.approxSizeLabel}) is not downloaded on this phone yet. " +
                    "Tap 'Download Quantised Model' below the AI Model menu (or import a .task file) for true offline AI."
            )
    }

    private suspend fun executeTextPrompt(
        systemInstruction: String,
        userPrompt: String
    ): String {
        if (activeProvider == OpenWeightProvider.ON_DEVICE_OFFLINE) {
            val engine = onDeviceLlmEngine
                ?: throw IllegalStateException("On-device MediaPipe LLM runtime is not available.")
            val modelFile = requireReadyOnDeviceModelFile()
            return withTimeout(120_000L) {
                engine.complete(
                    modelFile = modelFile,
                    systemInstruction = systemInstruction,
                    userPrompt = userPrompt
                )
            }
        }
        return try {
            withTimeout(60_000L) {
                openWeightClient.completeChat(
                    provider = activeProvider,
                    modelId = activeModelId,
                    systemInstruction = systemInstruction,
                    userPrompt = userPrompt
                )
            }
        } catch (e: TimeoutCancellationException) {
            completeWithDownloadedLocalModel(systemInstruction, userPrompt, e)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            completeWithDownloadedLocalModel(systemInstruction, userPrompt, e)
        }
    }

    private suspend fun completeWithDownloadedLocalModel(
        systemInstruction: String,
        userPrompt: String,
        cloudError: Throwable
    ): String {
        val manager = onDeviceModelManager
            ?: throw IllegalStateException(
                "Cloud AI failed and no on-device model manager is available. Install a .task model or use the offline KSMKE lesson.",
                cloudError
            )
        val engine = onDeviceLlmEngine
            ?: throw IllegalStateException(
                "Cloud AI failed and the on-device inference runtime is unavailable.",
                cloudError
            )
        val readySpec = OpenWeightModelCatalog.onDeviceQuantizedModels.firstOrNull {
            manager.getReadyModelFile(it.modelId) != null
        } ?: throw IllegalStateException(
            "Cloud AI failed (possibly free-tier quota). Download or import a quantised .task model to enable automatic offline AI.",
            cloudError
        )
        val modelFile = manager.getReadyModelFile(readySpec.modelId)
            ?: throw IllegalStateException("Downloaded on-device model is no longer available.", cloudError)
        return try {
            withTimeout(120_000L) {
                engine.complete(
                    modelFile = modelFile,
                    systemInstruction = systemInstruction,
                    userPrompt = userPrompt
                )
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            throw IllegalStateException(
                "Cloud AI and downloaded on-device AI both failed. The offline KSMKE guide remains available in Teacher Chat.",
                e
            )
        }
    }

    private val gurujiSystemPrompt = """
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

    fun streamTutorReply(
        studentMessage: String,
        chapterNumber: Int?,
        language: TeachingLanguage,
        historySummary: String,
        forceLocalModelId: String? = null
    ): Flow<String> {
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

        if (forceLocalModelId != null || activeProvider == OpenWeightProvider.ON_DEVICE_OFFLINE) {
            val engine = onDeviceLlmEngine
                ?: throw IllegalStateException("On-device MediaPipe LLM runtime is not initialized.")
            val modelFile = requireReadyOnDeviceModelFile(forceLocalModelId ?: activeModelId)
            return engine.streamCompletion(
                modelFile = modelFile,
                systemInstruction = gurujiSystemPrompt,
                userPrompt = prompt
            )
        }

        return openWeightClient.streamChatCompletion(
            provider = activeProvider,
            modelId = activeModelId,
            systemInstruction = gurujiSystemPrompt,
            userPrompt = prompt
        )
    }

    /** Cloud-first teacher stream with automatic local-AI / structured-guide recovery. */
    fun streamTutorReplyWithFallback(
        studentMessage: String,
        chapterNumber: Int?,
        language: TeachingLanguage,
        historySummary: String
    ): Flow<String> = flow {
        try {
            streamTutorReply(studentMessage, chapterNumber, language, historySummary).collect { emit(it) }
        } catch (e: TimeoutCancellationException) {
            recoverTeacherReply(studentMessage, chapterNumber, language, historySummary, e, this)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            recoverTeacherReply(studentMessage, chapterNumber, language, historySummary, e, this)
        }
    }

    private suspend fun recoverTeacherReply(
        studentMessage: String,
        chapterNumber: Int?,
        language: TeachingLanguage,
        historySummary: String,
        cloudError: Throwable,
        collector: FlowCollector<String>
    ) {
        val manager = onDeviceModelManager
        val engine = onDeviceLlmEngine
        val readySpec = OpenWeightModelCatalog.onDeviceQuantizedModels.firstOrNull { spec ->
            manager?.getReadyModelFile(spec.modelId) != null
        }
        if (readySpec != null && engine != null) {
            val notice = "Cloud AI unavailable (network/provider/free-tier quota). Switching to downloaded on-device AI.\n\n"
            collector.emit(notice)
            try {
                streamTutorReply(studentMessage, chapterNumber, language, historySummary, readySpec.modelId)
                    .collect { collector.emit(notice + it) }
                return
            } catch (e: CancellationException) {
                throw e
            } catch (localError: Exception) {
                Log.w(TAG, "On-device fallback failed: " + localError.message, localError)
            }
        }
        Log.w(TAG, "Using structured offline lesson after cloud failure: " + cloudError.message)
        collector.emit(buildOfflineKsmkeResponse(studentMessage, chapterNumber, language))
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

        return executeTextPrompt(
            systemInstruction = gurujiSystemPrompt,
            userPrompt = prompt
        )
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
            
            Return ONLY valid JSON matching this exact structure (no markdown prose):
            {
              "questions": [
                {
                  "questionEn": "...",
                  "questionKn": "...",
                  "marks": $marks,
                  "officialAnswer": "...",
                  "markingPoints": ["Step 1 ...", "Step 2 ...", "Step 3 ..."],
                  "commonMistakes": "..."
                }
              ]
            }
        """.trimIndent()

        val rawText = executeTextPrompt(
            systemInstruction = "You are a Karnataka SSLC Maths paper setter. Output strictly valid JSON only.",
            userPrompt = prompt
        )
        val root = OpenWeightAiClient.extractJsonObject(rawText)
        val array = root.getJSONArray("questions")
        val format = when (marks) {
            1 -> QuestionFormat.VSA_1M
            2 -> QuestionFormat.SA_2M
            3 -> QuestionFormat.LA_3M
            else -> QuestionFormat.LA_4M
        }
        return (0 until array.length()).map { idx ->
            val obj = array.getJSONObject(idx)
            val stepsArr = obj.optJSONArray("markingPoints")
            val steps = if (stepsArr != null) {
                (0 until stepsArr.length()).map { stepsArr.getString(it) }
            } else {
                listOf("Given data & formula (0.5M)", "Step-by-step substitution & answer (${marks - 0.5}M)")
            }
            PyqQuestion(
                id = "ai_${chapterNumber}_${System.currentTimeMillis()}_$idx",
                yearLabel = "Open-Weight AI Practice",
                examNumber = "OPEN-AI",
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

        return executeTextPrompt(
            systemInstruction = gurujiSystemPrompt,
            userPrompt = prompt
        )
    }

    suspend fun generateChapterQuestionPaperAi(
        chapterNumber: Int,
        language: TeachingLanguage
    ): ChapterQuestionPaper {
        val ch = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val chapterPyqs = KsmkeSyllabusRepository.pyqQuestions
            .filter { it.chapterNumber == chapterNumber }
            .take(8)
            .joinToString("\n") { "- [${it.yearLabel}, ${it.marks}M, ${it.topic}]: ${it.questionEn}" }

        val prompt = """
            You are a Karnataka SSLC Board Mathematics paper setter.
            Invent a BRAND-NEW, original 7-question chapterwise targeted question paper for:
            Chapter ${ch.chapterNumber}: ${ch.titleEn} (${ch.titleKn}).
            
            Reference historical PYQs for probability & topic weighting (DO NOT copy verbatim and DO NOT claim any fake historical exam year):
            $chapterPyqs
            
            Required Structure (Total ~16-20 Marks):
            - 2 questions in "Section I • Multiple Choice (1 Mark Each)" (marks=1, 4 distinct mcqOptions, correctMcqIndex 0..3)
            - 1 question in "Section II • Very Short Answer (1 Mark)" (marks=1, mcqOptions=[], correctMcqIndex=-1)
            - 2 questions in "Section III • Short Answer (2 Marks)" (marks=2, mcqOptions=[], correctMcqIndex=-1)
            - 1 question in "Section IV • Detailed Problem Solving (3 Marks)" (marks=3, mcqOptions=[], correctMcqIndex=-1)
            - 1 question in "Section V • Long Answer / Proof / Application (4 Marks)" (marks=4, mcqOptions=[], correctMcqIndex=-1)
            
            Preferred explanation language: ${language.label}. Use Unicode math symbols, no LaTeX dollars.
            Return ONLY valid JSON matching:
            {
              "paperTitle": "...",
              "pyqBlueprintSummary": "...",
              "questions": [
                {
                  "sectionLabel": "...",
                  "marks": 1,
                  "probabilityBadge": "88% High-Yield Pattern",
                  "pyqTargetNote": "...",
                  "questionEn": "...",
                  "questionKn": "...",
                  "mcqOptions": ["A", "B", "C", "D"],
                  "correctMcqIndex": 0,
                  "officialAnswer": "...",
                  "stepByStepSolution": ["Step 1 ...", "Step 2 ..."],
                  "commonMistake": "..."
                }
              ]
            }
        """.trimIndent()

        val rawText = executeTextPrompt(
            systemInstruction = "You are a Karnataka SSLC Maths paper setter. Output strictly valid JSON only.",
            userPrompt = prompt
        )
        val root = OpenWeightAiClient.extractJsonObject(rawText)
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
                "Open-weight AI practice paper weighted by historical KSEAB frequency patterns."
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

        return executeTextPrompt(
            systemInstruction = gurujiSystemPrompt,
            userPrompt = prompt
        )
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
                    TASK: Act as a precise Handwriting Mathematics Reader for the student's scanned notebook solution.
                    Follow this exact structured output:
                    1. Transcribed Steps: List each handwritten line as "Step 1: ...", "Step 2: ...", etc.
                    2. First Incorrect Step: Explicitly write either "First Incorrect Step: Step X — <exact issue>" or "First Incorrect Step: None (All steps are mathematically valid)".
                    3. Gentle Correction: First praise what is right in earlier steps, then clearly explain how to fix Step X (sign, formula, substitution, or missing unit).
                    4. Corrected KSEAB Presentation: Show the clean line-by-line board solution and verified final answer.
                    5. Underlying Concept: Name the exact single concept (e.g., "Discriminant Δ = b² - 4ac" or "Section Formula").
                    6. If any part of the photo is blurry or cut off, politely specify which line could not be read clearly.
                    """.trimIndent()
                )
            }
            appendLine(langInstruction)
        }

        if (activeProvider == OpenWeightProvider.ON_DEVICE_OFFLINE) {
            throw IllegalStateException(
                "Downloaded on-device Qwen2.5 INT8 models are text-only. To analyze handwritten camera photos, select a Vision model (such as Qwen 2.5 VL Vision) from the top-right AI Model menu."
            )
        }

        return withTimeout(60_000L) {
            openWeightClient.analyzeImageWithVisionModel(
                provider = activeProvider,
                bitmap = bitmap,
                systemInstruction = gurujiSystemPrompt,
                userPrompt = promptText
            )
        }
    }

    /**
     * Deterministic offline teaching fallback using KSMKE + MathVerificationEngine
     * when studying offline or without an API key.
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

    fun releaseOnDeviceEngine() {
        onDeviceLlmEngine?.release()
    }

    fun formatAiError(error: Throwable): String {
        if (error is CancellationException && error !is TimeoutCancellationException) {
            throw error
        }
        Log.e(TAG, "Open-weight AI error", error)
        return when (error) {
            is TimeoutCancellationException ->
                "Open-weight AI model timed out after 60 seconds. Try switching provider/model or tap 'Use Offline KSMKE Guide'."
            else ->
                error.localizedMessage ?: "Unable to reach open-weight OpenAI-compatible provider right now."
        }
    }
}
