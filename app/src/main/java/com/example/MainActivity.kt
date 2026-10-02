package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.LayoutDirection
import com.example.ui.components.TopNotificationHud
import com.example.model.AppLanguage
import com.example.model.MainNavigationTab
import com.example.ui.components.SawaBottomNavBar
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.AuthScreen
import com.example.ui.screens.CandidateDetailScreen
import com.example.ui.screens.ChatDetailScreen
import com.example.ui.screens.ChaperoneNoticeDialog
import com.example.ui.screens.ChatsScreen
import com.example.ui.screens.CommunityScreen
import com.example.ui.screens.DiscoveryScreen
import com.example.ui.screens.EditProfileScreen
import com.example.ui.screens.FiltersScreen
import com.example.ui.screens.GoldCenterScreen
import com.example.ui.screens.MatchCelebrationScreen
import com.example.ui.screens.MyProfileScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PrivacyPolicyScreen
import com.example.ui.screens.SelfieVerificationScreen
import com.example.ui.screens.SubscriptionScreen
import com.example.ui.screens.VideoCallScreen
import com.example.ui.theme.SawaSawaTheme
import com.example.viewmodel.MainViewModel
import com.example.viewmodel.ScreenState
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val uiState by viewModel.uiState.collectAsState()
            val isArabic = uiState.language == AppLanguage.ARABIC
            val layoutDirection = if (isArabic) LayoutDirection.Rtl else LayoutDirection.Ltr

            CompositionLocalProvider(LocalLayoutDirection provides layoutDirection) {
                SawaSawaTheme(
                    customPrimaryColor = Color(uiState.appSettings.primaryColorHex),
                    customGoldColor = Color(uiState.appSettings.goldColorHex)
                ) {
                    val scope = rememberCoroutineScope()
                    val snackbarHostState = remember { SnackbarHostState() }

                    LaunchedEffect(uiState.adminBroadcastSentToast) {
                        uiState.adminBroadcastSentToast?.let {
                            viewModel.showTopNotification(
                                iconEmoji = "📢",
                                title = if (isArabic) "إشعار إداري عام" else "Admin Broadcast",
                                message = it,
                                durationMs = 4000L
                            )
                        }
                    }

                    // Hardware & gesture back navigation
                    BackHandler(enabled = uiState.currentScreen !is ScreenState.Onboarding) {
                        viewModel.navigateBack()
                    }

                    Box(modifier = Modifier.fillMaxSize()) {
                        when (val screen = uiState.currentScreen) {
                        is ScreenState.Onboarding -> {
                            OnboardingScreen(
                                language = uiState.language,
                                onContinue = { viewModel.openAuth() },
                                onToggleLanguage = { viewModel.toggleLanguage() }
                            )
                        }

                        is ScreenState.Auth -> {
                            AuthScreen(
                                language = uiState.language,
                                onAuthSuccess = { name, phoneOrEmail, activeHours ->
                                    viewModel.onAuthSuccess(name, phoneOrEmail, activeHours)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "مرحباً $name! تم التحقق والدخول بنجاح عبر Firebase" else "Welcome $name! Signed in via Firebase"
                                        )
                                    }
                                },
                                onBack = { viewModel.navigateBack() }
                            )
                        }

                        is ScreenState.Filters -> {
                            FiltersScreen(
                                currentFilters = uiState.filters,
                                language = uiState.language,
                                onApply = { newPrefs -> viewModel.updateFilters(newPrefs) },
                                onReset = { viewModel.resetFilters() },
                                onClose = { viewModel.closeFilters() }
                            )
                        }

                        is ScreenState.GoldCenter -> {
                            GoldCenterScreen(
                                user = uiState.currentUser,
                                language = uiState.language,
                                onClose = { viewModel.closeGoldCenter() },
                                onActivateBoost = {
                                    viewModel.activateBoost()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "🚀 تم تفعيل التعزيز بنجاح!" else "🚀 Boost activated successfully!"
                                        )
                                    }
                                }
                            )
                        }

                        is ScreenState.Subscriptions -> {
                            SubscriptionScreen(
                                user = uiState.currentUser,
                                language = uiState.language,
                                currencyCode = uiState.appSettings.appCurrency,
                                currencySymbol = uiState.appSettings.appCurrencySymbol,
                                onPlanPurchased = { planId, price ->
                                    viewModel.purchasePlan(planId, price)
                                    viewModel.showTopNotification(
                                        iconEmoji = "👑",
                                        title = if (isArabic) "تم تفعيل الاشتراك بنجاح!" else "Subscribed!",
                                        message = price,
                                        isGoldAlert = true
                                    )
                                },
                                onRenewWithRoses = {
                                    val ok = viewModel.renewSubscriptionWithRoses()
                                    if (ok) {
                                        viewModel.showTopNotification(
                                            iconEmoji = "🌹",
                                            title = if (isArabic) "تم تجديد اشتراك VIP بباقات الورد!" else "VIP renewed with roses!",
                                            isGoldAlert = true
                                        )
                                    }
                                },
                                onClose = { viewModel.closeSubscriptions() }
                            )
                        }

                        is ScreenState.EditProfile -> {
                            EditProfileScreen(
                                user = uiState.currentUser,
                                language = uiState.language,
                                onSave = { updatedUser ->
                                    viewModel.updateUserProfile(updatedUser)
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "✓ تم حفظ تعديلات الملف الشخصي بنجاح" else "✓ Profile updated successfully"
                                        )
                                    }
                                },
                                onBack = { viewModel.closeEditProfile() }
                            )
                        }

                        is ScreenState.PrivacyPolicy -> {
                            PrivacyPolicyScreen(
                                language = uiState.language,
                                onBack = { viewModel.closePrivacyPolicy() },
                                onDeleteAccount = {
                                    viewModel.deleteAccount()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "تم حذف الحساب والبيانات نهائياً" else "Account deleted successfully"
                                        )
                                    }
                                }
                            )
                        }

                        is ScreenState.AdminDashboard -> {
                            AdminDashboardScreen(
                                candidates = uiState.candidates,
                                language = uiState.language,
                                appSettings = uiState.appSettings,
                                onBack = { viewModel.closeAdminDashboard() },
                                onToggleVerify = { id -> viewModel.adminToggleVerify(id) },
                                onToggleGold = { id -> viewModel.adminToggleGold(id) },
                                onToggleBan = { id ->
                                    viewModel.adminToggleBan(id)
                                    viewModel.showTopNotification(
                                        iconEmoji = "🛡️",
                                        title = if (isArabic) "تم تعديل حالة الحظر للعضو" else "Member ban status updated"
                                    )
                                },
                                onToggleBlur = { id -> viewModel.adminToggleBlur(id) },
                                onAddCandidate = { newCand ->
                                    viewModel.adminAddCandidate(newCand)
                                    viewModel.showTopNotification(
                                        iconEmoji = "✓",
                                        title = if (isArabic) "تم إضافة العضو الجديد للمنصة بنجاح ✓" else "New member added ✓"
                                    )
                                },
                                onSendBroadcast = { title, msg ->
                                    viewModel.adminSendBroadcast(title, msg)
                                },
                                onUpdateCurrency = { curr, sym ->
                                    viewModel.updateCurrency(curr, sym)
                                    viewModel.showTopNotification(
                                        iconEmoji = "💱",
                                        title = if (isArabic) "تم تغيير عملة التطبيق إلى $sym ($curr)" else "Currency updated to $curr"
                                    )
                                },
                                onUpdateLogoStyle = { style ->
                                    viewModel.updateLogoStyle(style)
                                    viewModel.showTopNotification(
                                        iconEmoji = "🎨",
                                        title = if (isArabic) "تم تعديل شكل وهوية الشعار بنجاح" else "Logo style updated"
                                    )
                                },
                                onUpdateColorPalette = { p, g ->
                                    viewModel.updateThemeColors(p, g)
                                    viewModel.showTopNotification(
                                        iconEmoji = "✨",
                                        title = if (isArabic) "تم تطبيق نظام الألوان الجديد بنجاح" else "New theme colors applied"
                                    )
                                },
                                onUpdateRosesThreshold = { t ->
                                    viewModel.updateRosesRenewalThreshold(t)
                                    viewModel.showTopNotification(
                                        iconEmoji = "🌹",
                                        title = if (isArabic) "تم تحديد شرط تجديد العضوية بـ $t وردة" else "Renewal set to $t roses"
                                    )
                                }
                            )
                        }

                        is ScreenState.SelfieVerification -> {
                            SelfieVerificationScreen(
                                language = uiState.language,
                                onComplete = {
                                    viewModel.completeSelfieVerification()
                                    scope.launch {
                                        snackbarHostState.showSnackbar(
                                            if (isArabic) "✓ تم توثيق حسابك بنجاح ومنحك شارة التوثيق الذهبية!" else "✓ Successfully verified! Gold badge awarded."
                                        )
                                    }
                                },
                                onBack = { viewModel.closeFilters() }
                            )
                        }

                        is ScreenState.VideoCall -> {
                            VideoCallScreen(
                                candidate = screen.candidate,
                                isIncoming = screen.isIncoming,
                                language = uiState.language,
                                onAccept = { viewModel.acceptVideoCall(screen.candidate) },
                                onEnd = { viewModel.endVideoCall() }
                            )
                        }

                        is ScreenState.ChaperoneNotice -> {
                            ChaperoneNoticeDialog(
                                candidate = screen.candidate,
                                language = uiState.language,
                                onDismiss = { viewModel.dismissChaperoneNotice() }
                            )
                        }

                        is ScreenState.CandidateDetail -> {
                            CandidateDetailScreen(
                                candidate = screen.candidate,
                                language = uiState.language,
                                onBack = { viewModel.closeCandidateDetail() },
                                onPass = {
                                    viewModel.passCurrentCandidate()
                                    viewModel.closeCandidateDetail()
                                },
                                onInstantChat = {
                                    viewModel.instantChat(screen.candidate)
                                },
                                onLike = {
                                    viewModel.likeCurrentCandidate()
                                    viewModel.closeCandidateDetail()
                                },
                                onToggleBlur = {
                                    viewModel.toggleBlur(screen.candidate)
                                },
                                onSendRose = {
                                    viewModel.sendRose(screen.candidate)
                                    viewModel.showTopNotification(
                                        iconEmoji = "🌹",
                                        title = if (isArabic) "أرسلت باقة ورد VIP" else "Sent VIP Rose",
                                        message = if (isArabic) "تم إهداء باقة الورد إلى ${screen.candidate.name} بنجاح" else "Rose sent to ${screen.candidate.nameEn}",
                                        isGoldAlert = true
                                    )
                                }
                            )
                        }

                        is ScreenState.MatchCelebration -> {
                            MatchCelebrationScreen(
                                candidate = screen.candidate,
                                language = uiState.language,
                                onStartChat = {
                                    viewModel.openChat(screen.candidate)
                                },
                                onKeepBrowsing = {
                                    viewModel.dismissMatchPopup()
                                }
                            )
                        }

                        is ScreenState.ChatDetail -> {
                            ChatDetailScreen(
                                candidate = screen.candidate,
                                messages = uiState.activeChatMessages,
                                language = uiState.language,
                                chaperoneActive = uiState.currentUser.isChaperoneActive,
                                chaperoneName = uiState.currentUser.chaperoneName,
                                onBack = { viewModel.closeChat() },
                                onSendMessage = { text ->
                                    viewModel.sendMessage(screen.candidate.id, text)
                                },
                                onSendVoiceNote = {
                                    viewModel.sendMessage(screen.candidate.id, "", isAudio = true, audioDuration = "00:30")
                                },
                                onSendVoiceNoteWithDuration = { duration ->
                                    viewModel.sendMessage(screen.candidate.id, "", isAudio = true, audioDuration = duration)
                                },
                                onSendImage = { imageUri ->
                                    viewModel.sendMessage(screen.candidate.id, "", isAudio = false, imageUrl = imageUri)
                                },
                                onStartVideoCall = {
                                    viewModel.startVideoCall(screen.candidate, isIncoming = false)
                                },
                                onOpenChaperoneNotice = {
                                    viewModel.openChaperoneNotice(screen.candidate)
                                },
                                onToggleAudio = { msgId ->
                                    viewModel.toggleAudioPlayback(msgId)
                                },
                                activeAudioId = uiState.activeAudioId,
                                isAudioPlaying = uiState.isAudioPlaying
                            )
                        }

                        is ScreenState.Main -> {
                            Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                bottomBar = {
                                    SawaBottomNavBar(
                                        currentTab = uiState.currentTab,
                                        onTabSelected = { tab -> viewModel.switchTab(tab) },
                                        isArabic = isArabic,
                                        unreadMatchesCount = uiState.matchedCandidates.size
                                    )
                                },
                                snackbarHost = { SnackbarHost(snackbarHostState) }
                            ) { innerPadding ->
                                when (uiState.currentTab) {
                                    MainNavigationTab.DISCOVER -> {
                                        DiscoveryScreen(
                                            currentCandidate = viewModel.currentTopCandidate(),
                                            remainingCount = uiState.filteredCandidates.size - uiState.currentCandidateIndex,
                                            language = uiState.language,
                                            boostActive = uiState.boostActive,
                                            boostsCount = uiState.boostsRemaining,
                                            logoStyle = uiState.appSettings.logoStyle,
                                            onFilterClick = { viewModel.openFilters() },
                                            onBoostClick = { viewModel.activateBoost() },
                                            onGoldClick = { viewModel.openGoldCenter() },
                                            onCardClick = { candidate -> viewModel.openCandidateDetail(candidate) },
                                            onToggleBlur = { candidate -> viewModel.toggleBlur(candidate) },
                                            onPass = { viewModel.passCurrentCandidate() },
                                            onInstantChat = { candidate, msg -> viewModel.instantChat(candidate, msg) },
                                            onLike = { viewModel.likeCurrentCandidate() },
                                            onSendRose = { candidate ->
                                                viewModel.sendRose(candidate)
                                                viewModel.showTopNotification(
                                                    iconEmoji = "🌹",
                                                    title = if (isArabic) "أرسلت باقة ورد VIP" else "Sent VIP Rose",
                                                    message = if (isArabic) "تم إهداء الوردة إلى ${candidate.name} بنجاح" else "Rose sent to ${candidate.nameEn}",
                                                    isGoldAlert = true
                                                )
                                            },
                                            onResetDiscovery = { viewModel.resetDiscovery() },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    MainNavigationTab.EXPLORE -> {
                                        CommunityScreen(
                                            candidates = uiState.candidates,
                                            language = uiState.language,
                                            userLatitude = uiState.userLatitude,
                                            userLongitude = uiState.userLongitude,
                                            isGpsActive = uiState.isGpsActive,
                                            onCandidateClick = { candidate -> viewModel.openCandidateDetail(candidate) },
                                            onLocationUpdated = { lat, lon, note ->
                                                viewModel.updateUserLocation(lat, lon, note)
                                            },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    MainNavigationTab.CHATS -> {
                                        ChatsScreen(
                                            matchedCandidates = uiState.matchedCandidates,
                                            allCandidates = uiState.candidates,
                                            language = uiState.language,
                                            chaperoneActive = uiState.currentUser.isChaperoneActive,
                                            onOpenChat = { candidate -> viewModel.openChat(candidate) },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }

                                    MainNavigationTab.PROFILE -> {
                                        MyProfileScreen(
                                            user = uiState.currentUser,
                                            language = uiState.language,
                                            onEditProfileClick = { viewModel.openEditProfile() },
                                            onOpenSubscriptions = { viewModel.openSubscriptions() },
                                            onOpenAdminDashboard = { viewModel.openAdminDashboard() },
                                            onOpenPrivacyPolicy = { viewModel.openPrivacyPolicy() },
                                            onOpenSelfieVerification = { viewModel.openSelfieVerification() },
                                            onTogglePhotoBlur = { viewModel.toggleUserPhotoBlur() },
                                            onToggleChaperone = { viewModel.toggleChaperone() },
                                            onToggleLanguage = { viewModel.toggleLanguage() },
                                            onUpdatePresence = { activeHours, isOnline ->
                                                viewModel.updateUserPresence(activeHours, isOnline)
                                            },
                                            onResetDiscovery = {
                                                viewModel.resetDiscovery()
                                                scope.launch {
                                                    snackbarHostState.showSnackbar(
                                                        if (isArabic) "تمت إعادة تعيين تصفح المرشحين بنجاح" else "Discovery reset successfully"
                                                    )
                                                }
                                            },
                                            onLogout = { viewModel.logout() },
                                            onBreakIce = { viewModel.breakChatIce() },
                                            onSendRose = {
                                                viewModel.showTopNotification(
                                                    iconEmoji = "🌹",
                                                    title = if (isArabic) "رصيد باقات الورد" else "Roses Balance",
                                                    message = if (isArabic) "لديك ${uiState.currentUser.rosesBalance} باقة ورد متاحة للإهداء والتجديد!" else "You have ${uiState.currentUser.rosesBalance} roses to gift or renew!",
                                                    durationMs = 3000L
                                                )
                                            },
                                            onSendHeart = { viewModel.sendVirtualHeart() },
                                            onActivateBoost = { viewModel.activateBoost() },
                                            onBuyFullBundle = { viewModel.purchaseFullBundle() },
                                            onInviteContactsSuccess = { count -> viewModel.onInviteContactsSuccess(count) },
                                            onUpdateGpsLocation = { lat, lng, city -> viewModel.onGpsLocationUpdated(lat, lng, city) },
                                            onSaveAudioBio = { duration -> viewModel.onSaveAudioBio(duration) },
                                            modifier = Modifier.padding(innerPadding)
                                        )
                                    }
                                }
                        }

                        // Floating In-App Top Notification HUD
                        TopNotificationHud(
                            notification = uiState.topNotification,
                            onDismiss = { viewModel.dismissTopNotification() }
                        )
                    }
                }
            }
        }
    }
}
}
}


