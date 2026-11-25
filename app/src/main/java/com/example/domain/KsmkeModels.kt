package com.example.domain

enum class TeachingLanguage(val label: String, val kannadaLabel: String) {
    EN("English", "ಇಂಗ್ಲಿಷ್"),
    KN("Kannada", "ಕನ್ನಡ"),
    BILINGUAL("Bilingual (EN + KN)", "ದ್ವಿಭಾಷಾ")
}

enum class MasteryStatus(val label: String, val weight: Int) {
    NOT_STARTED("Not Started", 0),
    NEEDS_FOUNDATION("Needs Foundation", 1),
    PARTIALLY_UNDERSTOOD("Partially Understood", 2),
    UNDERSTOOD("Understood", 3)
}

enum class QuestionSource(val badge: String) {
    HISTORICAL_BOARD("Board PYQ"),
    OFFICIAL_MODEL_PAPER("2026-27 MQP"),
    TEXTBOOK_EXERCISE("KSEAB Textbook"),
    AI_PRACTICE("AI Practice")
}

enum class QuestionFormat(val label: String, val recommendedMinutes: Int) {
    MCQ("1M MCQ", 2),
    VSA_1M("1M Short", 2),
    SA_2M("2M Step", 4),
    LA_3M("3M Detailed", 6),
    LA_4M("4M Proof/Graph", 9),
    LA_5M("5M Long", 12)
}

enum class DifficultyLevel(val label: String) {
    EASY("Easy"),
    MEDIUM("Standard"),
    CHALLENGING("HOTS")
}

data class SolvedExample(
    val title: String,
    val problem: String,
    val markingSteps: List<String>,
    val finalAnswer: String,
    val examinerTip: String
)

data class TheoremCard(
    val name: String,
    val kannadaName: String,
    val statement: String,
    val keyProofSteps: List<String>,
    val marksWeight: Int
)

enum class TopicCompletionStatus(val label: String) {
    NOT_VERIFIED("Pending Check"),
    IN_REVIEW("Practising"),
    VERIFIED_COMPLETE("Verified & Mastered")
}

data class TopicDetailedNote(
    val topicNumber: String,
    val topicTitleEn: String,
    val topicTitleKn: String,
    val coreConcepts: List<String>,
    val formulasAndRules: List<String>,
    val derivationsOrMethods: List<String>,
    val workedExamples: List<SolvedExample>,
    val examMistakesToAvoid: List<String>,
    val sourcePageRef: String = "",
    val exerciseRef: String = "",
    val dikshaModuleTitle: String = ""
)

data class KsmkeChapter(
    val chapterNumber: Int,
    val titleEn: String,
    val titleKn: String,
    val unitArea: String,
    val textbookEdition: String,
    val pageRange: String,
    val boardMarksWeightage: Int,
    val summaryEn: String,
    val summaryKn: String,
    val keyFormulas: List<String>,
    val theorems: List<TheoremCard>,
    val solvedExamples: List<SolvedExample>,
    val commonMistakes: List<String>,
    val starterQuestion: String,
    val detailedTopics: List<TopicDetailedNote> = emptyList()
)

data class PyqQuestion(
    val id: String,
    val yearLabel: String,
    val examNumber: String,
    val chapterNumber: Int,
    val topic: String,
    val marks: Int,
    val format: QuestionFormat,
    val difficulty: DifficultyLevel,
    val source: QuestionSource,
    val questionEn: String,
    val questionKn: String,
    val mcqOptions: List<String> = emptyList(),
    val correctMcqIndex: Int? = null,
    val officialAnswer: String,
    val markingPoints: List<String>,
    val requiredMethod: String,
    val commonMistakes: String,
    val conceptTag: String = ""
)

data class TargetedPaperQuestion(
    val id: String,
    val questionNumber: Int,
    val sectionLabel: String,
    val marks: Int,
    val probabilityBadge: String,
    val pyqTargetNote: String,
    val isAiInvented: Boolean,
    val questionEn: String,
    val questionKn: String,
    val mcqOptions: List<String> = emptyList(),
    val correctMcqIndex: Int? = null,
    val officialAnswer: String,
    val stepByStepSolution: List<String>,
    val commonMistake: String
)

data class ChapterQuestionPaper(
    val chapterNumber: Int,
    val chapterTitleEn: String,
    val chapterTitleKn: String,
    val paperTitle: String,
    val totalMarks: Int,
    val durationMinutes: Int,
    val pyqBlueprintSummary: String,
    val generatedByAi: Boolean,
    val questions: List<TargetedPaperQuestion>
)

