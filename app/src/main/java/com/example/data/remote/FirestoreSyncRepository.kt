package com.example.data.remote

import android.content.Context
import com.example.R
import com.example.core.OperationType
import com.example.core.handleFirestoreError
import com.example.domain.MasteryStatus
import com.example.domain.TeachingLanguage
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.snapshots
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class FirestoreSyncRepository(
    private val db: FirebaseFirestore,
    private val auth: FirebaseAuth = Firebase.auth
) {
    constructor(context: Context) : this(
        FirebaseFirestore.getInstance(
            context.applicationContext.getString(R.string.firestore_database_id)
        )
    )

    private fun requireUserId(): String {
        return auth.currentUser?.uid
            ?: throw IllegalStateException("User must be signed in with Google before accessing Firestore.")
    }

    fun observeProfile(userId: String): Flow<UserProfileDoc?> = flow {
        val uid = requireUserId()
        check(uid == userId) { "User mismatch" }
        val docRef = db.collection("users").document(uid)
        emitAll(
            docRef.snapshots()
                .map { snapshot ->
                    if (snapshot.exists()) {
                        snapshot.toObject(
                            UserProfileDoc::class.java,
                            DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                        )
                    } else null
                }
                .catch { error ->
                    logStreamError(error, OperationType.GET, docRef.path, uid)
                    throw error
                }
        )
    }

    fun observeChapterMastery(userId: String): Flow<List<ChapterMasteryDoc>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "User mismatch" }
        val colRef = db.collection("users").document(uid).collection("chapterMastery")
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ChapterMasteryDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamError(error, OperationType.LIST, colRef.path, uid)
                    throw error
                }
        )
    }

    fun observeExamAttempts(userId: String): Flow<List<ExamAttemptDoc>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "User mismatch" }
        val colRef = db.collection("users").document(uid).collection("examAttempts")
        emitAll(
            colRef.orderBy("createdAt", Query.Direction.DESCENDING)
                .snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        ExamAttemptDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamError(error, OperationType.LIST, colRef.path, uid)
                    throw error
                }
        )
    }

    fun observeStudyGoals(userId: String): Flow<List<StudyGoalDoc>> = flow {
        val uid = requireUserId()
        check(uid == userId) { "User mismatch" }
        val colRef = db.collection("users").document(uid).collection("studyGoals")
        emitAll(
            colRef.snapshots()
                .map { snapshot ->
                    snapshot.toObjects(
                        StudyGoalDoc::class.java,
                        DocumentSnapshot.ServerTimestampBehavior.ESTIMATE
                    )
                }
                .catch { error ->
                    logStreamError(error, OperationType.LIST, colRef.path, uid)
                    throw error
                }
        )
    }

    suspend fun ensureUserProfileExists(displayName: String): Result<Unit> {
        val path = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val safeName = displayName.trim().ifBlank { "SSLC Student" }.take(80)
            val docRef = db.collection("users").document(uid)
            val existing = docRef.get().await()
            if (!existing.exists()) {
                val payload = mapOf(
                    "userId" to uid,
                    "displayName" to safeName,
                    "preferredLanguage" to TeachingLanguage.BILINGUAL.name,
                    "targetScore" to 90L,
                    "readinessScore" to 52L,
                    "dailyGoalMinutes" to 45L,
                    "completedTodayMinutes" to 0L,
                    "streakDays" to 1L,
                    "createdAt" to FieldValue.serverTimestamp(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
                docRef.set(payload).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun updateProfilePreferences(
        preferredLanguage: TeachingLanguage,
        targetScore: Int,
        readinessScore: Int,
        completedTodayMinutes: Int
    ): Result<Unit> {
        val path = "users/${auth.currentUser?.uid ?: "unauthenticated"}"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
            docRef.update(
                mapOf(
                    "preferredLanguage" to preferredLanguage.name,
                    "targetScore" to targetScore.coerceIn(35, 100).toLong(),
                    "readinessScore" to readinessScore.coerceIn(0, 100).toLong(),
                    "completedTodayMinutes" to completedTodayMinutes.coerceIn(0, 600).toLong(),
                    "updatedAt" to FieldValue.serverTimestamp()
                )
            ).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.UPDATE, path)
            Result.failure(e)
        }
    }

    suspend fun saveChapterMastery(
        chapterNumber: Int,
        status: MasteryStatus,
        accuracyPercent: Int,
        questionsSolved: Int,
        commonMistakeNote: String
    ): Result<Unit> {
        val docId = "ch_$chapterNumber"
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/chapterMastery/$docId"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
                .collection("chapterMastery").document(docId)
            path = docRef.path
            val snapshot = docRef.get().await()
            val safeNote = commonMistakeNote.trim().take(500)
            if (snapshot.exists()) {
                docRef.update(
                    mapOf(
                        "masteryStatus" to status.name,
                        "accuracyPercent" to accuracyPercent.coerceIn(0, 100).toLong(),
                        "questionsSolved" to questionsSolved.coerceIn(0, 10000).toLong(),
                        "commonMistakeNote" to safeNote,
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "chapterNumber" to chapterNumber.coerceIn(1, 15).toLong(),
                        "masteryStatus" to status.name,
                        "accuracyPercent" to accuracyPercent.coerceIn(0, 100).toLong(),
                        "questionsSolved" to questionsSolved.coerceIn(0, 10000).toLong(),
                        "commonMistakeNote" to safeNote,
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    suspend fun recordExamAttempt(
        examTitle: String,
        examMode: String,
        scoreObtained: Double,
        maxMarks: Int,
        durationSeconds: Int,
        questionsAttempted: Int,
        questionsSkipped: Int,
        slowestTopic: String,
        feedbackSummary: String
    ): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/examAttempts"
        return try {
            val uid = requireUserId()
            val docRef = db.collection("users").document(uid)
                .collection("examAttempts").document()
            path = docRef.path
            val payload = mapOf(
                "userId" to uid,
                "examTitle" to examTitle.trim().ifBlank { "SSLC Mock Test" }.take(120),
                "examMode" to examMode,
                "scoreObtained" to scoreObtained.coerceIn(0.0, 80.0),
                "maxMarks" to maxMarks.coerceIn(1, 80).toLong(),
                "durationSeconds" to durationSeconds.coerceIn(0, 18000).toLong(),
                "questionsAttempted" to questionsAttempted.coerceIn(0, 38).toLong(),
                "questionsSkipped" to questionsSkipped.coerceIn(0, 38).toLong(),
                "slowestTopic" to slowestTopic.trim().take(120),
                "feedbackSummary" to feedbackSummary.trim().take(1000),
                "createdAt" to FieldValue.serverTimestamp(),
                "updatedAt" to FieldValue.serverTimestamp()
            )
            docRef.set(payload).await()
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.CREATE, path)
            Result.failure(e)
        }
    }

    suspend fun upsertStudyGoal(
        goalId: String,
        title: String,
        chapterNumber: Int,
        targetMinutes: Int,
        completed: Boolean,
        scheduledSlot: String
    ): Result<Unit> {
        var path = "users/${auth.currentUser?.uid ?: "unauthenticated"}/studyGoals/$goalId"
        return try {
            val uid = requireUserId()
            val safeId = goalId.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(128).ifBlank { "goal_1" }
            val docRef = db.collection("users").document(uid)
                .collection("studyGoals").document(safeId)
            path = docRef.path
            val snapshot = docRef.get().await()
            if (snapshot.exists()) {
                docRef.update(
                    mapOf(
                        "title" to title.trim().ifBlank { "Practice Goal" }.take(120),
                        "chapterNumber" to chapterNumber.coerceIn(1, 15).toLong(),
                        "targetMinutes" to targetMinutes.coerceIn(5, 180).toLong(),
                        "completed" to completed,
                        "scheduledSlot" to scheduledSlot.trim().ifBlank { "Evening" }.take(40),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            } else {
                docRef.set(
                    mapOf(
                        "userId" to uid,
                        "title" to title.trim().ifBlank { "Practice Goal" }.take(120),
                        "chapterNumber" to chapterNumber.coerceIn(1, 15).toLong(),
                        "targetMinutes" to targetMinutes.coerceIn(5, 180).toLong(),
                        "completed" to completed,
                        "scheduledSlot" to scheduledSlot.trim().ifBlank { "Evening" }.take(40),
                        "createdAt" to FieldValue.serverTimestamp(),
                        "updatedAt" to FieldValue.serverTimestamp()
                    )
                ).await()
            }
            Result.success(Unit)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            handleFirestoreError(e, OperationType.WRITE, path)
            Result.failure(e)
        }
    }

    private fun logStreamError(error: Throwable, op: OperationType, path: String, uid: String) {
        val firestoreError = generateSequence(error) { it.cause }
            .filterIsInstance<FirebaseFirestoreException>()
            .firstOrNull()
        if (firestoreError == null && error is CancellationException) return
        val toLog = firestoreError ?: (error as? Exception)
        if (toLog != null && auth.currentUser?.uid == uid) {
            handleFirestoreError(toLog, op, path)
        }
    }
}
