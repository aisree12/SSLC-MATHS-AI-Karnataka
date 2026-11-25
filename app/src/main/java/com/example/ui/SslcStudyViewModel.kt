package com.example.ui

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.core.PdfExportManager
import com.example.core.PdfExportResult
import com.example.data.ksmke.KsmkeSyllabusRepository
import android.net.Uri
import com.example.data.local.ChapterProgressEntity
import com.example.data.local.ConceptStatEntity
import com.example.data.local.DailyGoalEntity
import com.example.data.local.ExamHistoryEntity
import com.example.data.local.LocalProgressRepository
import com.example.data.local.OnDeviceModelManager
import com.example.data.local.OnDeviceModelStatus
import com.example.data.remote.AiTeacherRepository
import com.example.data.remote.ChapterMasteryDoc
import com.example.data.remote.ExamAttemptDoc
import com.example.data.remote.FirestoreSyncRepository
import com.example.data.remote.StudyGoalDoc
import com.example.data.remote.UserProfileDoc
import com.example.domain.ChapterQuestionPaper
import com.example.domain.ChatTurn
import com.example.domain.ConceptWeaknessInsight
import com.example.domain.ExamMode
import com.example.domain.ExaminerEvaluationReport
import com.example.domain.IndependentMarkingEngine
import com.example.domain.MathEngineSolution
import com.example.domain.MathVerificationEngine
import com.example.domain.MasteryStatus
import com.example.domain.OpenWeightModelCatalog
import com.example.domain.OpenWeightModelSpec
import com.example.domain.OpenWeightProvider
import com.example.domain.PyqQuestion
import com.example.domain.TargetedPaperQuestion
import com.example.domain.TeachingLanguage
import com.example.domain.TopicCompletionStatus
import com.example.domain.WeaknessAndPlannerEngine
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "SslcStudyVM"

sealed interface CloudUiState<out T> {
    data object Loading : CloudUiState<Nothing>
    data class Success<T>(val data: T) : CloudUiState<T>
    data class Error(val message: String) : CloudUiState<Nothing>
}

enum class ExamPhase(val label: String, val guidance: String) {
    READ_AND_PLAN("Phase 1 • Read & Plan (15 min)", "Scan all 38 questions, mark easy 1M/2M picks first, and check internal choices."),
    SOLVE("Phase 2 • Solve Questions (150 min)", "Write Given -> Formula -> Substitution -> Steps -> Final Answer with units."),
    REVIEW("Phase 3 • Review & Verify (30 min)", "Check signs, graph scale, theorem figures, and unanswered questions.")
}

data class ExamSessionUiState(
    val active: Boolean = false,
    val finished: Boolean = false,
    val mode: ExamMode = ExamMode.FULL_BOARD_80,
    val questions: List<PyqQuestion> = emptyList(),
    val currentIndex: Int = 0,
    val elapsedSeconds: Int = 0,
    val totalSeconds: Int = 195 * 60,
    val questionSeconds: Map<Int, Int> = emptyMap(),
    val selectedMcqAnswers: Map<Int, Int> = emptyMap(),
    val writtenAnswers: Map<Int, String> = emptyMap(),
    val selfAssessedMarks: Map<Int, Double> = emptyMap(),
    val skippedIndices: Set<Int> = emptySet(),
    val reportSummary: String = "",
    val slowestDiagnostic: String = "",
    val totalScore: Double = 0.0
) {
    val currentPhase: ExamPhase
        get() {
            if (mode != ExamMode.FULL_BOARD_80) return ExamPhase.SOLVE
            val elapsedMin = elapsedSeconds / 60
            return when {
                elapsedMin < 15 -> ExamPhase.READ_AND_PLAN
                elapsedMin < 165 -> ExamPhase.SOLVE
                else -> ExamPhase.REVIEW
            }
        }
}

data class CoachDashboardData(
    val profile: UserProfileDoc?,
    val masteryByChapter: Map<Int, ChapterMasteryDoc>,
    val examAttempts: List<ExamAttemptDoc>,
    val goals: List<StudyGoalDoc>
)

