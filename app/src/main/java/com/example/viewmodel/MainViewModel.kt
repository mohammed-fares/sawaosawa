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
    val locationStatusText: String = "القاهرة، مصر"
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
            it.copy(boostActive = true, boostsRemaining = remaining)
        }
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

    fun sendMessage(candidateId: String, text: String, isAudio: Boolean = false) {
        if (text.isBlank() && !isAudio) return
        viewModelScope.launch {
            chatDao.insertMessage(
                ChatMessage(
                    candidateId = candidateId,
                    text = if (isAudio) "تسجيل صوتي (00:30)" else text.trim(),
                    isFromUser = true,
                    isChaperoneMonitored = _uiState.value.currentUser.isChaperoneActive,
                    isAudioVoiceNote = isAudio,
                    audioDuration = if (isAudio) "00:30" else "00:00"
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
}