enum class ExamMode(
    val title: String,
    val subtitle: String,
    val maxMarks: Int,
    val questionCount: Int,
    val totalMinutes: Int
) {
    FULL_BOARD_80(
        title = "SSLC Full Board Mock",
        subtitle = "80 Marks • 38 Questions • 195 Min (15m Read + 150m Solve + 30m Review)",
        maxMarks = 80,
        questionCount = 38,
        totalMinutes = 195
    ),
    CHAPTER_TEST_20(
        title = "Chapter Mastery Test",
        subtitle = "20 Marks • 8 Questions • 45 Min Board Standard",
        maxMarks = 20,
        questionCount = 8,
        totalMinutes = 45
    ),
    SPEED_DRILL_10(
        title = "Calculation Accuracy Drill",
        subtitle = "10 Marks • 5 Questions • 15 Min Rapid Sprint",
        maxMarks = 10,
        questionCount = 5,
        totalMinutes = 15
    )
}

data class ChatTurn(
    val id: String,
    val isStudent: Boolean,
    val text: String,
    val mathVerificationNote: String? = null,
    val isStreaming: Boolean = false
)

data class AnswerStepCheck(
    val stepLabel: String,
    val marksAvailable: Double,
    val marksAwarded: Double,
    val feedback: String
)

enum class OfficialSourceType(val label: String) {
    KTBS_TEXTBOOK("KTBS Textbook"),
    KSEAB_BOARD_PAPER("KSEAB Paper & Key"),
    DIKSHA_RESOURCE("DIKSHA Karnataka")
}

data class OfficialSourceRecord(
    val id: String,
    val sourceType: OfficialSourceType,
    val title: String,
    val languageMedium: String,
    val editionOrYear: String,
    val chaptersCovered: String,
    val provenanceAndPermissionNote: String,
    val verificationState: String
)

data class PyqRecurrenceStat(
    val conceptTag: String,
    val chapterNumber: Int,
    val occurrences: Int,
    val distinctYears: List<String>,
    val totalMarksHistorically: Int,
    val recurrencePercent: Int,
    val yieldLabel: String
)

data class ExaminerEvaluationReport(
    val marksAwarded: Double,
    val maxMarks: Double,
    val stepChecks: List<AnswerStepCheck>,
    val missingSteps: List<String>,
    val detectedWeakConcept: String?,
    val summaryFeedback: String
)

data class ConceptWeaknessInsight(
    val conceptTag: String,
    val chapterNumber: Int,
    val attempts: Int,
    val errors: Int,
    val accuracyPercent: Int,
    val confidenceScore: Int,
    val recommendation: String
)

data class StudyPlanSlot(
    val chapterNumber: Int,
    val chapterTitle: String,
    val focusType: String,
    val dailyMinutes: Int,
    val targetConcept: String,
    val rationale: String
)

enum class OpenWeightProvider(
    val displayName: String,
    val chatCompletionsUrl: String,
    val requiresApiKey: Boolean,
    val badge: String
) {
    OPENROUTER_FREE(
        displayName = "OpenRouter (:free)",
        chatCompletionsUrl = "https://openrouter.ai/api/v1/chat/completions",
        requiresApiKey = true,
        badge = "Free Open-Weight Pool"
    ),
    GROQ_FREE(
        displayName = "Groq Free Tier",
        chatCompletionsUrl = "https://api.groq.com/openai/v1/chat/completions",
        requiresApiKey = true,
        badge = "Ultra-Fast Free"
    ),
    HUGGINGFACE_FREE(
        displayName = "Hugging Face Router",
        chatCompletionsUrl = "https://router.huggingface.co/v1/chat/completions",
        requiresApiKey = true,
        badge = "Open Inference"
    ),
    CEREBRAS_FREE(
        displayName = "Cerebras Free Tier",
        chatCompletionsUrl = "https://api.cerebras.ai/v1/chat/completions",
        requiresApiKey = true,
        badge = "Fast Qwen / Llama"
    ),
    LOCAL_OLLAMA(
        displayName = "Local Ollama (No Key)",
        chatCompletionsUrl = "http://10.0.2.2:11434/v1/chat/completions",
        requiresApiKey = false,
        badge = "Zero-Cost Self-Hosted"
    )
}

data class OpenWeightModelSpec(
    val id: String,
    val label: String,
    val family: String,
    val provider: OpenWeightProvider,
    val supportsVision: Boolean = false
)

