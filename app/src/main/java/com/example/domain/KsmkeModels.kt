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

data class TopicDetailedNote(
    val topicNumber: String,
    val topicTitleEn: String,
    val topicTitleKn: String,
    val coreConcepts: List<String>,
    val formulasAndRules: List<String>,
    val derivationsOrMethods: List<String>,
    val workedExamples: List<SolvedExample>,
    val examMistakesToAvoid: List<String>
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
    val commonMistakes: String
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
