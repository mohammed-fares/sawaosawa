package com.example.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.InitialData
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.model.ChatMessage
import com.example.model.CurrentUserProfile
import com.example.model.FilterPreferences
import com.example.model.MainNavigationTab
import com.example.model.VirtualRose
import com.example.data.RoseFirestoreService
import com.example.model.AppGlobalSettings
import com.example.model.AdminReport
import com.example.model.PhotoVerificationRequest
import com.example.model.TransactionRecord
import com.example.ui.components.TopNotificationData
import com.example.util.SecureImagePickerHelper
import com.example.model.RegistrationFlowStep
import com.example.model.CompatibilityBreakdown
import com.example.model.MatchingWeights
import com.example.model.RoseLedgerEntry
import com.example.model.HiddenConditionsRequest
import com.example.model.AuditLogRecord
import com.example.util.MatchingEngine
import com.example.data.RoseLedgerRepository
import com.example.data.SubscriptionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Onboarding : ScreenState()
    object Auth : ScreenState()
    data class RegistrationWizard(val step: RegistrationFlowStep = RegistrationFlowStep.WELCOME) : ScreenState()
    object CommitmentAgreement : ScreenState()
    object Main : ScreenState()
    object Filters : ScreenState()
    object GoldCenter : ScreenState()
    object SelfieVerification : ScreenState()
    object EditProfile : ScreenState()
    object Subscriptions : ScreenState()
    object PrivacyPolicy : ScreenState()
    object AdminDashboard : ScreenState()
    data class CandidateDetail(val candidate: CandidateProfile) : ScreenState()
    data class MatchCelebration(val candidate: CandidateProfile) : ScreenState()
    data class ChatDetail(val candidate: CandidateProfile) : ScreenState()
    data class VideoCall(val candidate: CandidateProfile, val isIncoming: Boolean = true) : ScreenState()
    data class ChaperoneNotice(val candidate: CandidateProfile) : ScreenState()
}

