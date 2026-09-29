package com.example.model

import androidx.annotation.DrawableRes
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "candidates")
data class CandidateProfile(
    @PrimaryKey val id: String,
    val name: String,
    val nameEn: String,
    val age: Int,
    val city: String,
    val cityEn: String,
    val distanceKm: Int,
    val profession: String,
    val professionEn: String,
    val ethnicity: String,
    val ethnicityEn: String,
    val religiousPractice: String,
    val religiousPracticeEn: String,
    val islamicDress: String,
    val islamicDressEn: String,
    val prayersHabit: String,
    val prayersHabitEn: String,
    val halalFood: String,
    val maritalStatus: String,
    val maritalStatusEn: String,
    val willingToRelocate: Boolean,
    val hasChildren: Boolean,
    val heightCm: Int,
    val education: String,
    val bio: String,
    val bioEn: String,
    val marriageGoal: String,
    val marriageGoalEn: String,
    val icebreakerQuestion: String,
    val icebreakerAnswer: String,
    @DrawableRes val photoRes: Int,
    val isLiked: Boolean = false,
    val isPassed: Boolean = false,
    val isMatched: Boolean = false,
    val isJustJoined: Boolean = false,
    val isLikedYou: Boolean = false,
    val isVerified: Boolean = true,
    val isPhotoBlurred: Boolean = false,
    val isGoldMember: Boolean = true,
    val audioDurationSec: Int = 37,
    val languages: String = "العربية, الإنجليزية, الفرنسية",
    val photoRevealRequested: Boolean = false,
    val gender: String = "MALE", // "MALE" or "FEMALE"
    val isBanned: Boolean = false,
    val latitude: Double = 30.0444, // Default Cairo/Egypt or specific coordinates
    val longitude: Double = 31.2357,
    val activeHours: String = "مساءً (من 7:00 م إلى 11:00 م)",
    val isOnlineNow: Boolean = true
)

@Entity(tableName = "chat_messages")
data class ChatMessage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val candidateId: String,
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val isChaperoneMonitored: Boolean = true,
    val isAudioVoiceNote: Boolean = false,
    val audioDuration: String = "00:37",
    val imageUrl: String? = null
)

data class FilterPreferences(
    val minAge: Int = 20,
    val maxAge: Int = 35,
    val maxDistanceKm: Int = 50,
    val ethnicity: String = "الكل",
    val religiousPractice: String = "الكل",
    val islamicDress: String = "الكل",
    val onlyVerified: Boolean = false,
    val showOtherProfiles: Boolean = true
)

data class CurrentUserProfile(
    val name: String = "سارة",
    val age: Int = 24,
    val city: String = "القاهرة",
    val profession: String = "طبيبة أطفال",
    val gender: String = "FEMALE",
    val bio: String = "طبيبة أطفال، أبحث عن شريك حياة يخاف الله، طموح، يقدر الأسرة والتفاهم والود. أحب القراءة وزيارة الأماكن التراثية.",
    val marriageGoal: String = "بناء بيت إسلامي سعيد قائم على المودة والرحمة وتربية أطفال صالحين.",
    val religiousPractice: String = "ملتزمة بالفرائض",
    val islamicDress: String = "حجاب محتشم",
    val prayersHabit: String = "أصلي الصلوات الخمس في وقتها",
    val halalFood: String = "حلال دائماً",
    val education: String = "ماجستير طب الأطفال",
    val heightCm: Int = 166,
    val chaperoneName: String = "أبو سارة (الولي)",
    val chaperoneEmail: String = "wali.guardian@example.com",
    val chaperonePhone: String = "+20 10 1234 5678",
    val isChaperoneActive: Boolean = true,
    val isPhotoBlurred: Boolean = false,
    val completionPercentage: Int = 96,
    val isVerified: Boolean = true,
    val isGoldMember: Boolean = true,
    val subscriptionPlan: String = "GOLD_MONTHLY",
    val subscriptionPrice: String = "199.99 ج.م / شهرياً",
    val subscriptionExpiresAt: String = "30 أكتوبر 2026",
    val boostsRemaining: Int = 3,
    val instantChatsRemaining: Int = 12,
    val activeHours: String = "مساءً (من 7:00 م إلى 11:00 م)",
    val isOnlineNow: Boolean = true,
    val latitude: Double = 30.0444, // Cairo coordinates
    val longitude: Double = 31.2357,
    val locationCity: String = "القاهرة، مصر",
    val isGpsEnabled: Boolean = false,
    val preferredLanguage: AppLanguage = AppLanguage.ARABIC,
    val phoneNumber: String = "+20 10 1234 5678",
    val email: String = "sara.matrimony@example.com",
    val isRegisteredWithFirebase: Boolean = false,
    val firebaseUid: String? = null
)

data class SubscriptionPlan(
    val id: String,
    val title: String,
    val titleEn: String,
    val period: String,
    val periodEn: String,
    val price: String,
    val originalPrice: String? = null,
    val discountPercent: String? = null,
    val isPopular: Boolean = false,
    val badge: String? = null
)

data class AdminReport(
    val id: String,
    val reporterName: String,
    val reportedUserId: String,
    val reportedUserName: String,
    val reason: String,
    val timestamp: String,
    val status: String = "PENDING" // "PENDING", "RESOLVED", "DISMISSED"
)

data class AdminBroadcast(
    val id: String,
    val title: String,
    val message: String,
    val sentAt: String,
    val audience: String
)

enum class AppLanguage {
    ARABIC,
    ENGLISH
}

enum class MainNavigationTab {
    DISCOVER,
    EXPLORE,
    CHATS,
    PROFILE
}
