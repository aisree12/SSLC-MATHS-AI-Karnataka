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
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
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

@Entity(tableName = "topic_verification")
data class TopicVerificationEntity(
    @PrimaryKey val topicNumber: String,
    val chapterNumber: Int,
    val completionStatus: String,
    val verifiedAtMillis: Long = System.currentTimeMillis()
)

@Entity(tableName = "concept_stats")
data class ConceptStatEntity(
    @PrimaryKey val conceptTag: String,
    val chapterNumber: Int,
    val attempts: Int,
    val errors: Int,
    val confidenceRating: Int = 3,
    val lastMistakeSummary: String = "",
    val updatedAtMillis: Long = System.currentTimeMillis()
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

    @Query("SELECT * FROM topic_verification ORDER BY chapterNumber ASC, topicNumber ASC")
    fun observeTopicVerifications(): Flow<List<TopicVerificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTopicVerification(entity: TopicVerificationEntity)

    @Query("SELECT * FROM concept_stats ORDER BY errors DESC, updatedAtMillis DESC")
    fun observeConceptStats(): Flow<List<ConceptStatEntity>>

    @Query("SELECT * FROM concept_stats WHERE conceptTag = :conceptTag LIMIT 1")
    suspend fun getConceptStat(conceptTag: String): ConceptStatEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertConceptStat(entity: ConceptStatEntity)
}

@Database(
    entities = [
        ChapterProgressEntity::class,
        ExamHistoryEntity::class,
        DailyGoalEntity::class,
        TopicVerificationEntity::class,
        ConceptStatEntity::class
    ],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun studyProgressDao(): StudyProgressDao

    companion object {
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `topic_verification` (`topicNumber` TEXT NOT NULL, `chapterNumber` INTEGER NOT NULL, `completionStatus` TEXT NOT NULL, `verifiedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`topicNumber`))"
                )
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `concept_stats` (`conceptTag` TEXT NOT NULL, `chapterNumber` INTEGER NOT NULL, `attempts` INTEGER NOT NULL, `errors` INTEGER NOT NULL, `confidenceRating` INTEGER NOT NULL, `lastMistakeSummary` TEXT NOT NULL, `updatedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`conceptTag`))"
                )
            }
        }

        val MIGRATION_2_3: Migration = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Preserve all original chapter-tagged student records before changing IDs.
                db.execSQL("CREATE TABLE IF NOT EXISTS legacy_chapter_progress_v2 AS SELECT * FROM chapter_progress")
                db.execSQL("CREATE TABLE IF NOT EXISTS legacy_daily_goals_v2 AS SELECT * FROM daily_goals")
                db.execSQL("CREATE TABLE IF NOT EXISTS legacy_topic_verification_v2 AS SELECT * FROM topic_verification")
                db.execSQL("CREATE TABLE IF NOT EXISTS legacy_concept_stats_v2 AS SELECT * FROM concept_stats")

                val mappedIds = "1,2,3,4,5,7,8,9,10,11,12,13,14,15"
                val mapping = "CASE chapterNumber " +
                    "WHEN 1 THEN 5 WHEN 2 THEN 6 WHEN 3 THEN 3 WHEN 4 THEN 10 " +
                    "WHEN 5 THEN 11 WHEN 7 THEN 7 WHEN 8 THEN 1 WHEN 9 THEN 2 " +
                    "WHEN 10 THEN 4 WHEN 11 THEN 8 WHEN 12 THEN 9 WHEN 13 THEN 13 " +
                    "WHEN 14 THEN 14 WHEN 15 THEN 12 ELSE chapterNumber END"
                db.execSQL("UPDATE chapter_progress SET chapterNumber = $mapping WHERE chapterNumber IN ($mappedIds)")
                db.execSQL("UPDATE daily_goals SET chapterNumber = $mapping WHERE chapterNumber IN ($mappedIds)")
                db.execSQL("UPDATE topic_verification SET chapterNumber = $mapping WHERE chapterNumber IN ($mappedIds)")
                val topicPrefixMap = "CASE " +
                    "WHEN topicNumber GLOB '1.*' THEN '5' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '2.*' THEN '6' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '3.*' THEN '3' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '4.*' THEN '10' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '5.*' THEN '11' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '7.*' THEN '7' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '8.*' THEN '1' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '9.*' THEN '2' || substr(topicNumber, 2) " +
                    "WHEN topicNumber GLOB '10.*' THEN '4' || substr(topicNumber, 3) " +
                    "WHEN topicNumber GLOB '11.*' THEN '8' || substr(topicNumber, 3) " +
                    "WHEN topicNumber GLOB '12.*' THEN '9' || substr(topicNumber, 3) " +
                    "WHEN topicNumber GLOB '13.*' THEN '13' || substr(topicNumber, 3) " +
                    "WHEN topicNumber GLOB '14.*' THEN '14' || substr(topicNumber, 3) " +
                    "WHEN topicNumber GLOB '15.*' THEN '12' || substr(topicNumber, 3) " +
                    "ELSE topicNumber END"
                db.execSQL("UPDATE topic_verification SET topicNumber = $topicPrefixMap WHERE topicNumber GLOB '1.*' OR topicNumber GLOB '2.*' OR topicNumber GLOB '3.*' OR topicNumber GLOB '4.*' OR topicNumber GLOB '5.*' OR topicNumber GLOB '7.*' OR topicNumber GLOB '8.*' OR topicNumber GLOB '9.*' OR topicNumber GLOB '10.*' OR topicNumber GLOB '11.*' OR topicNumber GLOB '12.*' OR topicNumber GLOB '13.*' OR topicNumber GLOB '14.*' OR topicNumber GLOB '15.*'")
                db.execSQL("UPDATE concept_stats SET chapterNumber = $mapping WHERE chapterNumber IN ($mappedIds)")

                // Legacy chapter 6 meant Constructions; official chapter 6 means Triangles.
                // Keep those old rows in the archive instead of attaching them to the wrong subject.
                db.execSQL("DELETE FROM chapter_progress WHERE chapterNumber = 6")
                db.execSQL("DELETE FROM daily_goals WHERE chapterNumber = 6")
                db.execSQL("DELETE FROM topic_verification WHERE chapterNumber = 6")
                db.execSQL("DELETE FROM concept_stats WHERE chapterNumber = 6")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sslc_maths_ksmke.db"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
                    .also { INSTANCE = it }
            }
        }
    }
}

