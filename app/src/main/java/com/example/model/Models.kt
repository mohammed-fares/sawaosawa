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
    val isOnlineNow: Boolean = true,
    val isVip: Boolean = true,
    val rosesReceivedCount: Int = 18,
    val country: String = "مصر",
    val area: String = "المعادي",
    val madhab: String = "شافعي / عام",
    val nationality: String = "مصرية",
    val origin: String = "مصر",
    val childrenCount: Int = 0,
    val desireForChildren: String = "نعم، أرغب في أطفال",
    val previousMarriage: String = "لم يسبق الزواج",
    val smoking: String = "غير مدخن",
    val lifestyle: String = "صحي ومتزن",
    val incomeRange: String = "متوسط إلى مرتفع",
    val interests: String = "القراءة، السفر، العمل التطوعي، الرياضة الخفيفة",
    val hobbies: String = "الخط العربي، القراءة",
    val partnerPreferences: String = "شخص ذو خلق ودين يقدر الحياة الزوجية والمودة",
    val religiousPreferences: String = "المحافظة على الصلاة وتحري الحلال",
    val familyPreferences: String = "تقدير الترابط الأسري والاحترام المتبادل",
    val hiddenConditions: String? = "اشتراط استقلالية السكن وإتمام الدراسات العليا",
    val hiddenConditionsAccessGranted: Boolean = false,
    val compatibilityScore: Int = 94,
    val referralCode: String = "SAWA100",
    val referralCount: Int = 0
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
    val audioDuration: String = "00:30",
    val imageUrl: String? = null
)

data class FilterPreferences(
    val minAge: Int = 20,
    val maxAge: Int = 35,
    val maxDistanceKm: Int = 50,
    val locationCountry: String = "الكل",
    val languagePreference: String = "الكل",
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
    val isVip: Boolean = true,
    val subscriptionPlan: String = "GOLD_MONTHLY",
    val subscriptionPrice: String = "199.99 ج.م / شهرياً",
    val subscriptionExpiresAt: String = "30 أكتوبر 2026",
    val boostsRemaining: Int = 3,
    val instantChatsRemaining: Int = 12,
    val heartsBalance: Int = 18,
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
    val firebaseUid: String? = null,
    val rosesBalance: Int = 38, // Virtual roses received by female user
    val rosesRequiredForRenewal: Int = 50,
    val photoUris: List<String> = emptyList(), // Up to 3 user photos
    val selectedPhotoIndex: Int = 0, // Which photo is primary
    val selfieVerificationStatus: String = "VERIFIED", // "NONE", "PENDING", "VERIFIED", "REJECTED"
    val selfieUri: String? = null,
    val bioAudioDuration: String? = null,
    val revealedPhotoUserIds: Set<String> = emptySet(),
    val likedCandidateIds: Set<String> = emptySet(),
    val hasAcceptedCommitmentAgreement: Boolean = false,
    val commitmentAgreementTimestamp: Long = 0L,
    val country: String = "مصر",
    val area: String = "المعادي",
    val madhab: String = "شافعي / عام",
    val nationality: String = "مصرية",
    val origin: String = "مصر",
    val childrenCount: Int = 0,
    val desireForChildren: String = "نعم، أرغب في الإنجاب وتكوين أسرة صالحة",
    val previousMarriage: String = "لم يسبق الزواج",
    val smoking: String = "غير مدخنة",
    val lifestyle: String = "ملتزمة، متزنة، محبة للعائلة والصحة",
    val incomeRange: String = "مستور والحمد لله",
    val interests: String = "الطب، رعاية الأطفال، القراءة، التطوع الخيري",
    val hobbies: String = "القراءة، الطبخ الصحي، المشي",
    val partnerPreferences: String = "شخص يخاف الله، طموح، يقدر المودة والرحمة والاستقرار الأسري",
    val religiousPreferences: String = "المحافظة على الصلوات، الصدق، الأمانة",
    val familyPreferences: String = "بر الوالدين وصلة الرحم",
    val hiddenConditions: String? = "توفير سكن مستقل ومراعاة طبيعة عملي في الطب",
    val hiddenConditionsAccessGrantedTo: Set<String> = emptySet(),
    val referralCode: String = "SAWA789",
    val referredBy: String? = null,
    val referralCount: Int = 3,
    val registrationStep: String = "COMPLETED"
)

data class PhotoVerificationRequest(
    val id: String,
    val userId: String,
    val userName: String,
    val userAvatarRes: Int = com.example.R.drawable.profile_sarah,
    val userPhotoUri: String? = null,
    val selfiePhotoUri: String? = null,
    val timestamp: String = "منذ 10 دقائق",
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED
    val rejectionReason: String? = null
)

data class TransactionRecord(
    val id: String,
    val userId: String,
    val userName: String,
    val planId: String,
    val planTitle: String,
    val amount: String,
    val currency: String,
    val paymentMethod: String, // VODAFONE_CASH, FAWRY, APPLE_PAY, MADA, CREDIT_CARD
    val timestamp: String,
    val status: String = "COMPLETED" // COMPLETED, PENDING, FAILED
)

