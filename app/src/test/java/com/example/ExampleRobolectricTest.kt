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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [35])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("SSLC Maths AI", appName)
    }

    @Test
    fun `room migration 1 to 3 preserves student progress and maps official chapter IDs`() = runBlocking {
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
            .addMigrations(AppDatabase.MIGRATION_1_2, AppDatabase.MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()

        try {
            val dao = migratedDb.studyProgressDao()

            // Verify existing v1 records survived migration intact
            val chapters = dao.observeChapterProgress().first()
            assertEquals(1, chapters.size)
            assertEquals(4, chapters.first().chapterNumber)
            assertEquals(92, chapters.first().accuracyPercent)

            val exams = dao.observeExamHistory().first()
            assertEquals(1, exams.size)
            assertEquals(17.5, exams.first().scoreObtained, 1e-6)

            val goals = dao.observeDailyGoals().first()
            assertEquals(1, goals.size)
            assertEquals("g_v1_1", goals.first().goalId)
            assertEquals(6, goals.first().chapterNumber)
            assertTrue(goals.first().completed)

            // Verify new v2 tables accept writes & queries seamlessly
            dao.upsertTopicVerification(
                TopicVerificationEntity(
                    topicNumber = "6.1",
                    chapterNumber = 6,
                    completionStatus = "VERIFIED_COMPLETE"
                )
            )
            dao.upsertConceptStat(
                ConceptStatEntity(
                    conceptTag = "Quadratic Formula",
                    chapterNumber = 4,
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
    @Test
    fun `migration 2 to 3 maps legacy chapter IDs and archives constructions records`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val dbName = "migration_test_sslc_v2_v3.db"
        val dbFile = context.getDatabasePath(dbName)
        dbFile.parentFile?.mkdirs()
        if (dbFile.exists()) dbFile.delete()

        val v2 = SQLiteDatabase.openOrCreateDatabase(dbFile, null)
        v2.execSQL("CREATE TABLE chapter_progress (chapterNumber INTEGER NOT NULL PRIMARY KEY, masteryStatus TEXT NOT NULL, accuracyPercent INTEGER NOT NULL, questionsSolved INTEGER NOT NULL, commonMistakeNote TEXT NOT NULL, updatedAtMillis INTEGER NOT NULL)")
        v2.execSQL("CREATE TABLE exam_history (id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, examTitle TEXT NOT NULL, examMode TEXT NOT NULL, scoreObtained REAL NOT NULL, maxMarks INTEGER NOT NULL, durationSeconds INTEGER NOT NULL, questionsAttempted INTEGER NOT NULL, questionsSkipped INTEGER NOT NULL, slowestTopic TEXT NOT NULL, feedbackSummary TEXT NOT NULL, createdAtMillis INTEGER NOT NULL)")
        v2.execSQL("CREATE TABLE daily_goals (goalId TEXT NOT NULL PRIMARY KEY, title TEXT NOT NULL, chapterNumber INTEGER NOT NULL, targetMinutes INTEGER NOT NULL, completed INTEGER NOT NULL, scheduledSlot TEXT NOT NULL)")
        v2.execSQL("CREATE TABLE topic_verification (topicNumber TEXT NOT NULL PRIMARY KEY, chapterNumber INTEGER NOT NULL, completionStatus TEXT NOT NULL, verifiedAtMillis INTEGER NOT NULL)")
        v2.execSQL("CREATE TABLE concept_stats (conceptTag TEXT NOT NULL PRIMARY KEY, chapterNumber INTEGER NOT NULL, attempts INTEGER NOT NULL, errors INTEGER NOT NULL, confidenceRating INTEGER NOT NULL, lastMistakeSummary TEXT NOT NULL, updatedAtMillis INTEGER NOT NULL)")
        v2.execSQL("INSERT INTO chapter_progress VALUES (1, 'UNDERSTOOD', 92, 18, 'AP', 1000)")
        v2.execSQL("INSERT INTO chapter_progress VALUES (2, 'PRACTICING', 80, 10, 'Triangles', 1001)")
        v2.execSQL("INSERT INTO chapter_progress VALUES (6, 'UNDERSTOOD', 99, 8, 'Constructions', 1002)")
        v2.execSQL("INSERT INTO chapter_progress VALUES (10, 'NEEDS_FOUNDATION', 40, 4, 'Quadratics', 1003)")
        v2.execSQL("INSERT INTO exam_history (examTitle, examMode, scoreObtained, maxMarks, durationSeconds, questionsAttempted, questionsSkipped, slowestTopic, feedbackSummary, createdAtMillis) VALUES ('Legacy exam', 'CHAPTER_TEST_20', 15.0, 20, 900, 6, 1, 'AP', 'Saved', 1004)")
        v2.execSQL("INSERT INTO daily_goals VALUES ('g_triangle', 'Triangles', 2, 25, 1, 'Morning')")
        v2.execSQL("INSERT INTO daily_goals VALUES ('g_construction', 'Constructions', 6, 20, 0, 'Evening')")
        v2.execSQL("INSERT INTO topic_verification VALUES ('1.1', 1, 'DONE', 1000)")
        v2.execSQL("INSERT INTO topic_verification VALUES ('6.1', 6, 'DONE', 1001)")
        v2.execSQL("INSERT INTO topic_verification VALUES ('10.1', 10, 'DONE', 1002)")
        v2.execSQL("INSERT INTO concept_stats VALUES ('AP', 1, 5, 1, 4, 'Common difference', 1000)")
        v2.execSQL("INSERT INTO concept_stats VALUES ('Constructions', 6, 3, 0, 5, 'Legacy', 1001)")
        v2.execSQL("INSERT INTO concept_stats VALUES ('Quadratic roots', 10, 4, 2, 2, 'Sign', 1002)")
        v2.version = 2
        v2.close()

        val migrated = Room.databaseBuilder(context, AppDatabase::class.java, dbName)
            .addMigrations(AppDatabase.MIGRATION_2_3)
            .allowMainThreadQueries()
            .build()
        try {
            val dao = migrated.studyProgressDao()
            val progress = dao.observeChapterProgress().first().associateBy { it.chapterNumber }
            assertEquals(setOf(4, 5, 6), progress.keys)
            assertEquals(92, progress[5]?.accuracyPercent)
            assertEquals(80, progress[6]?.accuracyPercent)
            assertEquals(40, progress[4]?.accuracyPercent)

            val goals = dao.observeDailyGoals().first()
            assertEquals(1, goals.size)
            assertEquals(6, goals.first().chapterNumber)
            assertEquals(1, dao.observeExamHistory().first().size)

            val topics = dao.observeTopicVerifications().first().associateBy { it.topicNumber }
            assertTrue(topics.containsKey("5.1"))
            assertTrue(topics.containsKey("4.1"))
            assertEquals(setOf("4.1", "5.1"), topics.keys)

            val concepts = dao.observeConceptStats().first().associateBy { it.conceptTag }
            assertEquals(5, concepts["AP"]?.chapterNumber)
            assertEquals(4, concepts["Quadratic roots"]?.chapterNumber)
            assertFalse(concepts.containsKey("Constructions"))

            val archive = migrated.openHelper.readableDatabase.query("SELECT COUNT(*) FROM legacy_chapter_progress_v2")
            archive.use {
                assertTrue(it.moveToFirst())
                assertEquals(4, it.getInt(0))
            }
            val archivedGoals = migrated.openHelper.readableDatabase.query("SELECT COUNT(*) FROM legacy_daily_goals_v2")
            archivedGoals.use {
                assertTrue(it.moveToFirst())
                assertEquals(2, it.getInt(0))
            }
        } finally {
            migrated.close()
            dbFile.delete()
        }
    }

}
