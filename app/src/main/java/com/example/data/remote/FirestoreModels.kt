package com.example.data.remote

import androidx.annotation.Keep
import com.example.domain.MasteryStatus
import com.example.domain.TeachingLanguage
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.IgnoreExtraProperties
import com.google.firebase.firestore.ServerTimestamp

@Keep
@IgnoreExtraProperties
data class UserProfileDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val displayName: String = "SSLC Student",
    val preferredLanguage: String = TeachingLanguage.BILINGUAL.name,
    val targetScore: Long = 90L,
    val readinessScore: Long = 52L,
    val dailyGoalMinutes: Long = 45L,
    val completedTodayMinutes: Long = 0L,
    val streakDays: Long = 1L,
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun safeLanguage(): TeachingLanguage =
        enumValues<TeachingLanguage>().firstOrNull { it.name == preferredLanguage }
            ?: TeachingLanguage.BILINGUAL
}

@Keep
@IgnoreExtraProperties
data class ChapterMasteryDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val chapterNumber: Long = 1L,
    val masteryStatus: String = MasteryStatus.NOT_STARTED.name,
    val accuracyPercent: Long = 0L,
    val questionsSolved: Long = 0L,
    val commonMistakeNote: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
) {
    fun safeStatus(): MasteryStatus =
        enumValues<MasteryStatus>().firstOrNull { it.name == masteryStatus }
            ?: MasteryStatus.NOT_STARTED
}

@Keep
@IgnoreExtraProperties
data class ExamAttemptDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val examTitle: String = "",
    val examMode: String = "FULL_BOARD_80",
    val scoreObtained: Double = 0.0,
    val maxMarks: Long = 80L,
    val durationSeconds: Long = 0L,
    val questionsAttempted: Long = 0L,
    val questionsSkipped: Long = 0L,
    val slowestTopic: String = "",
    val feedbackSummary: String = "",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)

@Keep
@IgnoreExtraProperties
data class StudyGoalDoc(
    @DocumentId val id: String = "",
    val userId: String = "",
    val title: String = "",
    val chapterNumber: Long = 1L,
    val targetMinutes: Long = 20L,
    val completed: Boolean = false,
    val scheduledSlot: String = "Evening",
    @ServerTimestamp val createdAt: Timestamp? = null,
    @ServerTimestamp val updatedAt: Timestamp? = null
)
