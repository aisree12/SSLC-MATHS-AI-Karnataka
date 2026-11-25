package com.example

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.ConceptStatEntity
import com.example.data.local.TopicVerificationEntity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SSLC Maths AI", appName)
    }

    @Test
    fun `room migration 1 to 2 preserves existing student progress and creates new tables`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration_test_sslc_v1_v2.db"
        val dbFile = context.getDatabasePath(dbName)
        dbFile.parentFile?.mkdirs()
        if (dbFile.exists()) dbFile.delete()

        // 1. Create a real v1 SQLite database and populate student records
        val v1Db = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        v1Db.execSQL(
            "CREATE TABLE IF NOT EXISTS `chapter_progress` (`chapterNumber` INTEGER NOT NULL, `masteryStatus` TEXT NOT NULL, `accuracyPercent` INTEGER NOT NULL, `questionsSolved` INTEGER NOT NULL, `commonMistakeNote` TEXT NOT NULL, `updatedAtMillis` INTEGER NOT NULL, PRIMARY KEY(`chapterNumber`))"
        )
        v1Db.execSQL(
            "CREATE TABLE IF NOT EXISTS `exam_history` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `examTitle` TEXT NOT NULL, `examMode` TEXT NOT NULL, `scoreObtained` REAL NOT NULL, `maxMarks` INTEGER NOT NULL, `durationSeconds` INTEGER NOT NULL, `questionsAttempted` INTEGER NOT NULL, `questionsSkipped` INTEGER NOT NULL, `slowestTopic` TEXT NOT NULL, `feedbackSummary` TEXT NOT NULL, `createdAtMillis` INTEGER NOT NULL)"
        )
        v1Db.execSQL(
            "CREATE TABLE IF NOT EXISTS `daily_goals` (`goalId` TEXT NOT NULL, `title` TEXT NOT NULL, `chapterNumber` INTEGER NOT NULL, `targetMinutes` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `scheduledSlot` TEXT NOT NULL, PRIMARY KEY(`goalId`))"
        )
        v1Db.execSQL(
            "INSERT INTO chapter_progress VALUES (10, 'UNDERSTOOD', 92, 18, 'Check discriminant sign', 1700000000000)"
        )
        v1Db.execSQL(
            "INSERT INTO exam_history (examTitle, examMode, scoreObtained, maxMarks, durationSeconds, questionsAttempted, questionsSkipped, slowestTopic, feedbackSummary, createdAtMillis) VALUES ('SSLC Mock 1', 'CHAPTER_TEST_20', 17.5, 20, 1800, 8, 0, 'Quadratic Formula', 'Great pacing', 1700000001000)"
        )
        v1Db.execSQL(
            "INSERT INTO daily_goals VALUES ('g_v1_1', 'Revise BPT Theorem', 2, 25, 1, 'Morning')"
        )
        v1Db.version = 1
        v1Db.close()

        // 2. Open with Room v2 using AppDatabase.MIGRATION_1_2
        val migratedDb = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(AppDatabase.MIGRATION_1_2)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = migratedDb.studyProgressDao()

            // Verify existing v1 records survived migration intact
            val chapters = dao.observeChapterProgress().first()
            assertEquals(1, chapters.size)
            assertEquals(10, chapters.first().chapterNumber)
            assertEquals(92, chapters.first().accuracyPercent)

            val exams = dao.observeExamHistory().first()
            assertEquals(1, exams.size)
            assertEquals(17.5, exams.first().scoreObtained, 1e-6)

            val goals = dao.observeDailyGoals().first()
            assertEquals(1, goals.size)
            assertEquals("g_v1_1", goals.first().goalId)
            assertTrue(goals.first().completed)

            // Verify new v2 tables accept writes & queries seamlessly
            dao.upsertTopicVerification(
                TopicVerificationEntity(
                    topicNumber = "2.1",
                    chapterNumber = 2,
                    completionStatus = "VERIFIED_COMPLETE"
                )
            )
            dao.upsertConceptStat(
                ConceptStatEntity(
                    conceptTag = "Quadratic Formula",
                    chapterNumber = 10,
                    attempts = 5,
                    errors = 1,
                    confidenceRating = 4,
                    lastMistakeSummary = "Sign check"
                )
            )

            assertEquals(1, dao.observeTopicVerifications().first().size)
            assertEquals(1, dao.observeConceptStats().first().size)
        } finally {
            migratedDb.close()
            dbFile.delete()
        }
    }
}