class LocalProgressRepository(private val dao: StudyProgressDao) {
    val chapterProgress: Flow<List<ChapterProgressEntity>> = dao.observeChapterProgress()
    val examHistory: Flow<List<ExamHistoryEntity>> = dao.observeExamHistory()
    val dailyGoals: Flow<List<DailyGoalEntity>> = dao.observeDailyGoals()
    val topicVerifications: Flow<List<TopicVerificationEntity>> = dao.observeTopicVerifications()
    val conceptStats: Flow<List<ConceptStatEntity>> = dao.observeConceptStats()

    suspend fun saveChapterProgress(entity: ChapterProgressEntity) =
        dao.upsertChapterProgress(entity)

    suspend fun saveExamAttempt(entity: ExamHistoryEntity) =
        dao.insertExamHistory(entity)

    suspend fun saveGoal(entity: DailyGoalEntity) =
        dao.upsertDailyGoal(entity)

    suspend fun saveTopicVerification(
        topicNumber: String,
        chapterNumber: Int,
        completionStatus: String
    ) {
        dao.upsertTopicVerification(
            TopicVerificationEntity(
                topicNumber = topicNumber,
                chapterNumber = chapterNumber,
                completionStatus = completionStatus
            )
        )
    }

    suspend fun recordConceptAttempt(
        conceptTag: String,
        chapterNumber: Int,
        hadError: Boolean,
        mistakeSummary: String = ""
    ) {
        val cleanTag = conceptTag.trim().ifBlank { return }
        val existing = dao.getConceptStat(cleanTag)
        val nextAttempts = (existing?.attempts ?: 0) + 1
        val nextErrors = (existing?.errors ?: 0) + if (hadError) 1 else 0
        val nextRating = existing?.confidenceRating ?: 3
        val note = if (hadError && mistakeSummary.isNotBlank()) {
            mistakeSummary.take(180)
        } else {
            existing?.lastMistakeSummary.orEmpty()
        }
        dao.upsertConceptStat(
            ConceptStatEntity(
                conceptTag = cleanTag,
                chapterNumber = chapterNumber,
                attempts = nextAttempts,
                errors = nextErrors,
                confidenceRating = nextRating,
                lastMistakeSummary = note
            )
        )
    }

    suspend fun setConceptConfidence(
        conceptTag: String,
        chapterNumber: Int,
        rating: Int
    ) {
        val cleanTag = conceptTag.trim().ifBlank { return }
        val existing = dao.getConceptStat(cleanTag)
        dao.upsertConceptStat(
            ConceptStatEntity(
                conceptTag = cleanTag,
                chapterNumber = chapterNumber,
                attempts = existing?.attempts ?: 1,
                errors = existing?.errors ?: 0,
                confidenceRating = rating.coerceIn(1, 5),
                lastMistakeSummary = existing?.lastMistakeSummary.orEmpty()
            )
        )
    }

    suspend fun ensureDefaultGoalsIfEmpty(currentGoals: List<DailyGoalEntity>) {
        if (currentGoals.isNotEmpty()) return
        val defaults = listOf(
            DailyGoalEntity(
                goalId = "goal_ch10_quad",
                title = "Solve 3 Quadratic Formula problems with full 5-step working",
                chapterNumber = 4,
                targetMinutes = 25,
                completed = false,
                scheduledSlot = "Morning • High Focus"
            ),
            DailyGoalEntity(
                goalId = "goal_ch2_bpt",
                title = "Revise Thales (BPT) Theorem statement, figure & proof steps",
                chapterNumber = 6,
                targetMinutes = 20,
                completed = false,
                scheduledSlot = "Afternoon • Geometry"
            ),
            DailyGoalEntity(
                goalId = "goal_ch11_trig",
                title = "Practise 5 Trigonometry identity & value-table questions",
                chapterNumber = 8,
                targetMinutes = 20,
                completed = false,
                scheduledSlot = "Evening • Revision"
            )
        )
        dao.upsertAllDailyGoals(defaults)
    }
}
