package com.example.data.local

import android.content.Context
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "chapter_progress")
data class ChapterProgressEntity(
    @PrimaryKey val chapterNumber: Int,
    val masteryStatus: String,
    val accuracyPercent: Int,
    val questionsSolved: Int,
    val commonMistakeNote: String,
    val updatedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "exam_history")
data class ExamHistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val examTitle: String,
    val examMode: String,
    val scoreObtained: Double,
    val maxMarks: Int,
    val durationSeconds: Int,
    val questionsAttempted: Int,
    val questionsSkipped: Int,
    val slowestTopic: String,
    val feedbackSummary: String,
    val createdAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "daily_goals")
data class DailyGoalEntity(
    @PrimaryKey val goalId: String,
    val title: String,
    val chapterNumber: Int,
    val targetMinutes: Int,
    val completed: Boolean,
    val scheduledSlot: String
)

@Dao
interface StudyProgressDao {
    @Query("SELECT * FROM chapter_progress ORDER BY chapterNumber ASC")
    fun observeChapterProgress(): Flow<List<ChapterProgressEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertChapterProgress(entity: ChapterProgressEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllChapterProgress(entities: List<ChapterProgressEntity>)

    @Query("SELECT * FROM exam_history ORDER BY createdAtMillis DESC")
    fun observeExamHistory(): Flow<List<ExamHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertExamHistory(entity: ExamHistoryEntity)

    @Query("SELECT * FROM daily_goals ORDER BY chapterNumber ASC")
    fun observeDailyGoals(): Flow<List<DailyGoalEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertDailyGoal(entity: DailyGoalEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAllDailyGoals(entities: List<DailyGoalEntity>)
}

@Database(
    entities = [
        ChapterProgressEntity::class,
        ExamHistoryEntity::class,
        DailyGoalEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyProgressDao(): StudyProgressDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sslc_maths_ksmke.db"
                ).build().also { INSTANCE = it }
            }
        }
    }
}

class LocalProgressRepository(private val dao: StudyProgressDao) {
    val chapterProgress: Flow<List<ChapterProgressEntity>> = dao.observeChapterProgress()
    val examHistory: Flow<List<ExamHistoryEntity>> = dao.observeExamHistory()
    val dailyGoals: Flow<List<DailyGoalEntity>> = dao.observeDailyGoals()

    suspend fun saveChapterProgress(entity: ChapterProgressEntity) =
        dao.upsertChapterProgress(entity)

    suspend fun saveExamAttempt(entity: ExamHistoryEntity) =
        dao.insertExamHistory(entity)

    suspend fun saveGoal(entity: DailyGoalEntity) =
        dao.upsertDailyGoal(entity)

    suspend fun ensureDefaultGoalsIfEmpty(currentGoals: List<DailyGoalEntity>) {
        if (currentGoals.isNotEmpty()) return
        val defaults = listOf(
            DailyGoalEntity(
                goalId = "goal_ch10_quad",
                title = "Solve 3 Quadratic Formula problems with full 5-step working",
                chapterNumber = 10,
                targetMinutes = 25,
                completed = false,
                scheduledSlot = "Morning • High Focus"
            ),
            DailyGoalEntity(
                goalId = "goal_ch2_bpt",
                title = "Revise Thales (BPT) Theorem statement, figure & proof steps",
                chapterNumber = 2,
                targetMinutes = 20,
                completed = false,
                scheduledSlot = "Afternoon • Geometry"
            ),
            DailyGoalEntity(
                goalId = "goal_ch11_trig",
                title = "Practise 5 Trigonometry identity & value-table questions",
                chapterNumber = 11,
                targetMinutes = 20,
                completed = false,
                scheduledSlot = "Evening • Revision"
            )
        )
        dao.upsertAllDailyGoals(defaults)
    }
}