object OpenWeightModelCatalog {
    val models: List<OpenWeightModelSpec> = listOf(
        // OpenRouter :free models
        OpenWeightModelSpec("qwen/qwen3-235b-a22b:free", "Qwen 3 235B (Free)", "Qwen", OpenWeightProvider.OPENROUTER_FREE),
        OpenWeightModelSpec("qwen/qwen-2.5-72b-instruct:free", "Qwen 2.5 72B (Free)", "Qwen", OpenWeightProvider.OPENROUTER_FREE),
        OpenWeightModelSpec("qwen/qwen2.5-vl-72b-instruct:free", "Qwen 2.5 VL Vision (Free)", "Qwen Vision", OpenWeightProvider.OPENROUTER_FREE, supportsVision = true),
        OpenWeightModelSpec("deepseek/deepseek-r1:free", "DeepSeek R1 Math (Free)", "DeepSeek", OpenWeightProvider.OPENROUTER_FREE),
        OpenWeightModelSpec("meta-llama/llama-3.3-70b-instruct:free", "Llama 3.3 70B (Free)", "Llama", OpenWeightProvider.OPENROUTER_FREE),
        OpenWeightModelSpec("mistralai/mistral-small-3.1-24b-instruct:free", "Mistral Small 3.1 (Free)", "Mistral", OpenWeightProvider.OPENROUTER_FREE),

        // Groq free tier
        OpenWeightModelSpec("qwen/qwen3-32b", "Qwen 3 32B (Groq Free)", "Qwen", OpenWeightProvider.GROQ_FREE),
        OpenWeightModelSpec("llama-3.3-70b-versatile", "Llama 3.3 70B (Groq Free)", "Llama", OpenWeightProvider.GROQ_FREE),
        OpenWeightModelSpec("meta-llama/llama-4-scout-17b-16e-instruct", "Llama 4 Scout Vision (Groq)", "Llama Vision", OpenWeightProvider.GROQ_FREE, supportsVision = true),

        // Hugging Face Router
        OpenWeightModelSpec("Qwen/Qwen2.5-72B-Instruct", "Qwen 2.5 72B (HF Free)", "Qwen", OpenWeightProvider.HUGGINGFACE_FREE),
        OpenWeightModelSpec("Qwen/Qwen2.5-VL-7B-Instruct", "Qwen 2.5 VL 7B Vision (HF)", "Qwen Vision", OpenWeightProvider.HUGGINGFACE_FREE, supportsVision = true),
        OpenWeightModelSpec("deepseek-ai/DeepSeek-R1-Distill-Qwen-32B", "DeepSeek R1 Qwen 32B (HF)", "DeepSeek", OpenWeightProvider.HUGGINGFACE_FREE),

        // Cerebras free tier
        OpenWeightModelSpec("qwen-3-32b", "Qwen 3 32B (Cerebras Free)", "Qwen", OpenWeightProvider.CEREBRAS_FREE),
        OpenWeightModelSpec("llama-3.3-70b", "Llama 3.3 70B (Cerebras Free)", "Llama", OpenWeightProvider.CEREBRAS_FREE),

        // Local Ollama (zero-key)
        OpenWeightModelSpec("qwen2.5-math:7b", "Qwen 2.5 Math 7B (Local)", "Qwen", OpenWeightProvider.LOCAL_OLLAMA),
        OpenWeightModelSpec("qwen2.5vl:7b", "Qwen 2.5 VL Vision (Local)", "Qwen Vision", OpenWeightProvider.LOCAL_OLLAMA, supportsVision = true),
        OpenWeightModelSpec("deepseek-r1:7b", "DeepSeek R1 7B (Local)", "DeepSeek", OpenWeightProvider.LOCAL_OLLAMA)
    )

    fun modelsFor(provider: OpenWeightProvider): List<OpenWeightModelSpec> =
        models.filter { it.provider == provider }

    fun defaultTextModel(provider: OpenWeightProvider): OpenWeightModelSpec =
        models.firstOrNull { it.provider == provider && !it.supportsVision } ?: models.first()

    fun defaultVisionModel(provider: OpenWeightProvider): OpenWeightModelSpec =
        models.firstOrNull { it.provider == provider && it.supportsVision }
            ?: OpenWeightModelSpec("qwen/qwen2.5-vl-72b-instruct:free", "Qwen 2.5 VL Vision (Free)", "Qwen Vision", OpenWeightProvider.OPENROUTER_FREE, supportsVision = true)
}


