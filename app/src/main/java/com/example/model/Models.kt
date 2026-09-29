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
    val photoRevealRequested: Boolean = false
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
    val city: String = "الرياض",
    val profession: String = "طبيبة أطفال",
    val chaperoneName: String = "أبو سارة (الولي)",
    val chaperoneEmail: String = "wali.guardian@example.com",
    val isChaperoneActive: Boolean = true,
    val isPhotoBlurred: Boolean = false,
    val completionPercentage: Int = 94,
    val isVerified: Boolean = true,
    val isGoldMember: Boolean = true,
    val boostsRemaining: Int = 3,
    val instantChatsRemaining: Int = 12
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
