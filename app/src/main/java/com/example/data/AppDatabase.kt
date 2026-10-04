package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.model.CandidateProfile
import com.example.model.ChatMessage
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `chat_messages` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `candidateId` TEXT NOT NULL, `text` TEXT NOT NULL, `isFromUser` INTEGER NOT NULL, `timestamp` INTEGER NOT NULL, `isChaperoneMonitored` INTEGER NOT NULL, `isAudioVoiceNote` INTEGER NOT NULL, `audioDuration` TEXT NOT NULL, `imageUrl` TEXT)")
    }
}

val MIGRATION_2_3 = object : Migration(2, 3) {
    override fun migrate(db: SupportSQLiteDatabase) {
        val columns = listOf(
            "gender TEXT NOT NULL DEFAULT 'MALE'",
            "isBanned INTEGER NOT NULL DEFAULT 0",
            "latitude REAL NOT NULL DEFAULT 30.0444",
            "longitude REAL NOT NULL DEFAULT 31.2357",
            "activeHours TEXT NOT NULL DEFAULT 'مساءً'",
            "isOnlineNow INTEGER NOT NULL DEFAULT 1",
            "isVip INTEGER NOT NULL DEFAULT 1",
            "rosesReceivedCount INTEGER NOT NULL DEFAULT 0",
            "country TEXT NOT NULL DEFAULT 'مصر'",
            "area TEXT NOT NULL DEFAULT 'المعادي'",
            "madhab TEXT NOT NULL DEFAULT 'شافعي / عام'",
            "nationality TEXT NOT NULL DEFAULT 'مصرية'",
            "origin TEXT NOT NULL DEFAULT 'مصر'",
            "childrenCount INTEGER NOT NULL DEFAULT 0",
            "desireForChildren TEXT NOT NULL DEFAULT 'نعم، أرغب في أطفال'",
            "previousMarriage TEXT NOT NULL DEFAULT 'لم يسبق الزواج'",
            "smoking TEXT NOT NULL DEFAULT 'غير مدخن'",
            "lifestyle TEXT NOT NULL DEFAULT 'صحي ومتزن'",
            "incomeRange TEXT NOT NULL DEFAULT 'متوسط إلى مرتفع'",
            "interests TEXT NOT NULL DEFAULT 'القراءة، السفر'",
            "hobbies TEXT NOT NULL DEFAULT 'القراءة'",
            "partnerPreferences TEXT NOT NULL DEFAULT 'شخص ذو خلق ودين'",
            "religiousPreferences TEXT NOT NULL DEFAULT 'المحافظة على الصلاة'",
            "familyPreferences TEXT NOT NULL DEFAULT 'تقدير الترابط الأسري'"
        )
        for (col in columns) {
            try {
                db.execSQL("ALTER TABLE candidates ADD COLUMN $col")
            } catch (_: Exception) {}
        }
    }
}

val MIGRATION_3_4 = object : Migration(3, 4) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN hiddenConditions TEXT DEFAULT NULL")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN hiddenConditionsAccessGranted INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN compatibilityScore INTEGER NOT NULL DEFAULT 90")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN referralCode TEXT NOT NULL DEFAULT 'SAWA100'")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN referralCount INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
    }
}

val MIGRATION_4_5 = object : Migration(4, 5) {
    override fun migrate(db: SupportSQLiteDatabase) {
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN hiddenConditions TEXT DEFAULT NULL")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN hiddenConditionsAccessGranted INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN compatibilityScore INTEGER NOT NULL DEFAULT 90")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN referralCode TEXT NOT NULL DEFAULT 'SAWA100'")
        } catch (_: Exception) {}
        try {
            db.execSQL("ALTER TABLE candidates ADD COLUMN referralCount INTEGER NOT NULL DEFAULT 0")
        } catch (_: Exception) {}
        try {
            db.execSQL("CREATE INDEX IF NOT EXISTS index_chat_messages_candidateId ON chat_messages(candidateId)")
        } catch (_: Exception) {}
    }
}

@Database(entities = [CandidateProfile::class, ChatMessage::class], version = 5, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun candidateDao(): CandidateDao
    abstract fun chatDao(): ChatDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "sawa_sawa_database"
                )
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3, MIGRATION_3_4, MIGRATION_4_5)
                    .fallbackToDestructiveMigrationOnDowngrade()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
