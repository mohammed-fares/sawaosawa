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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class ScreenState {
    object Onboarding : ScreenState()
    object Auth : ScreenState()
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
    val showRoseShowerOverlay: Boolean = false
)

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getDatabase(application)
    private val candidateDao = db.candidateDao()
    private val chatDao = db.chatDao()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        initDatabaseIfNeeded()
        observeData()
    }

    private fun initDatabaseIfNeeded() {
        viewModelScope.launch {
            val count = candidateDao.countCandidates()
            if (count == 0) {
                candidateDao.insertAll(InitialData.sampleCandidates)
                for (msg in InitialData.initialChatMessages) {
                    chatDao.insertMessage(msg)
                }
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
                    candidate.religiousPractice.contains(filters.religiousPractice) ||
                    candidate.religiousPracticeEn.contains(filters.religiousPractice, ignoreCase = true)
            val dressMatches = filters.islamicDress == "الكل" || filters.islamicDress == "All" ||
                    candidate.islamicDress.contains(filters.islamicDress) ||
                    candidate.islamicDressEn.contains(filters.islamicDress, ignoreCase = true)
            val verifiedMatches = !filters.onlyVerified || candidate.isVerified
            ageMatches && practiceMatches && dressMatches && verifiedMatches
        }
    }

    fun completeOnboarding() {
        _uiState.update { it.copy(currentScreen = ScreenState.Main) }
    }

    fun openAuth() {
        _uiState.update { it.copy(currentScreen = ScreenState.Auth) }
    }

    fun onAuthSuccess(name: String, phoneOrEmail: String, activeHours: String) {
        _uiState.update { state ->
            val updatedUser = state.currentUser.copy(
                name = if (name.isNotBlank()) name else state.currentUser.name,
                phoneNumber = if (phoneOrEmail.startsWith("+") || phoneOrEmail.any { ch -> ch.isDigit() }) phoneOrEmail else state.currentUser.phoneNumber,
                email = if (phoneOrEmail.contains("@")) phoneOrEmail else state.currentUser.email,
                activeHours = if (activeHours.isNotBlank()) activeHours else state.currentUser.activeHours,
                isRegisteredWithFirebase = true
            )
            state.copy(
                currentUser = updatedUser,
                currentScreen = ScreenState.Main
            )
        }
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
}