data class UiState(
    val currentScreen: ScreenState = ScreenState.Onboarding,
    val currentTab: MainNavigationTab = MainNavigationTab.DISCOVER,
    val language: AppLanguage = AppLanguage.ARABIC,
    val webAdminServerPort: Int = 8080,
    val isWebAdminServerRunning: Boolean = true,
    val candidates: List<CandidateProfile> = emptyList(),
    val filteredCandidates: List<CandidateProfile> = emptyList(),
    val matchedCandidates: List<CandidateProfile> = emptyList(),
    val currentCandidateIndex: Int = 0,
    val filters: FilterPreferences = FilterPreferences(),
    val currentUser: CurrentUserProfile = CurrentUserProfile(),
    val activeChatMessages: List<ChatMessage> = emptyList(),
    val activeChatCandidate: CandidateProfile? = null,
    val matchPopupCandidate: CandidateProfile? = null,
    val boostActive: Boolean = false,
    val boostsRemaining: Int = 3,
    val instantChatsRemaining: Int = 12,
    val activeAudioId: Long? = null,
    val isAudioPlaying: Boolean = false,
    val adminBroadcastSentToast: String? = null,
    val userLatitude: Double = 30.0444, // Default Cairo
    val userLongitude: Double = 31.2357,
    val isGpsActive: Boolean = false,
    val locationStatusText: String = "القاهرة، مصر",
    val topNotification: TopNotificationData? = null,
    val appSettings: AppGlobalSettings = AppGlobalSettings(),
    val verificationRequests: List<PhotoVerificationRequest> = emptyList(),
    val transactions: List<TransactionRecord> = emptyList(),
    val reports: List<AdminReport> = emptyList(),
    val showRocketTakeoffOverlay: Boolean = false,
    val showRoseShowerOverlay: Boolean = false,
    val registrationStep: RegistrationFlowStep = RegistrationFlowStep.WELCOME,
    val roseLedger: List<RoseLedgerEntry> = emptyList(),
    val compatibilityBreakdowns: Map<String, CompatibilityBreakdown> = emptyMap(),
    val hiddenConditionsRequests: List<HiddenConditionsRequest> = emptyList(),
    val auditLogs: List<AuditLogRecord> = emptyList()
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val candidateDao = db.candidateDao()
    private val chatDao = db.chatDao()
    private val roseLedgerRepo = RoseLedgerRepository(application)
    private val subscriptionRepo = SubscriptionRepository(application)

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var webAdminServer: com.example.server.WebAdminServer? = null

    init {
        initDatabaseIfNeeded()
        observeData()
        loadLocalPreferences()
        startWebAdminServer()
    }

    private fun loadLocalPreferences() {
        val prefs = getApplication<Application>().getSharedPreferences("sawa_user_prefs", android.content.Context.MODE_PRIVATE)
        val hasAgreed = prefs.getBoolean("commitment_agreed", false)
        val savedStepStr = prefs.getString("registration_step", RegistrationFlowStep.WELCOME.name) ?: RegistrationFlowStep.WELCOME.name
        val savedStep = try { RegistrationFlowStep.valueOf(savedStepStr) } catch (_: Exception) { RegistrationFlowStep.WELCOME }
        
        if (hasAgreed) {
            _uiState.update { 
                it.copy(
                    currentUser = it.currentUser.copy(hasAcceptedCommitmentAgreement = true),
                    registrationStep = RegistrationFlowStep.COMPLETED,
                    roseLedger = roseLedgerRepo.ledgerEntries.value
                ) 
            }
        } else {
            _uiState.update { it.copy(registrationStep = savedStep, roseLedger = roseLedgerRepo.ledgerEntries.value) }
        }
    }

    private fun initDatabaseIfNeeded() {
        viewModelScope.launch {
            try {
                val count = candidateDao.countCandidates()
                if (count == 0) {
                    candidateDao.insertAll(InitialData.sampleCandidates)
                    for (msg in InitialData.initialChatMessages) {
                        chatDao.insertMessage(msg)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("MainViewModel", "Database initialization handled safely: " + e.message, e)
            }

            // Populate sample verification requests, transactions and reports for admin dashboard & testing
            val sampleVerificationRequests = listOf(
                PhotoVerificationRequest(
                    id = "req_101",
                    userId = "user_sarah",
                    userName = "سارة أحمد",
                    userAvatarRes = com.example.R.drawable.profile_sarah,
                    userPhotoUri = null,
                    selfiePhotoUri = null,
                    timestamp = "منذ 15 دقيقة",
                    status = "PENDING"
                ),
                PhotoVerificationRequest(
                    id = "req_102",
                    userId = "user_layla",
                    userName = "ليلى محمود",
                    userAvatarRes = com.example.R.drawable.profile_layla,
                    userPhotoUri = null,
                    selfiePhotoUri = null,
                    timestamp = "منذ ساعة",
                    status = "PENDING"
                ),
                PhotoVerificationRequest(
                    id = "req_103",
                    userId = "user_nadia",
                    userName = "نادية كريم",
                    userAvatarRes = com.example.R.drawable.profile_sarah,
                    userPhotoUri = null,
                    selfiePhotoUri = null,
                    timestamp = "أمس",
                    status = "APPROVED"
                )
            )

            val sampleTransactions = listOf(
                TransactionRecord(
                    id = "TXN-882194",
                    userId = "user_ahmed",
                    userName = "أحمد خليل",
                    planId = "MONTHLY",
                    planTitle = "الباقة الشهرية المميزة",
                    amount = "199 ج.م",
                    currency = "EGP",
                    paymentMethod = "VODAFONE_CASH",
                    timestamp = "اليوم 02:40 م",
                    status = "COMPLETED"
                ),
                TransactionRecord(
                    id = "TXN-741902",
                    userId = "user_omar",
                    userName = "عمر فاروق",
                    planId = "ANNUAL",
                    planTitle = "الباقة السنوية الكاملة",
                    amount = "899 ج.م",
                    currency = "EGP",
                    paymentMethod = "FAWRY",
                    timestamp = "أمس 11:15 ص",
                    status = "COMPLETED"
                ),
                TransactionRecord(
                    id = "TXN-631024",
                    userId = "user_tariq",
                    userName = "طارق زيدان",
                    planId = "BUNDLE",
                    planTitle = "باقة الحلال الشاملة VIP",
                    amount = "349 ج.م",
                    currency = "EGP",
                    paymentMethod = "APPLE_PAY",
                    timestamp = "منذ يومين",
                    status = "COMPLETED"
                )
            )

            val sampleReports = listOf(
                AdminReport(
                    id = "r1",
                    reporterName = "سارة",
                    reportedUserId = "c1",
                    reportedUserName = "عدنان",
                    reason = "طلب التواصل خارج التطبيق بدون علم الولي",
                    timestamp = "منذ 15 دقيقة",
                    status = "PENDING"
                ),
                AdminReport(
                    id = "r2",
                    reporterName = "نور",
                    reportedUserId = "c4",
                    reportedUserName = "نادية",
                    reason = "اشتباه في صورة الحساب الشخصي",
                    timestamp = "منذ ساعتين",
                    status = "PENDING"
                )
            )

            _uiState.update { state ->
                state.copy(
                    verificationRequests = sampleVerificationRequests,
                    transactions = sampleTransactions,
                    reports = sampleReports
                )
            }
        }
    }

    private fun observeData() {
        viewModelScope.launch {
            candidateDao.getActiveCandidates().collectLatest { list ->
                _uiState.update { state ->
                    val filtered = applyFilters(list, state.filters)
                    state.copy(
                        candidates = list,
                        filteredCandidates = filtered,
                        currentCandidateIndex = 0
                    )
                }
            }
        }

        viewModelScope.launch {
            candidateDao.getMatchedCandidates().collectLatest { matches ->
                _uiState.update { it.copy(matchedCandidates = matches) }
            }
        }
    }

    private fun applyFilters(list: List<CandidateProfile>, filters: FilterPreferences): List<CandidateProfile> {
        return list.filter { candidate ->
            if (candidate.isBanned) return@filter false
            val ageMatches = candidate.age in filters.minAge..filters.maxAge
            val practiceMatches = filters.religiousPractice == "الكل" || filters.religiousPractice == "All" ||
                    candidate.religiousPractice.contains(filters.religiousPractice, ignoreCase = true) ||
                    candidate.religiousPracticeEn.contains(filters.religiousPractice, ignoreCase = true)
            val dressMatches = filters.islamicDress == "الكل" || filters.islamicDress == "All" ||
                    candidate.islamicDress.contains(filters.islamicDress, ignoreCase = true) ||
                    candidate.islamicDressEn.contains(filters.islamicDress, ignoreCase = true)
            val verifiedMatches = !filters.onlyVerified || candidate.isVerified
            val ethnicityMatches = filters.ethnicity == "الكل" || filters.ethnicity == "All" ||
                    candidate.ethnicity.contains(filters.ethnicity, ignoreCase = true) ||
                    candidate.ethnicityEn.contains(filters.ethnicity, ignoreCase = true)
            val locationMatches = filters.locationCountry == "الكل" || filters.locationCountry == "All" ||
                    candidate.city.contains(filters.locationCountry, ignoreCase = true) ||
                    candidate.cityEn.contains(filters.locationCountry, ignoreCase = true)
            val languageMatches = filters.languagePreference == "الكل" || filters.languagePreference == "All" ||
                    candidate.languages.contains(filters.languagePreference, ignoreCase = true)
            ageMatches && practiceMatches && dressMatches && verifiedMatches && ethnicityMatches && locationMatches && languageMatches
        }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openAuth() {
        _uiState.update { it.copy(currentScreen = ScreenState.Auth) }
    }

    fun onAuthSuccess(name: String, phoneOrEmail: String, activeHours: String) {
        val prefs = getApplication<Application>().getSharedPreferences("sawa_user_prefs", android.content.Context.MODE_PRIVATE)
        val hasAgreed = prefs.getBoolean("commitment_agreed", false)

        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                name = if (name.isNotBlank()) name else state.currentUser.name,
                phoneNumber = if (phoneOrEmail.startsWith("+") || phoneOrEmail.any { ch -> ch.isDigit() }) phoneOrEmail else state.currentUser.phoneNumber,
                email = if (phoneOrEmail.contains("@")) phoneOrEmail else state.currentUser.email,
                activeHours = if (activeHours.isNotBlank()) activeHours else state.currentUser.activeHours,
                isRegisteredWithFirebase = true,
                hasAcceptedCommitmentAgreement = hasAgreed
            )
            state.copy(
                currentUser = updatedUser,
                currentScreen = if (hasAgreed) ScreenState.Main else ScreenState.CommitmentAgreement
            )
        }
    }

    fun openRegistrationWizard() {
        val step = _uiState.value.registrationStep
        _uiState.update { it.copy(currentScreen = ScreenState.RegistrationWizard(step)) }
    }

    fun onRegistrationStepCompleted(nextStep: RegistrationFlowStep, updatedProfile: CurrentUserProfile) {
        val prefs = getApplication<Application>().getSharedPreferences("sawa_user_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putString("registration_step", nextStep.name).apply()

        _uiState.update { state ->
            state.copy(
                currentUser = updatedProfile,
                registrationStep = nextStep,
                currentScreen = if (nextStep == RegistrationFlowStep.COMPLETED) ScreenState.Main else ScreenState.RegistrationWizard(nextStep)
            )
        }

        if (nextStep == RegistrationFlowStep.COMPLETED) {
            prefs.edit().putBoolean("commitment_agreed", true).apply()
            recalculateMatching()
            showTopNotification(
                iconEmoji = "🎉",
                title = if (_uiState.value.language == AppLanguage.ARABIC)
                    "تم إكمال تسجيل حسابك بنجاح! مرحباً بك في سوا سوا"
                else
                    "Registration completed! Welcome to Sawa Sawa"
            )
        }
    }

    fun recalculateMatching() {
        val user = _uiState.value.currentUser
        val weights = MatchingWeights()
        val breakdowns = mutableMapOf<String, CompatibilityBreakdown>()
        val updatedCandidates = _uiState.value.candidates.map { cand ->
            val breakdown = MatchingEngine.calculateCompatibility(user, cand, weights)
            breakdowns[cand.id] = breakdown
            cand.copy(compatibilityScore = breakdown.totalScore)
        }
        _uiState.update { state ->
            state.copy(
                candidates = updatedCandidates,
                filteredCandidates = applyFilters(updatedCandidates, state.filters),
                compatibilityBreakdowns = breakdowns
            )
        }
    }

    fun requestHiddenConditionsAccess(candidateId: String, candidateName: String) {
        val req = HiddenConditionsRequest(
            id = "REQ-${System.currentTimeMillis()}",
            requesterId = _uiState.value.currentUser.phoneNumber,
            requesterName = _uiState.value.currentUser.name,
            targetUserId = candidateId
        )
        _uiState.update { state ->
            val updated = state.candidates.map {
                if (it.id == candidateId) it.copy(hiddenConditionsAccessGranted = true) else it
            }
            state.copy(
                candidates = updated,
                filteredCandidates = applyFilters(updated, state.filters),
                hiddenConditionsRequests = listOf(req) + state.hiddenConditionsRequests
            )
        }
        showTopNotification(
            iconEmoji = "📩",
            title = if (_uiState.value.language == AppLanguage.ARABIC)
                "تم إرسال طلب استئذان لرؤية شروط $candidateName"
            else
                "Requested permission to view conditions of $candidateName"
        )
    }

    fun earnRoses(amount: Int, reason: String) {
        roseLedgerRepo.recordTransaction(
            type = "EARN",
            amount = amount,
            description = reason
        )
        _uiState.update { state ->
            state.copy(
                currentUser = state.currentUser.copy(rosesBalance = roseLedgerRepo.currentBalance.value),
                roseLedger = roseLedgerRepo.ledgerEntries.value
            )
        }
        showTopNotification(
            iconEmoji = "🌹",
            title = if (_uiState.value.language == AppLanguage.ARABIC)
                "حصلت على $amount وردة! ($reason)"
            else
                "Earned $amount Roses! ($reason)"
        )
    }

    fun buyRoses(amount: Int, paymentMethod: String, price: String) {
        viewModelScope.launch {
            subscriptionRepo.verifyAndActivatePayment(
                planId = "ROSES_$amount",
                planTitle = "باقة $amount وردة",
                amount = price,
                currency = _uiState.value.appSettings.appCurrency,
                paymentMethod = paymentMethod,
                userId = _uiState.value.currentUser.phoneNumber,
                userName = _uiState.value.currentUser.name
            )
            roseLedgerRepo.recordTransaction(
                type = "BUY",
                amount = amount,
                description = "شراء $amount وردة عبر $paymentMethod"
            )
            _uiState.update { state ->
                state.copy(
                    currentUser = state.currentUser.copy(rosesBalance = roseLedgerRepo.currentBalance.value),
                    roseLedger = roseLedgerRepo.ledgerEntries.value,
                    transactions = subscriptionRepo.transactions.value
                )
            }
            showTopNotification(
                iconEmoji = "💎",
                title = if (_uiState.value.language == AppLanguage.ARABIC)
                    "تم شراء $amount وردة بنجاح عبر $paymentMethod"
                else
                    "Purchased $amount Roses via $paymentMethod"
            )
        }
    }

    fun sendRosesWithLedger(candidateId: String, candidateName: String, count: Int = 1) {
        val result = roseLedgerRepo.recordTransaction(
            type = "SEND",
            amount = count,
            counterpartyId = candidateId,
            counterpartyName = candidateName,
            description = "إرسال باقة $count وردة إلى $candidateName بنية التعارف الحلال 🌹"
        )
        result.onSuccess {
            _uiState.update { state ->
                state.copy(
                    currentUser = state.currentUser.copy(rosesBalance = roseLedgerRepo.currentBalance.value),
                    roseLedger = roseLedgerRepo.ledgerEntries.value
                )
            }
            triggerRoseShower()
            showTopNotification(
                iconEmoji = "🌹",
                title = if (_uiState.value.language == AppLanguage.ARABIC)
                    "تم إرسال باقة الورد إلى $candidateName بنجاح!"
                else
                    "Sent $count Roses to $candidateName!"
            )
        }.onFailure { err ->
            showTopNotification(
                iconEmoji = "⚠️",
                title = err.message ?: "رصيد الورد غير كافٍ"
            )
        }
    }

    fun exportUserData() {
        showTopNotification(
            iconEmoji = "📦",
            title = if (_uiState.value.language == AppLanguage.ARABIC)
                "تم تجهيز وتصدير أرشيف بياناتك الشخصية (GDPR Archive)"
            else
                "Your personal data archive is prepared for download"
        )
    }

    fun deleteAccountPermanently() {
        viewModelScope.launch {
            candidateDao.resetAllDiscovery()
            val prefs = getApplication<Application>().getSharedPreferences("sawa_user_prefs", android.content.Context.MODE_PRIVATE)
            prefs.edit().clear().apply()
            _uiState.update {
                UiState(
                    currentScreen = ScreenState.Onboarding,
                    currentUser = CurrentUserProfile(name = "ضيف جديد", hasAcceptedCommitmentAgreement = false)
                )
            }
            showTopNotification(
                iconEmoji = "🗑️",
                title = if (_uiState.value.language == AppLanguage.ARABIC)
                    "تم حذف حسابك وكافة البيانات الشخصية نهائياً"
                else
                    "Your account and all data have been permanently deleted"
            )
        }
    }

    fun handleDeepLink(uriString: String) {
        when {
            uriString.contains("/profile/") -> {
                val candId = uriString.substringAfter("/profile/").substringBefore("/")
                val candidate = _uiState.value.candidates.find { it.id == candId }
                if (candidate != null) {
                    _uiState.update { it.copy(currentScreen = ScreenState.CandidateDetail(candidate)) }
                }
            }
            uriString.contains("/invite/") -> {
                val refCode = uriString.substringAfter("/invite/").substringBefore("/")
                _uiState.update {
                    it.copy(currentUser = it.currentUser.copy(referredBy = refCode))
                }
                showTopNotification(
                    iconEmoji = "🎁",
                    title = "تم تطبيق كود الدعوة والهدية الترحيبية: $refCode"
                )
            }
            uriString.contains("/verify") -> {
                _uiState.update { it.copy(currentScreen = ScreenState.SelfieVerification) }
            }
            uriString.contains("/download") -> {
                _uiState.update { it.copy(currentScreen = ScreenState.Main) }
            }
        }
    }

    fun acceptCommitmentAgreement() {
        val prefs = getApplication<Application>().getSharedPreferences("sawa_user_prefs", android.content.Context.MODE_PRIVATE)
        prefs.edit().putBoolean("commitment_agreed", true).apply()
        _uiState.update { state ->
            state.copy(
                currentUser = state.currentUser.copy(hasAcceptedCommitmentAgreement = true),
                currentScreen = ScreenState.Main
            )
        }
        showTopNotification(
            iconEmoji = "🤝",
            title = if (_uiState.value.language == AppLanguage.ARABIC) "أهلاً بك في منصة سوا! تم تسجيل تعهدك بالالتزام" else "Welcome to Sawa! Your commitment has been recorded"
        )
    }

    fun updateUserLocation(lat: Double, lon: Double, note: String) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                latitude = lat,
                longitude = lon,
                locationCity = note,
                isGpsEnabled = true
            )
            state.copy(
                userLatitude = lat,
                userLongitude = lon,
                isGpsActive = true,
                locationStatusText = note,
                currentUser = updatedUser
            )
        }
    }

    fun updateUserPresence(activeHours: String, isOnline: Boolean) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                activeHours = activeHours,
                isOnlineNow = isOnline
            )
            state.copy(currentUser = updatedUser)
        }
    }

    fun logout() {
        com.example.data.AuthService.signOut()
        _uiState.update { it.copy(currentScreen = ScreenState.Onboarding) }
    }

    fun deleteAccount() {
        viewModelScope.launch {
            candidateDao.resetAllDiscovery()
            _uiState.update {
                it.copy(
                    currentUser = CurrentUserProfile(),
                    currentScreen = ScreenState.Onboarding
                )
            }
        }
    }

    fun switchTab(tab: MainNavigationTab) {
        _uiState.update { it.copy(currentTab = tab, currentScreen = ScreenState.Main) }
    }

    fun toggleLanguage() {
        _uiState.update {
            val newLang = if (it.language == AppLanguage.ARABIC) AppLanguage.ENGLISH else AppLanguage.ARABIC
            it.copy(language = newLang)
        }
    }

    fun openFilters() {
        _uiState.update { it.copy(currentScreen = ScreenState.Filters) }
    }

    fun closeFilters() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openGoldCenter() {
        _uiState.update { it.copy(currentScreen = ScreenState.GoldCenter) }
    }

    fun closeGoldCenter() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openSubscriptions() {
        _uiState.update { it.copy(currentScreen = ScreenState.Subscriptions) }
    }

    fun closeSubscriptions() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openEditProfile() {
        _uiState.update { it.copy(currentScreen = ScreenState.EditProfile) }
    }

    fun closeEditProfile() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun updateUserProfile(updated: CurrentUserProfile) {
        _uiState.update {
            it.copy(
                currentUser = updated,
                currentScreen = ScreenState.Main
            )
        }
    }

    fun openPrivacyPolicy() {
        _uiState.update { it.copy(currentScreen = ScreenState.PrivacyPolicy) }
    }

    fun closePrivacyPolicy() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openAdminDashboard() {
        _uiState.update { it.copy(currentScreen = ScreenState.AdminDashboard) }
    }

    fun closeAdminDashboard() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun purchasePlan(planId: String, price: String) {
        _uiState.update {
            val updatedUser = it.currentUser.copy(
                isGoldMember = true,
                subscriptionPlan = planId,
                subscriptionPrice = price,
                boostsRemaining = it.currentUser.boostsRemaining + 3,
                instantChatsRemaining = it.currentUser.instantChatsRemaining + 12
            )
            it.copy(currentUser = updatedUser)
        }
    }

    fun openSelfieVerification() {
        _uiState.update { it.copy(currentScreen = ScreenState.SelfieVerification) }
    }

    fun completeSelfieVerification() {
        _uiState.update {
            it.copy(
                currentUser = it.currentUser.copy(isVerified = true),
                currentScreen = ScreenState.Main
            )
        }
    }

    fun startVideoCall(candidate: CandidateProfile, isIncoming: Boolean = false) {
        _uiState.update { it.copy(currentScreen = ScreenState.VideoCall(candidate, isIncoming)) }
    }

    fun acceptVideoCall(candidate: CandidateProfile) {
        _uiState.update { it.copy(currentScreen = ScreenState.VideoCall(candidate, isIncoming = false)) }
    }

    fun endVideoCall() {
        val cand = _uiState.value.activeChatCandidate
        if (cand != null) {
            _uiState.update { it.copy(currentScreen = ScreenState.ChatDetail(cand)) }
        } else {
            _uiState.update { it.copy(currentScreen = ScreenState.Main) }
        }
    }

    fun openChaperoneNotice(candidate: CandidateProfile) {
        _uiState.update { it.copy(currentScreen = ScreenState.ChaperoneNotice(candidate)) }
    }

    fun dismissChaperoneNotice() {
        val cand = _uiState.value.activeChatCandidate
        if (cand != null) {
            _uiState.update { it.copy(currentScreen = ScreenState.ChatDetail(cand)) }
        } else {
            _uiState.update { it.copy(currentScreen = ScreenState.Main) }
        }
    }

    fun updateFilters(newFilters: FilterPreferences) {
        _uiState.update { state ->
            val filtered = applyFilters(state.candidates, newFilters)
            state.copy(
                filters = newFilters,
                filteredCandidates = filtered,
                currentCandidateIndex = 0,
                currentScreen = ScreenState.Main
            )
        }
    }

    fun resetFilters() {
        updateFilters(FilterPreferences())
    }

    fun openCandidateDetail(candidate: CandidateProfile) {
        _uiState.update { it.copy(currentScreen = ScreenState.CandidateDetail(candidate)) }
    }

    fun closeCandidateDetail() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun likeCurrentCandidate() {
        val current = currentTopCandidate() ?: return
        viewModelScope.launch {
            val isMutual = current.isLikedYou
            candidateDao.markLiked(current.id, isMutual)
            if (isMutual) {
                _uiState.update {
                    it.copy(
                        matchPopupCandidate = current,
                        currentScreen = ScreenState.MatchCelebration(current)
                    )
                }
            } else {
                advanceCandidate()
            }
        }
    }

    fun passCurrentCandidate() {
        val current = currentTopCandidate() ?: return
        viewModelScope.launch {
            candidateDao.markPassed(current.id)
            advanceCandidate()
        }
    }

    fun instantChat(candidate: CandidateProfile, message: String = "السلام عليكم ورحمة الله وبركاته، أعجبني ملفك وأود التعارف للزواج") {
        viewModelScope.launch {
            candidateDao.markLiked(candidate.id, true)
            chatDao.insertMessage(
                ChatMessage(
                    candidateId = candidate.id,
                    text = message,
                    isFromUser = true,
                    isChaperoneMonitored = _uiState.value.currentUser.isChaperoneActive
                )
            )
            openChat(candidate)
        }
    }

    fun toggleBlur(candidate: CandidateProfile) {
        viewModelScope.launch {
            candidateDao.toggleBlur(candidate.id)
        }
    }

    fun toggleUserPhotoBlur() {
        _uiState.update {
            val updated = it.currentUser.copy(isPhotoBlurred = !it.currentUser.isPhotoBlurred)
            it.copy(currentUser = updated)
        }
    }

    fun toggleChaperone() {
        _uiState.update {
            val updated = it.currentUser.copy(isChaperoneActive = !it.currentUser.isChaperoneActive)
            it.copy(currentUser = updated)
        }
    }

    fun activateBoost() {
        _uiState.update {
            val remaining = if (it.boostsRemaining > 0) it.boostsRemaining - 1 else 0
            it.copy(
                boostActive = true,
                boostsRemaining = remaining,
                showRocketTakeoffOverlay = true
            )
        }
    }

    fun sendRose(candidate: CandidateProfile) {
        viewModelScope.launch {
            candidateDao.incrementRoses(candidate.id)
            val rose = VirtualRose(
                senderId = _uiState.value.currentUser.phoneNumber,
                senderName = _uiState.value.currentUser.name,
                receiverId = candidate.id,
                roseCount = 1,
                timestamp = System.currentTimeMillis(),
                message = "باقة ورد عطرة بنية التعارف الحلال 🌹"
            )
            RoseFirestoreService.sendRoseToUser(
                rose = rose,
                onSuccess = {},
                onError = {}
            )
        }
    }

    fun renewSubscriptionWithRoses(): Boolean {
        val currentRoses = _uiState.value.currentUser.rosesBalance
        val requiredRoses = _uiState.value.currentUser.rosesRequiredForRenewal
        if (currentRoses >= requiredRoses) {
            _uiState.update { state ->
                val updatedUser = state.currentUser.copy(
                    rosesBalance = currentRoses - requiredRoses,
                    isGoldMember = true,
                    isVip = true,
                    subscriptionPlan = "ROSE_RENEWAL",
                    subscriptionPrice = "$requiredRoses باقة ورد 🌹",
                    subscriptionExpiresAt = "30 نوفمبر 2026",
                    boostsRemaining = state.currentUser.boostsRemaining + 3,
                    instantChatsRemaining = state.currentUser.instantChatsRemaining + 12
                )
                state.copy(currentUser = updatedUser)
            }
            return true
        }
        return false
    }

    fun purchaseFullBundle() {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                boostsRemaining = state.currentUser.boostsRemaining + 5,
                instantChatsRemaining = state.currentUser.instantChatsRemaining + 20,
                rosesBalance = state.currentUser.rosesBalance + 50,
                heartsBalance = state.currentUser.heartsBalance + 25,
                isGoldMember = true,
                isVip = true
            )
            state.copy(
                currentUser = updatedUser,
                boostsRemaining = updatedUser.boostsRemaining,
                showRocketTakeoffOverlay = true
            )
        }
        showTopNotification(
            iconEmoji = "🎁",
            title = if (_uiState.value.language == AppLanguage.ARABIC) "تم تفعيل باقة الامتيازات الشاملة!" else "Full Bundle Activated!",
            message = if (_uiState.value.language == AppLanguage.ARABIC) "حصلت على 20 كسر جليد، 50 باقة ورد، 25 قلب مميز، 5 تعزيزات، وعضوية VIP!" else "Received 20 icebreakers, 50 roses, 25 hearts, 5 boosts & VIP!",
            durationMs = 4500L
        )
    }

    fun breakChatIce() {
        if (_uiState.value.currentUser.instantChatsRemaining > 0) {
            _uiState.update { state ->
                state.copy(currentUser = state.currentUser.copy(instantChatsRemaining = state.currentUser.instantChatsRemaining - 1))
            }
            showTopNotification(
                iconEmoji = "⚡",
                title = if (_uiState.value.language == AppLanguage.ARABIC) "رصيد كسر الجليد والدردشة" else "Chat Icebreaker Used",
                message = if (_uiState.value.language == AppLanguage.ARABIC) "تم استخدام كسر جليد واحد لمراسلة فورية! المتبقي: ${_uiState.value.currentUser.instantChatsRemaining}" else "Used 1 icebreaker! Remaining: ${_uiState.value.currentUser.instantChatsRemaining}",
                durationMs = 3000L
            )
        } else {
            showTopNotification(
                iconEmoji = "⚠️",
                title = if (_uiState.value.language == AppLanguage.ARABIC) "رصيد كسر الجليد غير كافٍ" else "Insufficient Icebreaker Credits",
                message = if (_uiState.value.language == AppLanguage.ARABIC) "يمكنك شحن رصيد إضافي أو شراء باقة الامتيازات الكاملة" else "Recharge credits or buy the full bundle pack",
                durationMs = 3000L
            )
        }
    }

    fun sendVirtualHeart() {
        if (_uiState.value.currentUser.heartsBalance > 0) {
            _uiState.update { state ->
                state.copy(currentUser = state.currentUser.copy(heartsBalance = state.currentUser.heartsBalance - 1))
            }
            showTopNotification(
                iconEmoji = "💖",
                title = if (_uiState.value.language == AppLanguage.ARABIC) "إرسال علامة القلب والإعجاب المميز" else "Super Heart Sent",
                message = if (_uiState.value.language == AppLanguage.ARABIC) "تم إرسال إعجاب مميز بقلب متوهج بنية الزواج الحلال! رصيدك: ${_uiState.value.currentUser.heartsBalance}" else "Sent special glowing heart for halal marriage! Balance: ${_uiState.value.currentUser.heartsBalance}",
                durationMs = 3500L
            )
        } else {
            showTopNotification(
                iconEmoji = "💖",
                title = if (_uiState.value.language == AppLanguage.ARABIC) "رصيد القلوب غير كافٍ" else "Hearts Balance Empty",
                message = if (_uiState.value.language == AppLanguage.ARABIC) "قم بتفعيل باقة الامتيازات الكاملة للحصول على 25 قلباً مميزاً" else "Activate full bundle for 25 super hearts",
                durationMs = 3000L
            )
        }
    }

    fun onInviteContactsSuccess(contactsCount: Int) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                rosesBalance = state.currentUser.rosesBalance + 10,
                boostsRemaining = state.currentUser.boostsRemaining + 1,
                instantChatsRemaining = state.currentUser.instantChatsRemaining + 3,
                heartsBalance = state.currentUser.heartsBalance + 5
            )
            state.copy(currentUser = updatedUser)
        }
        showTopNotification(
            iconEmoji = "🎁",
            title = if (_uiState.value.language == AppLanguage.ARABIC) "مكافأة دعوة جهات الاتصال!" else "Contacts Invite Reward!",
            message = if (_uiState.value.language == AppLanguage.ARABIC) "تم إرسال الدعوات وحصلت على 10 باقات ورد، 1 تعزيز، 3 كسر جليد، و5 قلوب!" else "Invites sent! Received 10 roses, 1 boost, 3 chats & 5 hearts!",
            durationMs = 4500L
        )
    }

    fun onGpsLocationUpdated(lat: Double, lng: Double, cityName: String) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                latitude = lat,
                longitude = lng,
                locationCity = cityName,
                isGpsEnabled = true
            )
            state.copy(currentUser = updatedUser)
        }
        showTopNotification(
            iconEmoji = "📍",
            title = if (_uiState.value.language == AppLanguage.ARABIC) "تم تحديث موقعك بدقة" else "Location Updated",
            message = if (_uiState.value.language == AppLanguage.ARABIC) "تم ضبط إحداثيات GPS ($cityName) لحساب أقرب التوافقات بدقة" else "GPS coordinates updated for accurate proximity matching",
            durationMs = 3000L
        )
    }

    fun onSaveAudioBio(duration: String) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                bioAudioDuration = duration
            )
            state.copy(currentUser = updatedUser)
        }
        showTopNotification(
            iconEmoji = "🎙️",
            title = if (_uiState.value.language == AppLanguage.ARABIC) "تم حفظ التسجيل الصوتي التعريفي!" else "Audio Bio Saved!",
            message = if (_uiState.value.language == AppLanguage.ARABIC) "أصبح مقطعك الصوتي متاحاً الآن في ملفك الشخصي ($duration)" else "Your audio bio is now live on your profile ($duration)",
            durationMs = 3500L
        )
    }

    fun toggleAudioPlayback(messageId: Long) {
        _uiState.update {
            if (it.activeAudioId == messageId && it.isAudioPlaying) {
                it.copy(isAudioPlaying = false)
            } else {
                it.copy(activeAudioId = messageId, isAudioPlaying = true)
            }
        }
    }

    // Admin Operations
    fun adminToggleVerify(candidateId: String) {
        viewModelScope.launch {
            val cand = candidateDao.getCandidateById(candidateId) ?: return@launch
            val updated = cand.copy(isVerified = !cand.isVerified)
            candidateDao.update(updated)
        }
    }

    fun adminToggleGold(candidateId: String) {
        viewModelScope.launch {
            val cand = candidateDao.getCandidateById(candidateId) ?: return@launch
            val updated = cand.copy(isGoldMember = !cand.isGoldMember)
            candidateDao.update(updated)
        }
    }

    fun adminToggleBan(candidateId: String) {
        viewModelScope.launch {
            val cand = candidateDao.getCandidateById(candidateId) ?: return@launch
            val updated = cand.copy(isBanned = !cand.isBanned)
            candidateDao.update(updated)
        }
    }

    fun adminToggleBlur(candidateId: String) {
        viewModelScope.launch {
            val cand = candidateDao.getCandidateById(candidateId) ?: return@launch
            val updated = cand.copy(isPhotoBlurred = !cand.isPhotoBlurred)
            candidateDao.update(updated)
        }
    }

    fun adminAddCandidate(candidate: CandidateProfile) {
        viewModelScope.launch {
            candidateDao.insertAll(listOf(candidate))
        }
    }

    fun adminSendBroadcast(title: String, message: String) {
        _uiState.update {
            it.copy(adminBroadcastSentToast = title)
        }
    }

    fun resetDiscovery() {
        viewModelScope.launch {
            candidateDao.resetAllDiscovery()
            _uiState.update { it.copy(currentCandidateIndex = 0) }
        }
    }

    private fun advanceCandidate() {
        _uiState.update { state ->
            val nextIndex = state.currentCandidateIndex + 1
            state.copy(currentCandidateIndex = nextIndex)
        }
    }

    fun currentTopCandidate(): CandidateProfile? {
        val filtered = _uiState.value.filteredCandidates
        val idx = _uiState.value.currentCandidateIndex
        return if (idx < filtered.size) filtered[idx] else null
    }

    fun dismissMatchPopup() {
        _uiState.update {
            it.copy(
                matchPopupCandidate = null,
                currentScreen = ScreenState.Main
            )
        }
        advanceCandidate()
    }

    fun openChat(candidate: CandidateProfile) {
        _uiState.update {
            it.copy(
                activeChatCandidate = candidate,
                currentScreen = ScreenState.ChatDetail(candidate)
            )
        }
        viewModelScope.launch {
            chatDao.getMessagesForCandidate(candidate.id).collectLatest { msgs ->
                _uiState.update { it.copy(activeChatMessages = msgs) }
            }
        }
    }

    fun sendMessage(candidateId: String, text: String, isAudio: Boolean = false, imageUrl: String? = null, audioDuration: String = "00:30") {
        if (text.isBlank() && !isAudio && imageUrl == null) return
        viewModelScope.launch {
            chatDao.insertMessage(
                ChatMessage(
                    candidateId = candidateId,
                    text = if (isAudio) "تسجيل صوتي ($audioDuration)" else if (imageUrl != null) "📷 صورة مرفقة" else text.trim(),
                    isFromUser = true,
                    isChaperoneMonitored = _uiState.value.currentUser.isChaperoneActive,
                    isAudioVoiceNote = isAudio,
                    audioDuration = audioDuration,
                    imageUrl = imageUrl
                )
            )

            kotlinx.coroutines.delay(1200)
            val replyText = if (_uiState.value.language == AppLanguage.ARABIC) {
                "شكراً لرسالتك المهذبة. بارك الله فيك، أسعد بالتواصل بما يرضي الله."
            } else {
                "Thank you for your respectful message. May Allah bless you, happy to connect."
            }
            chatDao.insertMessage(
                ChatMessage(
                    candidateId = candidateId,
                    text = replyText,
                    isFromUser = false,
                    isChaperoneMonitored = _uiState.value.currentUser.isChaperoneActive
                )
            )
            com.example.util.NotificationHelper.notifyNewDiscreetMessage(getApplication())
        }
    }

    fun closeChat() {
        _uiState.update {
            it.copy(
                currentScreen = ScreenState.Main,
                currentTab = MainNavigationTab.CHATS
            )
        }
    }

    fun navigateBack() {
        when (_uiState.value.currentScreen) {
            is ScreenState.CandidateDetail,
            is ScreenState.Filters,
            is ScreenState.GoldCenter,
            is ScreenState.Subscriptions,
            is ScreenState.EditProfile,
            is ScreenState.PrivacyPolicy,
            is ScreenState.AdminDashboard,
            is ScreenState.SelfieVerification,
            is ScreenState.MatchCelebration,
            is ScreenState.ChaperoneNotice -> {
                _uiState.update { it.copy(currentScreen = ScreenState.Main) }
            }
            is ScreenState.RegistrationWizard -> {
                _uiState.update { it.copy(currentScreen = ScreenState.Onboarding) }
            }
            is ScreenState.CommitmentAgreement -> {
                _uiState.update { it.copy(currentScreen = ScreenState.Auth) }
            }
            is ScreenState.Auth -> {
                _uiState.update { it.copy(currentScreen = ScreenState.Onboarding) }
            }
            is ScreenState.VideoCall -> {
                endVideoCall()
            }
            is ScreenState.ChatDetail -> {
                _uiState.update {
                    it.copy(
                        currentScreen = ScreenState.Main,
                        currentTab = MainNavigationTab.CHATS
                    )
                }
            }
            is ScreenState.Main -> {
                if (_uiState.value.currentTab != MainNavigationTab.DISCOVER) {
                    _uiState.update { it.copy(currentTab = MainNavigationTab.DISCOVER) }
                }
            }
            ScreenState.Onboarding -> {}
        }
    }

    // Top In-App Floating Notification Management
    fun showTopNotification(
        iconEmoji: String = "✨",
        title: String,
        message: String? = null,
        isGoldAlert: Boolean = false,
        durationMs: Long = 3200L
    ) {
        _uiState.update {
            it.copy(
                topNotification = TopNotificationData(
                    id = System.currentTimeMillis(),
                    iconEmoji = iconEmoji,
                    title = title,
                    message = message,
                    isGoldAlert = isGoldAlert,
                    durationMs = durationMs
                )
            )
        }
    }

    fun dismissTopNotification() {
        _uiState.update { it.copy(topNotification = null) }
    }

    // App Global Settings Management (Admin Control Panel)
    fun updateAppSettings(settings: AppGlobalSettings) {
        _uiState.update { it.copy(appSettings = settings) }
    }

    fun updateCurrency(currencyCode: String, symbol: String) {
        _uiState.update { state ->
            val updatedSettings = state.appSettings.copy(
                appCurrency = currencyCode,
                appCurrencySymbol = symbol
            )
            state.copy(appSettings = updatedSettings)
        }
    }

    fun updateLogoStyle(style: String) {
        _uiState.update { state ->
            val updatedSettings = state.appSettings.copy(logoStyle = style)
            state.copy(appSettings = updatedSettings)
        }
    }

    fun updateThemeColors(primaryHex: Long, goldHex: Long) {
        _uiState.update { state ->
            val updatedSettings = state.appSettings.copy(
                primaryColorHex = primaryHex,
                goldColorHex = goldHex
            )
            state.copy(appSettings = updatedSettings)
        }
    }

    fun updateRosesRenewalThreshold(roses: Int) {
        _uiState.update { state ->
            val updatedSettings = state.appSettings.copy(rosesToRenewSubscription = roses)
            val updatedUser = state.currentUser.copy(rosesRequiredForRenewal = roses)
            state.copy(appSettings = updatedSettings, currentUser = updatedUser)
        }
    }

    // User Profile Photos Management (Secure, max 3 photos)
    fun addProfilePhoto(filePath: String) {
        _uiState.update { state ->
            val currentList = state.currentUser.photoUris.toMutableList()
            if (currentList.size < 3) {
                currentList.add(filePath)
                val updatedUser = state.currentUser.copy(photoUris = currentList)
                state.copy(currentUser = updatedUser)
            } else {
                state
            }
        }
    }

    fun removeProfilePhoto(index: Int) {
        _uiState.update { state ->
            val currentList = state.currentUser.photoUris.toMutableList()
            if (index in currentList.indices) {
                val removedPath = currentList.removeAt(index)
                SecureImagePickerHelper.deletePhotoFile(removedPath)
                val newSelectedIndex = state.currentUser.selectedPhotoIndex.coerceIn(
                    0, (currentList.size - 1).coerceAtLeast(0)
                )
                val updatedUser = state.currentUser.copy(
                    photoUris = currentList,
                    selectedPhotoIndex = newSelectedIndex
                )
                state.copy(currentUser = updatedUser)
            } else {
                state
            }
        }
    }

    fun setPrimaryProfilePhoto(index: Int) {
        _uiState.update { state ->
            if (index in state.currentUser.photoUris.indices) {
                val updatedUser = state.currentUser.copy(selectedPhotoIndex = index)
                state.copy(currentUser = updatedUser)
            } else {
                state
            }
        }
    }

    // Photo Verification Management (Selfie Verification)
    fun submitSelfieVerification(selfieUri: String?) {
        val newReq = PhotoVerificationRequest(
            id = "req_${System.currentTimeMillis()}",
            userId = _uiState.value.currentUser.phoneNumber,
            userName = _uiState.value.currentUser.name,
            userAvatarRes = com.example.R.drawable.profile_sarah,
            userPhotoUri = _uiState.value.currentUser.photoUris.firstOrNull(),
            selfiePhotoUri = selfieUri,
            timestamp = "الآن",
            status = "PENDING"
        )
        _uiState.update { state ->
            val updatedList = listOf(newReq) + state.verificationRequests
            val updatedUser = state.currentUser.copy(
                selfieVerificationStatus = "PENDING",
                selfieUri = selfieUri
            )
            state.copy(
                verificationRequests = updatedList,
                currentUser = updatedUser,
                currentScreen = ScreenState.Main
            )
        }
        showTopNotification(
            iconEmoji = "📷",
            title = "تم إرسال سلفي التوثيق للمراجعة الإدارية بنجاح ✓",
            message = "سيتم تدقيق الصورة واعتماد شارة التوثيق الذهبية قريباً"
        )
    }

    fun adminApproveVerification(requestId: String) {
        _uiState.update { state ->
            val updatedRequests = state.verificationRequests.map {
                if (it.id == requestId) it.copy(status = "APPROVED") else it
            }
            val targetReq = state.verificationRequests.find { it.id == requestId }
            val updatedUser = if (targetReq != null && (targetReq.userId == state.currentUser.phoneNumber || targetReq.userId == "user_sarah")) {
                state.currentUser.copy(isVerified = true, selfieVerificationStatus = "VERIFIED")
            } else state.currentUser

            state.copy(
                verificationRequests = updatedRequests,
                currentUser = updatedUser
            )
        }
        showTopNotification(
            iconEmoji = "✓",
            title = "تمت الموافقة على توثيق الحساب واعتماد الشارة الذهبية!"
        )
    }

    fun adminRejectVerification(requestId: String, reason: String = "عدم وضوح ملامح الوجه أو عدم مطابقة الصورة") {
        _uiState.update { state ->
            val updatedRequests = state.verificationRequests.map {
                if (it.id == requestId) it.copy(status = "REJECTED", rejectionReason = reason) else it
            }
            val targetReq = state.verificationRequests.find { it.id == requestId }
            val updatedUser = if (targetReq != null && (targetReq.userId == state.currentUser.phoneNumber || targetReq.userId == "user_sarah")) {
                state.currentUser.copy(selfieVerificationStatus = "REJECTED")
            } else state.currentUser

            state.copy(
                verificationRequests = updatedRequests,
                currentUser = updatedUser
            )
        }
        showTopNotification(
            iconEmoji = "⚠️",
            title = "تم رفض طلب التوثيق",
            message = reason
        )
    }

    // Payment and Pricing Management
    fun recordTransaction(
        planId: String,
        planTitle: String,
        price: String,
        paymentMethod: String
    ) {
        val tx = TransactionRecord(
            id = "TXN-${System.currentTimeMillis().toString().takeLast(6)}",
            userId = _uiState.value.currentUser.phoneNumber,
            userName = _uiState.value.currentUser.name,
            planId = planId,
            planTitle = planTitle,
            amount = price,
            currency = _uiState.value.appSettings.appCurrency,
            paymentMethod = paymentMethod,
            timestamp = "الآن",
            status = "COMPLETED"
        )
        _uiState.update { state ->
            val isFullBundle = planId == "BUNDLE"
            val updatedBoosts = if (isFullBundle) state.currentUser.boostsRemaining + 10 else state.currentUser.boostsRemaining
            val updatedRoses = if (isFullBundle) state.currentUser.rosesBalance + 50 else state.currentUser.rosesBalance
            val updatedChats = if (isFullBundle) 999 else state.currentUser.instantChatsRemaining

            state.copy(
                transactions = listOf(tx) + state.transactions,
                currentUser = state.currentUser.copy(
                    isGoldMember = true,
                    isVip = true,
                    subscriptionPlan = planId,
                    subscriptionPrice = price,
                    boostsRemaining = updatedBoosts,
                    rosesBalance = updatedRoses,
                    instantChatsRemaining = updatedChats
                )
            )
        }
    }

    fun updatePlanPrices(weekly: Double, monthly: Double, annual: Double, bundle: Double) {
        _uiState.update { state ->
            val updated = state.appSettings.copy(
                weeklyPrice = weekly,
                monthlyPrice = monthly,
                annualPrice = annual,
                bundlePrice = bundle
            )
            state.copy(appSettings = updated)
        }
        showTopNotification(
            iconEmoji = "💳",
            title = "تم تحديث أسعار الاشتراكات وتطبيقها على المتجر بنجاح"
        )
    }

    // Chat Extra Features
    fun blockCandidate(candidateId: String) {
        viewModelScope.launch {
            candidateDao.toggleBan(candidateId)
            _uiState.update { state ->
                val updated = state.candidates.filterNot { it.id == candidateId }
                state.copy(
                    candidates = updated,
                    filteredCandidates = applyFilters(updated, state.filters),
                    currentScreen = ScreenState.Main
                )
            }
            showTopNotification(
                iconEmoji = "🚫",
                title = "تم حظر المستخدم بنجاح ولن يظهر لك مجدداً"
            )
        }
    }

    fun reportCandidate(reporterName: String, candidateId: String, candidateName: String, reason: String) {
        val rep = AdminReport(
            id = "rep_${System.currentTimeMillis()}",
            reporterName = reporterName,
            reportedUserId = candidateId,
            reportedUserName = candidateName,
            reason = reason,
            timestamp = "الآن",
            status = "PENDING"
        )
        _uiState.update { state ->
            state.copy(reports = listOf(rep) + state.reports)
        }
        showTopNotification(
            iconEmoji = "🛡️",
            title = "تم رفع البلاغ إلى المشرفين الشرعيين للمراجعة"
        )
    }

    fun dismissReport(reportId: String) {
        _uiState.update { state ->
            state.copy(reports = state.reports.filterNot { it.id == reportId })
        }
    }

    fun revealPhotoToUser(candidateId: String) {
        _uiState.update { state ->
            val revealed = state.currentUser.revealedPhotoUserIds + candidateId
            state.copy(currentUser = state.currentUser.copy(revealedPhotoUserIds = revealed))
        }
        showTopNotification(
            iconEmoji = "🔓",
            title = "تم منح الإذن للطرف الآخر برؤية صورك الخاصة بنجاح"
        )
    }

    fun sendCallInvitation(candidate: CandidateProfile, isVideo: Boolean) {
        showTopNotification(
            iconEmoji = if (isVideo) "📹" else "📞",
            title = if (_uiState.value.language == AppLanguage.ARABIC)
                "تم إرسال دعوة مكالمة شرعية إلى ${candidate.name}"
            else
                "Call invitation sent to ${candidate.nameEn}",
            message = "بانتظار قبول الطرف الآخر لبدء الاتصال الشرعي"
        )
    }

    fun dismissRocketTakeoffOverlay() {
        _uiState.update { it.copy(showRocketTakeoffOverlay = false) }
    }

    fun triggerRoseShower() {
        _uiState.update { it.copy(showRoseShowerOverlay = true) }
    }

    fun dismissRoseShower() {
        _uiState.update { it.copy(showRoseShowerOverlay = false) }
    }

    // Web Administration Server (Browser control of app content, permissions, verification, and pricing)
    fun startWebAdminServer() {
        if (webAdminServer != null) return
        try {
            webAdminServer = com.example.server.WebAdminServer(
                context = getApplication(),
                getCandidates = { _uiState.value.candidates },
                onToggleBan = { id -> adminToggleBan(id) },
                onToggleVerify = { id -> adminToggleVerify(id) },
                onToggleGold = { id -> adminToggleGold(id) },
                onUpdateCandidateContent = { id, name, city, bio ->
                    viewModelScope.launch {
                        val cand = candidateDao.getCandidateById(id) ?: return@launch
                        val updated = cand.copy(name = name, city = city, bio = bio)
                        candidateDao.update(updated)
                    }
                },
                getPricing = {
                    val settings = _uiState.value.appSettings
                    mapOf(
                        "weekly" to settings.weeklyPrice,
                        "monthly" to settings.monthlyPrice,
                        "annual" to settings.annualPrice
                    )
                },
                onUpdatePricing = { weekly, monthly, annual ->
                    val currentBundle = _uiState.value.appSettings.bundlePrice
                    updatePlanPrices(weekly, monthly, annual, currentBundle)
                },
                getVerifications = { _uiState.value.verificationRequests },
                onActionVerification = { id, approve ->
                    if (approve) {
                        adminApproveVerification(id)
                    } else {
                        adminRejectVerification(id)
                    }
                }
            ).also { server ->
                server.start()
                _uiState.update { it.copy(webAdminServerPort = server.activePort, isWebAdminServerRunning = true) }
            }
        } catch (e: Exception) {
            android.util.Log.e("MainViewModel", "Failed to start WebAdminServer", e)
        }
    }

    fun stopWebAdminServer() {
        webAdminServer?.stop()
        webAdminServer = null
        _uiState.update { it.copy(isWebAdminServerRunning = false) }
    }

    override fun onCleared() {
        super.onCleared()
        webAdminServer?.stop()
    }
}