class SslcStudyViewModel(
    private val firestoreSync: FirestoreSyncRepository,
    private val localProgress: LocalProgressRepository,
    private val aiTeacher: AiTeacherRepository,
    private val currentUserId: String,
    private val initialDisplayName: String,
    private val onDeviceModelManager: OnDeviceModelManager? = null
) : ViewModel() {

    private val _language = MutableStateFlow(TeachingLanguage.BILINGUAL)
    val language: StateFlow<TeachingLanguage> = _language.asStateFlow()

    private val _activeChapter = MutableStateFlow<Int?>(10)
    val activeChapter: StateFlow<Int?> = _activeChapter.asStateFlow()

    private val _offlineStudyMode = MutableStateFlow(false)
    val offlineStudyMode: StateFlow<Boolean> = _offlineStudyMode.asStateFlow()

    private val _selectedProvider = MutableStateFlow(aiTeacher.activeProvider)
    val selectedProvider: StateFlow<OpenWeightProvider> = _selectedProvider.asStateFlow()

    private val _selectedModelId = MutableStateFlow(aiTeacher.activeModelId)
    val selectedModelId: StateFlow<String> = _selectedModelId.asStateFlow()

    val onDeviceModelStatuses: StateFlow<Map<String, OnDeviceModelStatus>> =
        onDeviceModelManager?.statuses ?: MutableStateFlow<Map<String, OnDeviceModelStatus>>(emptyMap()).asStateFlow()

    val isOpenAiKeyConfigured: Boolean
        get() = aiTeacher.isApiKeyConfigured()

    private val _chatTurns = MutableStateFlow(
        listOf(
            ChatTurn(
                id = "welcome",
                isStudent = false,
                text = "Namaste! I am Guruji, your Karnataka SSLC Class 10 Mathematics teacher. " +
                    "We will build your confidence step by step — never rush, never feel shy about mistakes. " +
                    "Ask me anything in English or ಕನ್ನಡ (for example: \"Why do we use the quadratic formula?\" or \"Explain Thales Theorem\")."
            )
        )
    )
    val chatTurns: StateFlow<List<ChatTurn>> = _chatTurns.asStateFlow()

    private val _aiError = MutableStateFlow<String?>(null)
    val aiError: StateFlow<String?> = _aiError.asStateFlow()

    private val _isAiBusy = MutableStateFlow(false)
    val isAiBusy: StateFlow<Boolean> = _isAiBusy.asStateFlow()

    private val _generatedPyqs = MutableStateFlow<List<PyqQuestion>>(emptyList())
    val generatedPyqs: StateFlow<List<PyqQuestion>> = _generatedPyqs.asStateFlow()

    private val _pyqGenState = MutableStateFlow<CloudUiState<String>?>(null)
    val pyqGenState: StateFlow<CloudUiState<String>?> = _pyqGenState.asStateFlow()

    private val _answerEvalState = MutableStateFlow<CloudUiState<String>?>(null)
    val answerEvalState: StateFlow<CloudUiState<String>?> = _answerEvalState.asStateFlow()

    private val _examinerReport = MutableStateFlow<ExaminerEvaluationReport?>(null)
    val examinerReport: StateFlow<ExaminerEvaluationReport?> = _examinerReport.asStateFlow()

    private val _daysUntilExam = MutableStateFlow(60)
    val daysUntilExam: StateFlow<Int> = _daysUntilExam.asStateFlow()

    private val _dailyStudyMinutes = MutableStateFlow(60)
    val dailyStudyMinutes: StateFlow<Int> = _dailyStudyMinutes.asStateFlow()

    private val _mathSolution = MutableStateFlow<MathEngineSolution?>(null)
    val mathSolution: StateFlow<MathEngineSolution?> = _mathSolution.asStateFlow()

    private val _examState = MutableStateFlow(ExamSessionUiState())
    val examState: StateFlow<ExamSessionUiState> = _examState.asStateFlow()

    private val _syncMessage = MutableStateFlow<String?>(null)
    val syncMessage: StateFlow<String?> = _syncMessage.asStateFlow()

    // Detailed topic notes, Chapterwise Question Papers, PDF Export, and Camera Math Scanner states
    private val _aiChapterNotes = MutableStateFlow<Map<Int, CloudUiState<String>>>(emptyMap())
    val aiChapterNotes: StateFlow<Map<Int, CloudUiState<String>>> = _aiChapterNotes.asStateFlow()

    private val _activeChapterPaper = MutableStateFlow(KsmkeSyllabusRepository.buildChapterQuestionPaper(10))
    val activeChapterPaper: StateFlow<ChapterQuestionPaper> = _activeChapterPaper.asStateFlow()

    private val _aiPaperGenState = MutableStateFlow<CloudUiState<String>?>(null)
    val aiPaperGenState: StateFlow<CloudUiState<String>?> = _aiPaperGenState.asStateFlow()

    private val _paperMcqAnswers = MutableStateFlow<Map<String, Int>>(emptyMap())
    val paperMcqAnswers: StateFlow<Map<String, Int>> = _paperMcqAnswers.asStateFlow()

    private val _paperWrittenAnswers = MutableStateFlow<Map<String, String>>(emptyMap())
    val paperWrittenAnswers: StateFlow<Map<String, String>> = _paperWrittenAnswers.asStateFlow()

    private val _paperQaChecks = MutableStateFlow<Map<String, CloudUiState<String>>>(emptyMap())
    val paperQaChecks: StateFlow<Map<String, CloudUiState<String>>> = _paperQaChecks.asStateFlow()

    private val _pdfExportState = MutableStateFlow<CloudUiState<PdfExportResult>?>(null)
    val pdfExportState: StateFlow<CloudUiState<PdfExportResult>?> = _pdfExportState.asStateFlow()

    private val _cameraScanState = MutableStateFlow<CloudUiState<String>?>(null)
    val cameraScanState: StateFlow<CloudUiState<String>?> = _cameraScanState.asStateFlow()

    private val _pyqPaperTabSelected = MutableStateFlow(false)
    val pyqPaperTabSelected: StateFlow<Boolean> = _pyqPaperTabSelected.asStateFlow()

    fun setPyqPaperTabSelected(selected: Boolean) {
        _pyqPaperTabSelected.value = selected
    }

    private var examTimerJob: Job? = null

    val localGoals: StateFlow<List<DailyGoalEntity>> = localProgress.dailyGoals
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val localChapterProgress: StateFlow<List<ChapterProgressEntity>> = localProgress.chapterProgress
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val localExamHistory: StateFlow<List<ExamHistoryEntity>> = localProgress.examHistory
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val topicVerifications: StateFlow<Map<String, TopicCompletionStatus>> =
        localProgress.topicVerifications
            .combine(MutableStateFlow(Unit)) { entities, _ ->
                entities.associate { entity ->
                    val status = enumValues<TopicCompletionStatus>()
                        .firstOrNull { it.name == entity.completionStatus }
                        ?: TopicCompletionStatus.NOT_VERIFIED
                    entity.topicNumber to status
                }
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyMap())

    val conceptStats: StateFlow<List<ConceptStatEntity>> = localProgress.conceptStats
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val conceptWeaknesses: StateFlow<List<ConceptWeaknessInsight>> =
        localProgress.conceptStats
            .combine(MutableStateFlow(Unit)) { stats, _ ->
                WeaknessAndPlannerEngine.analyzeConceptWeaknesses(stats)
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), emptyList())

    val cloudDashboard: StateFlow<CloudUiState<CoachDashboardData>> = combine(
        firestoreSync.observeProfile(currentUserId),
        firestoreSync.observeChapterMastery(currentUserId),
        firestoreSync.observeExamAttempts(currentUserId),
        firestoreSync.observeStudyGoals(currentUserId)
    ) { profile, mastery, exams, goals ->
        CoachDashboardData(
            profile = profile,
            masteryByChapter = mastery.associateBy { it.chapterNumber.toInt() },
            examAttempts = exams,
            goals = goals
        )
    }
        .mapSuccess()
        .catch { error ->
            val firestoreError = generateSequence(error) { it.cause }
                .filterIsInstance<FirebaseFirestoreException>()
                .firstOrNull()
            if (firestoreError == null && error is CancellationException) throw error
            val rootError = firestoreError ?: error
            Log.w(TAG, "Error observing cloud dashboard", rootError)
            emit(CloudUiState.Error(rootError.message ?: "Failed to sync cloud progress"))
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000L),
            initialValue = CloudUiState.Loading
        )

    private fun kotlinx.coroutines.flow.Flow<CoachDashboardData>.mapSuccess():
        kotlinx.coroutines.flow.Flow<CloudUiState<CoachDashboardData>> =
        kotlinx.coroutines.flow.flow {
            collect { data ->
                data.profile?.let { _language.value = it.safeLanguage() }
                emit(CloudUiState.Success(data))
            }
        }

    init {
        viewModelScope.launch {
            firestoreSync.ensureUserProfileExists(initialDisplayName)
        }
        viewModelScope.launch {
            localProgress.dailyGoals.collect { goals ->
                if (goals.isEmpty()) {
                    localProgress.ensureDefaultGoalsIfEmpty(goals)
                }
            }
        }
    }

    fun setLanguage(lang: TeachingLanguage) {
        _language.value = lang
        val current = (cloudDashboard.value as? CloudUiState.Success)?.data?.profile
        viewModelScope.launch {
            firestoreSync.updateProfilePreferences(
                preferredLanguage = lang,
                targetScore = current?.targetScore?.toInt() ?: 90,
                readinessScore = current?.readinessScore?.toInt() ?: 52,
                completedTodayMinutes = current?.completedTodayMinutes?.toInt() ?: 0
            )
        }
    }

    fun setActiveChapter(chapterNumber: Int?) {
        _activeChapter.value = chapterNumber
    }

    fun setOfflineStudyMode(offline: Boolean) {
        _offlineStudyMode.value = offline
    }

    fun selectOpenWeightProvider(provider: OpenWeightProvider) {
        _selectedProvider.value = provider
        aiTeacher.activeProvider = provider
        val defaultModel = OpenWeightModelCatalog.defaultTextModel(provider).id
        _selectedModelId.value = defaultModel
        aiTeacher.activeModelId = defaultModel
    }

    fun selectOpenWeightModel(modelId: String) {
        _selectedModelId.value = modelId
        aiTeacher.activeModelId = modelId
    }

    fun selectAiModel(spec: OpenWeightModelSpec) {
        _selectedProvider.value = spec.provider
        _selectedModelId.value = spec.id
        aiTeacher.activeProvider = spec.provider
        aiTeacher.activeModelId = spec.id
        _aiError.value = null
    }

    fun downloadOnDeviceModel(modelId: String) {
        onDeviceModelManager?.startDownload(modelId)
    }

    fun cancelOnDeviceModelDownload(modelId: String) {
        onDeviceModelManager?.cancelDownload(modelId)
    }

    fun deleteOnDeviceModel(modelId: String) {
        onDeviceModelManager?.deleteModel(modelId)
    }

    fun importLocalOnDeviceModel(modelId: String, uri: Uri) {
        val manager = onDeviceModelManager ?: return
        viewModelScope.launch {
            val res = manager.importLocalModelFile(modelId, uri)
            res.onSuccess {
                _syncMessage.value = "Quantised on-device model imported and ready for offline AI."
            }.onFailure { err ->
                _aiError.value = err.localizedMessage ?: "Failed to import .task model file."
            }
        }
    }

    fun dismissSyncMessage() {
        _syncMessage.value = null
    }

    fun clearAiError() {
        _aiError.value = null
    }

    fun askTeacher(studentMessage: String) {
        val trimmed = studentMessage.trim()
        if (trimmed.isBlank() || _isAiBusy.value) return

        _aiError.value = null
        val userTurn = ChatTurn(
            id = "u_${System.currentTimeMillis()}",
            isStudent = true,
            text = trimmed
        )
        val replyId = "a_${System.currentTimeMillis() + 1}"

        // Check if student gave a quadratic equation for deterministic Math Engine verification
        val mathNote = detectMathVerificationNote(trimmed)

        if (_offlineStudyMode.value) {
            val offlineText = aiTeacher.buildOfflineKsmkeResponse(
                studentMessage = trimmed,
                chapterNumber = _activeChapter.value,
                language = _language.value
            )
            _chatTurns.update {
                it + userTurn + ChatTurn(
                    id = replyId,
                    isStudent = false,
                    text = offlineText,
                    mathVerificationNote = mathNote
                )
            }
            return
        }

        val recentContext = _chatTurns.value.takeLast(4).joinToString("\n") {
            if (it.isStudent) "Student: ${it.text}" else "Teacher: ${it.text.take(240)}"
        }

        _chatTurns.update {
            it + userTurn + ChatTurn(
                id = replyId,
                isStudent = false,
                text = "",
                mathVerificationNote = mathNote,
                isStreaming = true
            )
        }
        _isAiBusy.value = true

        viewModelScope.launch {
            try {
                aiTeacher.streamTutorReplyWithFallback(
                    studentMessage = trimmed,
                    chapterNumber = _activeChapter.value,
                    language = _language.value,
                    historySummary = recentContext
                ).collect { accumulated ->
                    _chatTurns.update { list ->
                        list.map { turn ->
                            if (turn.id == replyId) {
                                turn.copy(text = accumulated, isStreaming = true)
                            } else turn
                        }
                    }
                }
                _chatTurns.update { list ->
                    list.map { turn ->
                        if (turn.id == replyId) turn.copy(isStreaming = false) else turn
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                val msg = aiTeacher.formatAiError(e)
                _aiError.value = msg
                _chatTurns.update { list ->
                    list.filterNot { it.id == replyId && it.text.isBlank() }
                        .map { if (it.id == replyId) it.copy(isStreaming = false) else it }
                }
            } finally {
                _isAiBusy.value = false
            }
        }
    }

    fun useOfflineFallbackForLastQuestion() {
        val lastStudent = _chatTurns.value.lastOrNull { it.isStudent }?.text ?: return
        _aiError.value = null
        val offlineText = aiTeacher.buildOfflineKsmkeResponse(
            studentMessage = lastStudent,
            chapterNumber = _activeChapter.value,
            language = _language.value
        )
        _chatTurns.update {
            it + ChatTurn(
                id = "off_${System.currentTimeMillis()}",
                isStudent = false,
                text = offlineText,
                mathVerificationNote = detectMathVerificationNote(lastStudent)
            )
        }
    }

    private fun detectMathVerificationNote(input: String): String? {
        val compact = input.replace(" ", "")
        val quad = Regex("(-?\\d+)x\\^?2([+-]\\d+)x([+-]\\d+)").find(compact)
        if (quad != null) {
            val a = quad.groupValues[1].toDoubleOrNull() ?: return null
            val b = quad.groupValues[2].toDoubleOrNull() ?: return null
            val c = quad.groupValues[3].toDoubleOrNull() ?: return null
            val sol = MathVerificationEngine.solveQuadratic(a, b, c)
            return "Math Engine Verified: ${sol.finalAnswer} (Δ = ${MathVerificationEngine.fmt(b * b - 4 * a * c)})"
        }
        return null
    }

    fun runQuadraticVerifier(a: Double, b: Double, c: Double) {
        _mathSolution.value = MathVerificationEngine.solveQuadratic(a, b, c)
    }

    fun runApVerifier(a: Double, d: Double, n: Int) {
        _mathSolution.value = MathVerificationEngine.solveArithmeticProgression(a, d, n)
    }

    fun runLinearPairVerifier(a1: Double, b1: Double, c1: Double, a2: Double, b2: Double, c2: Double) {
        _mathSolution.value = MathVerificationEngine.solveLinearPair(a1, b1, c1, a2, b2, c2)
    }

    fun runCoordinateVerifier(x1: Double, y1: Double, x2: Double, y2: Double, m1: Double, m2: Double) {
        _mathSolution.value = MathVerificationEngine.solveCoordinateGeometry(x1, y1, x2, y2, m1, m2)
    }

    fun generateAiPracticeQuestions(chapterNumber: Int, marks: Int) {
        _pyqGenState.value = CloudUiState.Loading
        viewModelScope.launch {
            try {
                val created = aiTeacher.generatePracticeQuestions(chapterNumber, marks, count = 3)
                _generatedPyqs.update { created + it }
                _pyqGenState.value = CloudUiState.Success("Generated ${created.size} new AI Practice questions (marked AI Practice).")
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _pyqGenState.value = CloudUiState.Error(aiTeacher.formatAiError(e))
            }
        }
    }

    fun setTopicVerificationStatus(
        topicNumber: String,
        chapterNumber: Int,
        status: TopicCompletionStatus
    ) {
        viewModelScope.launch {
            localProgress.saveTopicVerification(
                topicNumber = topicNumber,
                chapterNumber = chapterNumber,
                completionStatus = status.name
            )
            _syncMessage.value = "Topic $topicNumber marked: ${status.label}."
        }
    }

    fun updateStudyPlannerConfig(daysRemaining: Int, dailyMinutes: Int) {
        _daysUntilExam.value = daysRemaining.coerceIn(1, 365)
        _dailyStudyMinutes.value = dailyMinutes.coerceIn(20, 300)
    }

    fun setConceptConfidenceRating(conceptTag: String, chapterNumber: Int, rating: Int) {
        viewModelScope.launch {
            localProgress.setConceptConfidence(conceptTag, chapterNumber, rating)
        }
    }

    fun evaluateStudentAnswer(
        question: PyqQuestion,
        studentSteps: String
    ) {
        if (studentSteps.isBlank()) {
            _examinerReport.value = null
            _answerEvalState.value = CloudUiState.Error("Write your solution steps first so Guruji can check every marking point.")
            return
        }

        val concept = question.conceptTag.ifBlank { question.topic }
        val deterministicReport = IndependentMarkingEngine.evaluateWrittenSolution(
            questionEn = question.questionEn,
            officialAnswer = question.officialAnswer,
            markingPoints = question.markingPoints,
            maxMarks = question.marks,
            studentSteps = studentSteps,
            conceptTag = concept
        )
        _examinerReport.value = deterministicReport

        viewModelScope.launch {
            localProgress.recordConceptAttempt(
                conceptTag = concept,
                chapterNumber = question.chapterNumber,
                hadError = deterministicReport.marksAwarded < deterministicReport.maxMarks * 0.75,
                mistakeSummary = deterministicReport.missingSteps.firstOrNull().orEmpty()
            )
        }

        if (_offlineStudyMode.value) {
            _answerEvalState.value = CloudUiState.Success(
                "Offline Real Examiner Evaluation Complete (${MathVerificationEngine.fmt(deterministicReport.marksAwarded)}/${question.marks} Marks). Review the step marks breakdown above."
            )
            return
        }

        _answerEvalState.value = CloudUiState.Loading
        viewModelScope.launch {
            try {
                val feedback = aiTeacher.evaluateAnswerPresentation(
                    questionText = question.questionEn,
                    expectedAnswer = question.officialAnswer,
                    maxMarks = question.marks,
                    studentSteps = studentSteps,
                    language = _language.value
                )
                _answerEvalState.value = CloudUiState.Success(feedback)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _answerEvalState.value = CloudUiState.Success(
                    "Deterministic Examiner Score: ${MathVerificationEngine.fmt(deterministicReport.marksAwarded)}/${question.marks}M. " +
                        "(AI detailed commentary offline: ${aiTeacher.formatAiError(e)})"
                )
            }
        }
    }

    // --- Detailed Notes, Chapterwise Question Papers, Integrated Q&A, PDF Export, Camera Scan ---

    fun generateAiChapterNotes(chapterNumber: Int) {
        _aiChapterNotes.update { it + (chapterNumber to CloudUiState.Loading) }
        viewModelScope.launch {
            try {
                val notes = aiTeacher.generateDetailedTopicNotes(chapterNumber, _language.value)
                _aiChapterNotes.update { it + (chapterNumber to CloudUiState.Success(notes)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aiChapterNotes.update { it + (chapterNumber to CloudUiState.Error(aiTeacher.formatAiError(e))) }
            }
        }
    }

    fun selectPaperChapter(chapterNumber: Int) {
        _activeChapter.value = chapterNumber
        _activeChapterPaper.value = KsmkeSyllabusRepository.buildChapterQuestionPaper(chapterNumber)
        _aiPaperGenState.value = null
    }

    fun generateAiProbabilityPaper(chapterNumber: Int) {
        _aiPaperGenState.value = CloudUiState.Loading
        viewModelScope.launch {
            try {
                val aiPaper = aiTeacher.generateChapterQuestionPaperAi(chapterNumber, _language.value)
                _activeChapterPaper.value = aiPaper
                _aiPaperGenState.value = CloudUiState.Success(
                    "Generated fresh ${aiPaper.totalMarks}-mark AI PYQ-targeted paper for Chapter $chapterNumber."
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _aiPaperGenState.value = CloudUiState.Error(aiTeacher.formatAiError(e))
            }
        }
    }

    fun selectPaperMcqAnswer(questionId: String, optionIndex: Int) {
        _paperMcqAnswers.update { it + (questionId to optionIndex) }
    }

    fun updatePaperWrittenAnswer(questionId: String, text: String) {
        _paperWrittenAnswers.update { it + (questionId to text) }
    }

    fun checkPaperQuestionAndAnswer(
        question: TargetedPaperQuestion,
        studentUnableToSolve: Boolean = false
    ) {
        val mcqIdx = _paperMcqAnswers.value[question.id]
        val written = _paperWrittenAnswers.value[question.id].orEmpty()

        // Fast deterministic offline handling when offline study mode is enabled
        if (_offlineStudyMode.value) {
            val statusHeader = when {
                studentUnableToSolve || (written.isBlank() && mcqIdx == null) ->
                    "Step-by-Step Explanation (Offline KSMKE):"
                question.mcqOptions.isNotEmpty() && mcqIdx != null ->
                    if (mcqIdx == question.correctMcqIndex) "Correct MCQ choice! (${question.officialAnswer})"
                    else "MCQ Needs Review — You picked '${question.mcqOptions.getOrNull(mcqIdx)}', official answer is '${question.officialAnswer}'."
                else -> "Compare your written steps against the KSEAB marking scheme below:"
            }
            val body = buildString {
                appendLine(statusHeader)
                appendLine("Official Answer: ${question.officialAnswer}")
                question.stepByStepSolution.forEach { appendLine("• $it") }
                appendLine("Avoid Mistake: ${question.commonMistake}")
            }
            _paperQaChecks.update { it + (question.id to CloudUiState.Success(body)) }
            return
        }

        _paperQaChecks.update { it + (question.id to CloudUiState.Loading) }
        viewModelScope.launch {
            try {
                val feedback = aiTeacher.checkQuestionAndAnswer(
                    question = question,
                    studentMcqIndex = mcqIdx,
                    studentWrittenSteps = written,
                    studentUnableToSolve = studentUnableToSolve,
                    language = _language.value
                )
                _paperQaChecks.update { it + (question.id to CloudUiState.Success(feedback)) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _paperQaChecks.update { it + (question.id to CloudUiState.Error(aiTeacher.formatAiError(e))) }
            }
        }
    }

    fun exportChapterNotesPdf(context: Context, chapterNumber: Int) {
        val appContext = context.applicationContext
        val chapter = KsmkeSyllabusRepository.getChapter(chapterNumber)
        val aiNotes = (_aiChapterNotes.value[chapterNumber] as? CloudUiState.Success)?.data
        _pdfExportState.value = CloudUiState.Loading
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) {
                PdfExportManager.exportChapterNotesPdf(
                    context = appContext,
                    chapter = chapter,
                    topics = chapter.detailedTopics,
                    aiDeepDiveMarkdown = aiNotes
                )
            }
            _pdfExportState.value = res.fold(
                onSuccess = { CloudUiState.Success(it) },
                onFailure = { CloudUiState.Error(it.localizedMessage ?: "Failed to save PDF") }
            )
        }
    }

    fun exportChapterPaperPdf(context: Context, includeAnswerKey: Boolean) {
        val appContext = context.applicationContext
        val paper = _activeChapterPaper.value
        _pdfExportState.value = CloudUiState.Loading
        viewModelScope.launch {
            val res = withContext(Dispatchers.IO) {
                PdfExportManager.exportQuestionPaperPdf(
                    context = appContext,
                    paper = paper,
                    includeAnswerKey = includeAnswerKey
                )
            }
            _pdfExportState.value = res.fold(
                onSuccess = { CloudUiState.Success(it) },
                onFailure = { CloudUiState.Error(it.localizedMessage ?: "Failed to save paper PDF") }
            )
        }
    }

    fun clearPdfExportState() {
        _pdfExportState.value = null
    }

    fun analyzeCameraMathPhoto(
        bitmap: Bitmap,
        linkedQuestionText: String? = null,
        expectedAnswer: String? = null,
        studentUnableToSolve: Boolean = false
    ) {
        _cameraScanState.value = CloudUiState.Loading
        viewModelScope.launch {
            try {
                val result = aiTeacher.analyzeScannedSolution(
                    bitmap = bitmap,
                    linkedQuestionText = linkedQuestionText,
                    expectedAnswer = expectedAnswer,
                    studentUnableToSolve = studentUnableToSolve,
                    chapterNumber = _activeChapter.value,
                    language = _language.value
                )
                _cameraScanState.value = CloudUiState.Success(result)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _cameraScanState.value = CloudUiState.Error(aiTeacher.formatAiError(e))
            }
        }
    }

    fun clearCameraScanState() {
        _cameraScanState.value = null
    }

    fun addScanAnalysisToChat(questionLabel: String, analysisText: String) {
        val ts = System.currentTimeMillis()
        _chatTurns.update {
            it + ChatTurn(
                id = "scan_q_$ts",
                isStudent = true,
                text = "[Scanned Math Photo] $questionLabel"
            ) + ChatTurn(
                id = "scan_a_${ts + 1}",
                isStudent = false,
                text = analysisText
            )
        }
    }

    fun updateChapterMastery(
        chapterNumber: Int,
        status: MasteryStatus,
        accuracyPercent: Int,
        questionsSolved: Int,
        mistakeNote: String
    ) {
        viewModelScope.launch {
            localProgress.saveChapterProgress(
                ChapterProgressEntity(
                    chapterNumber = chapterNumber,
                    masteryStatus = status.name,
                    accuracyPercent = accuracyPercent.coerceIn(0, 100),
                    questionsSolved = questionsSolved.coerceAtLeast(0),
                    commonMistakeNote = mistakeNote.take(500)
                )
            )
            val res = firestoreSync.saveChapterMastery(
                chapterNumber = chapterNumber,
                status = status,
                accuracyPercent = accuracyPercent,
                questionsSolved = questionsSolved,
                commonMistakeNote = mistakeNote
            )
            _syncMessage.value = if (res.isSuccess) {
                "Chapter $chapterNumber mastery saved & synced."
            } else {
                "Saved offline; cloud sync pending (${res.exceptionOrNull()?.localizedMessage ?: "offline"})."
            }
        }
    }

    fun toggleDailyGoal(goal: DailyGoalEntity) {
        val updated = goal.copy(completed = !goal.completed)
        viewModelScope.launch {
            localProgress.saveGoal(updated)
            firestoreSync.upsertStudyGoal(
                goalId = updated.goalId,
                title = updated.title,
                chapterNumber = updated.chapterNumber,
                targetMinutes = updated.targetMinutes,
                completed = updated.completed,
                scheduledSlot = updated.scheduledSlot
            )
        }
    }

    fun addPersonalizedGoal(chapterNumber: Int, title: String, targetMinutes: Int) {
        val safeTitle = title.trim().ifBlank { "Practise Ch $chapterNumber board questions" }.take(120)
        val goalId = "goal_${chapterNumber}_${System.currentTimeMillis()}"
        val entity = DailyGoalEntity(
            goalId = goalId,
            title = safeTitle,
            chapterNumber = chapterNumber,
            targetMinutes = targetMinutes.coerceIn(5, 180),
            completed = false,
            scheduledSlot = "Personal Focus"
        )
        viewModelScope.launch {
            localProgress.saveGoal(entity)
            val res = firestoreSync.upsertStudyGoal(
                goalId = goalId,
                title = safeTitle,
                chapterNumber = chapterNumber,
                targetMinutes = entity.targetMinutes,
                completed = false,
                scheduledSlot = entity.scheduledSlot
            )
            if (res.isFailure) {
                _syncMessage.value = "Goal saved locally (cloud sync queued)."
            }
        }
    }

    // --- Examination Simulator ---

    fun startExamSimulator(mode: ExamMode) {
        examTimerJob?.cancel()
        val paper = KsmkeSyllabusRepository.buildExamPaper(mode)
        _examState.value = ExamSessionUiState(
            active = true,
            finished = false,
            mode = mode,
            questions = paper,
            currentIndex = 0,
            elapsedSeconds = 0,
            totalSeconds = mode.totalMinutes * 60
        )
        examTimerJob = viewModelScope.launch {
            while (true) {
                delay(1000L)
                val cur = _examState.value
                if (!cur.active || cur.finished) break
                val nextElapsed = cur.elapsedSeconds + 1
                val curQSeconds = cur.questionSeconds[cur.currentIndex] ?: 0
                _examState.value = cur.copy(
                    elapsedSeconds = nextElapsed,
                    questionSeconds = cur.questionSeconds + (cur.currentIndex to (curQSeconds + 1))
                )
                if (nextElapsed >= cur.totalSeconds) {
                    finishExamSimulator()
                    break
                }
            }
        }
    }

    fun selectExamQuestion(index: Int) {
        val cur = _examState.value
        if (index in cur.questions.indices) {
            _examState.value = cur.copy(currentIndex = index)
        }
    }

    fun answerMcqInExam(index: Int, optionIndex: Int) {
        _examState.update { cur ->
            cur.copy(
                selectedMcqAnswers = cur.selectedMcqAnswers + (index to optionIndex),
                skippedIndices = cur.skippedIndices - index
            )
        }
    }

    fun updateWrittenAnswerInExam(index: Int, answerText: String) {
        _examState.update { cur ->
            cur.copy(
                writtenAnswers = cur.writtenAnswers + (index to answerText),
                skippedIndices = if (answerText.isNotBlank()) cur.skippedIndices - index else cur.skippedIndices
            )
        }
    }

    fun setSelfAssessedMarksInExam(index: Int, marks: Double) {
        _examState.update { cur ->
            cur.copy(selfAssessedMarks = cur.selfAssessedMarks + (index to marks))
        }
    }

    fun toggleSkipExamQuestion(index: Int) {
        _examState.update { cur ->
            val next = if (index in cur.skippedIndices) cur.skippedIndices - index else cur.skippedIndices + index
            cur.copy(skippedIndices = next)
        }
    }

    fun finishExamSimulator() {
        examTimerJob?.cancel()
        val cur = _examState.value
        if (!cur.active) return

        var totalScore = 0.0
        var attempted = 0
        var skipped = 0
        var slowestIdx = 0
        var slowestSec = -1

        cur.questions.forEachIndexed { idx, q ->
            val spent = cur.questionSeconds[idx] ?: 0
            if (spent > slowestSec) {
                slowestSec = spent
                slowestIdx = idx
            }
            if (q.mcqOptions.isNotEmpty()) {
                val chosen = cur.selectedMcqAnswers[idx]
                if (chosen != null) {
                    attempted++
                    if (chosen == q.correctMcqIndex) totalScore += q.marks
                } else {
                    skipped++
                }
            } else {
                val written = cur.writtenAnswers[idx].orEmpty()
                val selfMark = cur.selfAssessedMarks[idx]
                if (written.isNotBlank() || selfMark != null) {
                    attempted++
                    totalScore += (selfMark ?: (q.marks * 0.6))
                } else {
                    skipped++
                }
            }
        }

        val slowestQ = cur.questions.getOrNull(slowestIdx)
        val recommendedMin = slowestQ?.format?.recommendedMinutes ?: 5
        val spentMin = (slowestSec.coerceAtLeast(0)) / 60
        val slowestNote = if (slowestQ != null) {
            "You spent ${spentMin}m (${slowestSec}s) on Q${slowestIdx + 1} (${slowestQ.topic}, ${slowestQ.marks}M) where target pace is ${recommendedMin}m. Practise 3 similar problems to sharpen speed."
        } else {
            "Good pacing across questions."
        }

        val pct = ((totalScore / cur.mode.maxMarks) * 100.0).toInt().coerceIn(0, 100)
        val summary = "Scored ${MathVerificationEngine.fmt(totalScore)} / ${cur.mode.maxMarks} ($pct%). Attempted $attempted of ${cur.questions.size}; skipped $skipped."

        _examState.value = cur.copy(
            finished = true,
            totalScore = totalScore,
            slowestDiagnostic = slowestNote,
            reportSummary = summary
        )

        viewModelScope.launch {
            // Record concept-level attempts for intelligent weakness detection
            cur.questions.forEachIndexed { idx, q ->
                val concept = q.conceptTag.ifBlank { q.topic }
                if (q.mcqOptions.isNotEmpty()) {
                    val chosen = cur.selectedMcqAnswers[idx]
                    if (chosen != null) {
                        localProgress.recordConceptAttempt(
                            conceptTag = concept,
                            chapterNumber = q.chapterNumber,
                            hadError = chosen != q.correctMcqIndex,
                            mistakeSummary = if (chosen != q.correctMcqIndex) q.commonMistakes else ""
                        )
                    }
                } else {
                    val written = cur.writtenAnswers[idx].orEmpty()
                    if (written.isNotBlank()) {
                        val rep = IndependentMarkingEngine.evaluateWrittenSolution(
                            questionEn = q.questionEn,
                            officialAnswer = q.officialAnswer,
                            markingPoints = q.markingPoints,
                            maxMarks = q.marks,
                            studentSteps = written,
                            conceptTag = concept
                        )
                        localProgress.recordConceptAttempt(
                            conceptTag = concept,
                            chapterNumber = q.chapterNumber,
                            hadError = rep.marksAwarded < rep.maxMarks * 0.75,
                            mistakeSummary = rep.missingSteps.firstOrNull().orEmpty()
                        )
                    }
                }
            }

            localProgress.saveExamAttempt(
                ExamHistoryEntity(
                    examTitle = cur.mode.title,
                    examMode = cur.mode.name,
                    scoreObtained = totalScore,
                    maxMarks = cur.mode.maxMarks,
                    durationSeconds = cur.elapsedSeconds,
                    questionsAttempted = attempted,
                    questionsSkipped = skipped,
                    slowestTopic = slowestQ?.topic ?: "Mixed",
                    feedbackSummary = "$summary $slowestNote"
                )
            )
            firestoreSync.recordExamAttempt(
                examTitle = cur.mode.title,
                examMode = cur.mode.name,
                scoreObtained = totalScore,
                maxMarks = cur.mode.maxMarks,
                durationSeconds = cur.elapsedSeconds,
                questionsAttempted = attempted,
                questionsSkipped = skipped,
                slowestTopic = slowestQ?.topic ?: "Mixed",
                feedbackSummary = "$summary $slowestNote".take(1000)
            )
            val profile = (cloudDashboard.value as? CloudUiState.Success)?.data?.profile
            val updatedReadiness = (((profile?.readinessScore?.toInt() ?: 52) + pct) / 2).coerceIn(10, 98)
            firestoreSync.updateProfilePreferences(
                preferredLanguage = _language.value,
                targetScore = profile?.targetScore?.toInt() ?: 90,
                readinessScore = updatedReadiness,
                completedTodayMinutes = (profile?.completedTodayMinutes?.toInt() ?: 0) + (cur.elapsedSeconds / 60).coerceAtLeast(1)
            )
        }
    }

    fun exitExamSimulator() {
        examTimerJob?.cancel()
        _examState.value = ExamSessionUiState()
    }

    override fun onCleared() {
        super.onCleared()
        examTimerJob?.cancel()
        aiTeacher.releaseOnDeviceEngine()
    }
}
