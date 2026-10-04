package com.example

import android.content.Context
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory
import androidx.sqlite.db.SupportSQLiteOpenHelper
import androidx.test.core.app.ApplicationProvider
import com.example.data.MIGRATION_1_2
import com.example.data.MIGRATION_2_3
import com.example.data.MIGRATION_3_4
import com.example.data.MIGRATION_4_5
import com.example.model.CandidateProfile
import com.example.model.ChatMessage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
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
    assertEquals("سوا سوا", appName)
  }

  @Test
  fun `verify Room database initialization and integrity`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = com.example.data.AppDatabase.getDatabase(context)
    val candidateDao = db.candidateDao()
    val chatDao = db.chatDao()

    val count = candidateDao.countCandidates()
    assertTrue(count >= 0)

    val testMsg = ChatMessage(
      candidateId = "test_user_c1",
      text = "السلام عليكم ورحمة الله",
      isFromUser = true,
      timestamp = System.currentTimeMillis()
    )
    val msgId = chatDao.insertMessage(testMsg)
    assertTrue(msgId > 0)

    val msgs = chatDao.getMessagesForCandidate("test_user_c1").first()
    assertTrue(msgs.any { it.text == "السلام عليكم ورحمة الله" })
  }

  @Test
  fun `test candidate profile insertion and all migration fields preservation`() = runBlocking {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val db = com.example.data.AppDatabase.getDatabase(context)
    val candidateDao = db.candidateDao()

    val testCandidate = CandidateProfile(
      id = "test_candidate_production_gate",
      name = "خديجة",
      nameEn = "Khadija",
      age = 25,
      city = "الرياض",
      cityEn = "Riyadh",
      distanceKm = 12,
      profession = "مهندسة برمجيات",
      professionEn = "Software Engineer",
      ethnicity = "عربية",
      ethnicityEn = "Arab",
      religiousPractice = "ملتزمة",
      religiousPracticeEn = "Practicing",
      islamicDress = "حجاب شرعي كامل",
      islamicDressEn = "Modest Hijab",
      prayersHabit = "أصلي دائماً في وقتها",
      prayersHabitEn = "Always pray on time",
      halalFood = "دائماً أتحرى الحلال",
      maritalStatus = "عزباء",
      maritalStatusEn = "Single",
      willingToRelocate = true,
      hasChildren = false,
      heightCm = 165,
      education = "ماجستير حاسبات",
      bio = "مهندسة برمجيات تسعى لبناء بيت مسلم على هدي السنة النبوية",
      bioEn = "Software engineer seeking halal marriage",
      marriageGoal = "بناء أسرة صالحة على تقوى من الله",
      marriageGoalEn = "Build pious Muslim family",
      icebreakerQuestion = "ما هو هدفك من الزواج؟",
      icebreakerAnswer = "المودة والرحمة والاستقرار",
      photoRes = com.example.R.drawable.profile_sarah,
      hiddenConditions = "سكن مستقل وإكمال الدراسات العليا",
      hiddenConditionsAccessGranted = true,
      compatibilityScore = 96,
      referralCode = "SAWA_KHADIJA",
      referralCount = 3
    )

    candidateDao.insertAll(listOf(testCandidate))

    val fetched = candidateDao.getCandidateById("test_candidate_production_gate")
    assertNotNull(fetched)
    assertEquals("خديجة", fetched?.name)
    assertEquals("سكن مستقل وإكمال الدراسات العليا", fetched?.hiddenConditions)
    assertEquals(true, fetched?.hiddenConditionsAccessGranted)
    assertEquals(96, fetched?.compatibilityScore)
    assertEquals("SAWA_KHADIJA", fetched?.referralCode)
    assertEquals(3, fetched?.referralCount)
  }

  @Test
  fun `verify migration chain from 1 through 5 with SQLite directly`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val config = SupportSQLiteOpenHelper.Configuration.builder(context)
      .name(null) // in-memory
      .callback(object : SupportSQLiteOpenHelper.Callback(1) {
        override fun onCreate(db: SupportSQLiteDatabase) {
          // Version 1 schema: basic candidates table
          db.execSQL("""
            CREATE TABLE IF NOT EXISTS `candidates` (
              `id` TEXT NOT NULL PRIMARY KEY,
              `name` TEXT NOT NULL,
              `nameEn` TEXT NOT NULL,
              `age` INTEGER NOT NULL,
              `city` TEXT NOT NULL,
              `cityEn` TEXT NOT NULL,
              `distanceKm` INTEGER NOT NULL,
              `profession` TEXT NOT NULL,
              `professionEn` TEXT NOT NULL,
              `ethnicity` TEXT NOT NULL,
              `ethnicityEn` TEXT NOT NULL,
              `religiousPractice` TEXT NOT NULL,
              `religiousPracticeEn` TEXT NOT NULL,
              `islamicDress` TEXT NOT NULL,
              `islamicDressEn` TEXT NOT NULL,
              `prayersHabit` TEXT NOT NULL,
              `prayersHabitEn` TEXT NOT NULL,
              `halalFood` TEXT NOT NULL,
              `maritalStatus` TEXT NOT NULL,
              `maritalStatusEn` TEXT NOT NULL,
              `willingToRelocate` INTEGER NOT NULL,
              `hasChildren` INTEGER NOT NULL,
              `heightCm` INTEGER NOT NULL,
              `education` TEXT NOT NULL,
              `bio` TEXT NOT NULL,
              `bioEn` TEXT NOT NULL,
              `marriageGoal` TEXT NOT NULL,
              `marriageGoalEn` TEXT NOT NULL,
              `icebreakerQuestion` TEXT NOT NULL,
              `icebreakerAnswer` TEXT NOT NULL,
              `photoRes` INTEGER NOT NULL,
              `isLiked` INTEGER NOT NULL DEFAULT 0,
              `isPassed` INTEGER NOT NULL DEFAULT 0,
              `isMatched` INTEGER NOT NULL DEFAULT 0,
              `isJustJoined` INTEGER NOT NULL DEFAULT 0,
              `isLikedYou` INTEGER NOT NULL DEFAULT 0,
              `isVerified` INTEGER NOT NULL DEFAULT 1,
              `isPhotoBlurred` INTEGER NOT NULL DEFAULT 0,
              `isGoldMember` INTEGER NOT NULL DEFAULT 0,
              `audioDurationSec` INTEGER NOT NULL DEFAULT 30,
              `languages` TEXT NOT NULL DEFAULT 'العربية',
              `photoRevealRequested` INTEGER NOT NULL DEFAULT 0
            )
          """.trimIndent())
          // Seed data at v1
          db.execSQL("INSERT INTO candidates (id, name, nameEn, age, city, cityEn, distanceKm, profession, professionEn, ethnicity, ethnicityEn, religiousPractice, religiousPracticeEn, islamicDress, islamicDressEn, prayersHabit, prayersHabitEn, halalFood, maritalStatus, maritalStatusEn, willingToRelocate, hasChildren, heightCm, education, bio, bioEn, marriageGoal, marriageGoalEn, icebreakerQuestion, icebreakerAnswer, photoRes) VALUES ('v1_user', 'علي', 'Ali', 28, 'القاهرة', 'Cairo', 5, 'طبيب', 'Doctor', 'عربي', 'Arab', 'ملتزم', 'Practicing', 'ثوب', 'Modest', 'دائما', 'Always', 'حلال', 'أعزب', 'Single', 0, 0, 178, 'جامعي', 'طبيب', 'Doctor', 'عفاف', 'Marriage', 'سؤال', 'جواب', 0)")
        }

        override fun onUpgrade(db: SupportSQLiteDatabase, oldVersion: Int, newVersion: Int) {}
      })
      .build()

    val openHelper = FrameworkSQLiteOpenHelperFactory().create(config)
    val db = openHelper.writableDatabase

    // Execute migration 1 -> 2
    MIGRATION_1_2.migrate(db)
    // Execute migration 2 -> 3
    MIGRATION_2_3.migrate(db)
    // Execute migration 3 -> 4
    MIGRATION_3_4.migrate(db)
    // Execute migration 4 -> 5
    MIGRATION_4_5.migrate(db)

    // Verify v1 user data preserved
    val cursor = db.query("SELECT id, name, compatibilityScore, referralCode FROM candidates WHERE id = 'v1_user'")
    assertTrue(cursor.moveToFirst())
    assertEquals("v1_user", cursor.getString(0))
    assertEquals("علي", cursor.getString(1))
    assertEquals(90, cursor.getInt(2)) // default from migration
    assertEquals("SAWA100", cursor.getString(3)) // default from migration
    cursor.close()

    // Verify chat_messages table exists and index exists
    db.execSQL("INSERT INTO chat_messages (candidateId, text, isFromUser, timestamp, isChaperoneMonitored, isAudioVoiceNote, audioDuration) VALUES ('v1_user', 'مرحباً بك', 1, 1000, 1, 0, '00:10')")
    val chatCursor = db.query("SELECT text FROM chat_messages WHERE candidateId = 'v1_user'")
    assertTrue(chatCursor.moveToFirst())
    assertEquals("مرحباً بك", chatCursor.getString(0))
    chatCursor.close()

    db.close()
  }
}