data class VirtualRose(
    val id: String = "",
    val senderId: String = "",
    val senderName: String = "",
    val receiverId: String = "",
    val roseCount: Int = 1,
    val timestamp: Long = System.currentTimeMillis(),
    val message: String = "باقة ورد عطرة بنية التعارف الحلال 🌹"
)

data class AppGlobalSettings(
    val appCurrency: String = "EGP", // EGP, SAR, USD, AED
    val appCurrencySymbol: String = "ج.م",
    val primaryColorHex: Long = 0xFF0F4C47, // Petroleum Green
    val goldColorHex: Long = 0xFFD4AF37,    // Radiant Gold
    val logoStyle: String = "COMBINED",     // "EMBLEM", "TYPOGRAPHY", "COMBINED"
    val discreteNotifications: Boolean = true,
    val rosesToRenewSubscription: Int = 50,
    val weeklyPrice: Double = 79.0,
    val monthlyPrice: Double = 199.0,
    val annualPrice: Double = 899.0,
    val bundlePrice: Double = 349.0
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

enum class RegistrationFlowStep {
    WELCOME,
    LANGUAGE,
    COUNTRY,
    PHONE_INPUT,
    OTP_VERIFY,
    ACCOUNT_CREATION,
    COMMITMENT_AGREEMENT,
    PROFILE_SETUP,
    PHOTO_UPLOAD,
    SELFIE_VERIFY,
    PREFERENCES,
    LOCATION_PERMISSION,
    NOTIFICATIONS_PERMISSION,
    COMPLETED
}

data class CommitmentAgreementRecord(
    val agreementVersion: String = "2.4",
    val acceptedAt: Long = System.currentTimeMillis(),
    val userId: String = "",
    val language: String = "ar",
    val ipOrDeviceInfo: String = "Android-Device-Verified"
)

enum class AdminRole {
    SUPER_ADMIN,
    ADMIN,
    MODERATOR,
    VERIFICATION_AGENT,
    SUPPORT_AGENT,
    MARKETING_MANAGER,
    FINANCE_MANAGER,
    CONTENT_MANAGER,
    ANALYTICS_MANAGER
}

data class MatchingWeights(
    val preferencesWeight: Float = 0.25f,
    val distanceWeight: Float = 0.15f,
    val interestsWeight: Float = 0.15f,
    val religiousWeight: Float = 0.20f,
    val intentionsWeight: Float = 0.15f,
    val completenessWeight: Float = 0.10f
)

data class CompatibilityBreakdown(
    val totalScore: Int,
    val religiousScore: Int,
    val intentionsScore: Int,
    val interestsScore: Int,
    val distanceScore: Int,
    val preferencesScore: Int,
    val summaryAr: String,
    val summaryEn: String
)

data class RoseLedgerEntry(
    val id: String = "",
    val userId: String = "",
    val type: String = "EARN", // EARN, BUY, SEND, RECEIVE, SPEND, REFUND, ADMIN_GRANT
    val amount: Int = 1,
    val balanceAfter: Int = 0,
    val counterpartyId: String? = null,
    val counterpartyName: String? = null,
    val description: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

data class HiddenConditionsRequest(
    val id: String,
    val requesterId: String,
    val requesterName: String,
    val targetUserId: String,
    val status: String = "PENDING", // PENDING, ACCEPTED, REJECTED, REVOKED
    val requestedAt: Long = System.currentTimeMillis()
)

data class WaliRecord(
    val name: String,
    val phone: String,
    val relationship: String,
    val isApproved: Boolean = false,
    val canViewChats: Boolean = true
)

data class CampaignRecord(
    val id: String,
    val name: String,
    val type: String, // INSTALL, REGISTRATION, SUBSCRIPTION, ROSES, BRAND
    val status: String = "ACTIVE", // ACTIVE, PAUSED, COMPLETED
    val dailyBudget: Double,
    val totalBudget: Double,
    val country: String = "مصر",
    val gender: String = "ALL",
    val targetAgeMin: Int = 20,
    val targetAgeMax: Int = 45,
    val impressions: Int = 0,
    val clicks: Int = 0,
    val conversions: Int = 0,
    val spend: Double = 0.0,
    val roas: Double = 0.0
)

data class SocialMediaDraft(
    val id: String,
    val platform: String, // FACEBOOK, INSTAGRAM, TIKTOK, YOUTUBE, X, TELEGRAM, WHATSAPP
    val title: String,
    val captionAr: String,
    val captionEn: String,
    val hashtags: String,
    val status: String = "DRAFT", // DRAFT, SCHEDULED, PUBLISHED
    val scheduledDate: String = "2026-10-10"
)

data class CouponRecord(
    val code: String,
    val discountPercent: Int,
    val validUntil: String,
    val usageCount: Int = 0,
    val maxUsage: Int = 500,
    val isActive: Boolean = true
)

data class AuditLogRecord(
    val id: String,
    val adminUser: String,
    val role: String,
    val action: String,
    val targetResource: String,
    val details: String,
    val timestamp: String,
    val ipAddress: String = "127.0.0.1"
)

data class SupportTicketRecord(
    val id: String,
    val userId: String,
    val userName: String,
    val subject: String,
    val category: String,
    val priority: String = "NORMAL",
    val status: String = "OPEN", // OPEN, IN_PROGRESS, RESOLVED, CLOSED
    val createdAt: String,
    val messagesCount: Int = 1
)
