package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import coil.compose.AsyncImage
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CurrentUserProfile
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@Composable
fun MyProfileScreen(
    user: CurrentUserProfile,
    language: AppLanguage,
    onEditProfileClick: () -> Unit,
    onOpenSubscriptions: () -> Unit,
    onOpenAdminDashboard: () -> Unit,
    onOpenPrivacyPolicy: () -> Unit,
    onOpenSelfieVerification: () -> Unit,
    onTogglePhotoBlur: () -> Unit,
    onToggleChaperone: () -> Unit,
    onToggleLanguage: () -> Unit,
    onUpdatePresence: (activeHours: String, isOnline: Boolean) -> Unit,
    onResetDiscovery: () -> Unit,
    onLogout: () -> Unit,
    onBreakIce: () -> Unit = {},
    onSendRose: () -> Unit = {},
    onSendHeart: () -> Unit = {},
    onActivateBoost: () -> Unit = {},
    onBuyFullBundle: () -> Unit = {},
    onInviteContactsSuccess: (Int) -> Unit = {},
    onUpdateGpsLocation: (Double, Double, String) -> Unit = { _, _, _ -> },
    onSaveAudioBio: (String) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var showLogoutDialog by remember { mutableStateOf(false) }

    var showAudioBioDialog by remember { mutableStateOf(false) }
    var isRecordingBio by remember { mutableStateOf(false) }
    var bioSeconds by remember { androidx.compose.runtime.mutableIntStateOf(0) }
    var isPlayingBio by remember { mutableStateOf(false) }

    val contactsPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            onInviteContactsSuccess(28)
        }
    }

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { perms ->
        val granted = perms[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                perms[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (granted) {
            onUpdateGpsLocation(30.0444, 31.2357, if (isArabic) "القاهرة، المعادي" else "Cairo, Maadi")
        }
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            showAudioBioDialog = true
        }
    }

    androidx.compose.runtime.LaunchedEffect(isRecordingBio) {
        if (isRecordingBio) {
            bioSeconds = 0
            while (isRecordingBio && bioSeconds < 30) {
                kotlinx.coroutines.delay(1000)
                bioSeconds++
            }
            if (isRecordingBio && bioSeconds >= 30) {
                isRecordingBio = false
            }
        }
    }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 80.dp)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isArabic) "ملفي الشخصي" else "My Profile",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Admin Quick Shortcut
                    IconButton(
                        onClick = onOpenAdminDashboard,
                        modifier = Modifier
                            .testTag("admin_shortcut_button")
                            .size(38.dp)
                            .background(PetroleumGreenDark, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin",
                            tint = RadiantGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // User Photo & Name
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(CircleShape)
                        .border(3.5.dp, RadiantGold, CircleShape)
                        .clickable { onEditProfileClick() }
                ) {
                    val activePhoto = if (user.photoUris.isNotEmpty() && user.selectedPhotoIndex in user.photoUris.indices) {
                        user.photoUris[user.selectedPhotoIndex]
                    } else null

                    if (activePhoto != null && (activePhoto.startsWith("content://") || activePhoto.startsWith("file://") || activePhoto.startsWith("http"))) {
                        AsyncImage(
                            model = activePhoto,
                            contentDescription = user.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (user.isPhotoBlurred) Modifier.blur(20.dp) else Modifier)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.profile_sarah),
                            contentDescription = user.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .then(if (user.isPhotoBlurred) Modifier.blur(20.dp) else Modifier)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .align(Alignment.BottomEnd)
                            .background(PetroleumGreen, CircleShape)
                            .border(2.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit photo",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${user.name}، ${user.age}",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Icon(
                        imageVector = Icons.Default.Verified,
                        contentDescription = "Verified",
                        tint = RadiantGold,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Text(
                    text = "${user.profession} • ${user.city}",
                    fontSize = 13.sp,
                    color = Color.Gray,
                    modifier = Modifier.padding(top = 2.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Primary CTA: Edit Profile Button
                Button(
                    onClick = onEditProfileClick,
                    modifier = Modifier
                        .testTag("edit_profile_button")
                        .fillMaxWidth()
                        .height(46.dp),
                    shape = RoundedCornerShape(23.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Icon(imageVector = Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تعديل الملف الشخصي والبيانات" else "Edit Profile Details",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Gold VIP Subscription Banner (Card with direct click to Subscriptions)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .clickable { onOpenSubscriptions() },
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GoldShineBrush)
                            .padding(horizontal = 16.dp, vertical = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = "👑", fontSize = 22.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isArabic) "عضوية سوا سوا GOLD VIP" else "Sawa Sawa GOLD VIP",
                                        fontWeight = FontWeight.Black,
                                        fontSize = 14.sp,
                                        color = Color(0xFF2E2204)
                                    )
                                    Text(
                                        text = "${user.subscriptionPrice} • ينتهي ${user.subscriptionExpiresAt}",
                                        fontSize = 11.sp,
                                        color = Color(0xFF574106)
                                    )
                                }
                            }

                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
                                contentDescription = null,
                                tint = Color(0xFF382903),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Profile Completion Gauge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = PetroleumGreenContainer)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = if (isArabic) "اكتمال الملف الشخصي" else "Profile Completion",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreenDark
                            )
                            Text(
                                text = "${user.completionPercentage}%",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = PetroleumGreenDark
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        LinearProgressIndicator(
                            progress = { user.completionPercentage / 100f },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp)),
                            color = PetroleumGreen,
                            trackColor = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // SECTION: Aziz Perks & Balances Card (رصيد الامتيازات والباقات)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, RadiantGold.copy(alpha = 0.7f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "👑", fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "رصيد الامتيازات الخاصة (عزيز) والباقات" else "Aziz Perks & Balances",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = if (isArabic) "رصيدك المتاح من كسر الجليد، الورود، والقلوب" else "Icebreakers, roses, super hearts & boosts",
                                    fontSize = 11.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // 4-Balance Metric Grid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. كسر الدردشة
                        PerkBalanceBadge(
                            iconEmoji = "⚡",
                            title = if (isArabic) "كسر الدردشة" else "Icebreaker",
                            count = "${user.instantChatsRemaining}",
                            actionText = if (isArabic) "استخدام" else "Use",
                            onClick = onBreakIce,
                            modifier = Modifier.weight(1f)
                        )

                        // 2. إرسال الورود
                        PerkBalanceBadge(
                            iconEmoji = "🌹",
                            title = if (isArabic) "باقات الورد" else "Roses",
                            count = "${user.rosesBalance}",
                            actionText = if (isArabic) "إهداء" else "Gift",
                            onClick = onSendRose,
                            modifier = Modifier.weight(1f)
                        )

                        // 3. علامة القلب
                        PerkBalanceBadge(
                            iconEmoji = "💖",
                            title = if (isArabic) "علامة القلب" else "Hearts",
                            count = "${user.heartsBalance}",
                            actionText = if (isArabic) "إرسال" else "Send",
                            onClick = onSendHeart,
                            modifier = Modifier.weight(1f)
                        )

                        // 4. التعزيز
                        PerkBalanceBadge(
                            iconEmoji = "🚀",
                            title = if (isArabic) "تعزيز الملف" else "Boosts",
                            count = "${user.boostsRemaining}",
                            actionText = if (isArabic) "إطلاق" else "Launch",
                            onClick = onActivateBoost,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Full Bundle Package Promotion Button (نظام الباقة الكاملة)
                    Button(
                        onClick = onBuyFullBundle,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(14.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = RadiantGoldDark,
                            contentColor = Color.White
                        )
                    ) {
                        Icon(imageVector = Icons.Default.CardGiftcard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "تفعيل باقة الامتيازات الشاملة (VIP Bundle) 🎁" else "Activate Full VIP Perks Bundle 🎁",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // SECTION: Legal Device Permissions & Integrations (الصلاحيات القانونية والتواصل)
            Text(
                text = if (isArabic) "الصلاحيات القانونية وخدمات الهاتف 📱" else "Permissions & System Integration 📱",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Permission 1: Contacts Invitation
                    ProfileMenuRow(
                        title = if (isArabic) "دعوة جهات الاتصال وكسب مكافآت 🎁" else "Invite Contacts & Earn Rewards 🎁",
                        subtitle = if (isArabic) "صلاحية دفتر الهاتف لإرسال دعوات واكتساب 10 ورود وتعزيز مجاني" else "Read contacts to invite friends and earn bonus roses",
                        icon = Icons.Default.Contacts,
                        onClick = {
                            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS)
                        }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Permission 2: GPS Location
                    ProfileMenuRow(
                        title = if (isArabic) "تحديث الموقع الجغرافي الدقيق (GPS) 📍" else "Update Accurate GPS Location 📍",
                        subtitle = if (isArabic) "صلاحية تحديد المكان لحساب أقرب التوافقات في منطقتك (${user.locationCity})" else "Location permission for nearest candidate matching (${user.locationCity})",
                        icon = Icons.Default.LocationOn,
                        onClick = {
                            locationPermissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Permission 3: Audio Bio Recording (Microphone)
                    ProfileMenuRow(
                        title = if (isArabic) "تسجيل صوتي تعريفي للملف الشخصي (30 ثانية) 🎙️" else "Record Audio Bio (30s max) 🎙️",
                        subtitle = if (user.bioAudioDuration != null) {
                            if (isArabic) "تم حفظ تسجيلك التعريفي (${user.bioAudioDuration}) - اضغط للاستماع أو التسجيل مجدداً" else "Audio bio saved (${user.bioAudioDuration}) - tap to update or play"
                        } else {
                            if (isArabic) "صلاحية الميكروفون لتسجيل نبذة صوتية تظهر في ملفك الشخصي" else "Mic permission to add a voice bio to your profile"
                        },
                        icon = Icons.Default.Mic,
                        onClick = {
                            micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Navigation Menu Options
            Text(
                text = if (isArabic) "الحساب والأدوات" else "Account & Tools",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)) {
                    // Option 1: Selfie Verification
                    ProfileMenuRow(
                        title = if (isArabic) "سلفي توثيق الهوية" else "Selfie Verification",
                        subtitle = if (isArabic) "توثيق حسابك بالشارة الذهبية" else "Verify account with gold badge",
                        icon = Icons.Default.CameraAlt,
                        onClick = onOpenSelfieVerification
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Option 2: Subscription Plans
                    ProfileMenuRow(
                        title = if (isArabic) "باقات الاشتراك والدفع" else "Subscriptions & Pricing",
                        subtitle = if (isArabic) "إدارة الخطط الأسبوعية والشهرية والسنوية" else "Manage weekly, monthly & annual plans",
                        icon = Icons.Default.CardMembership,
                        onClick = onOpenSubscriptions
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Option 3: Admin Dashboard
                    ProfileMenuRow(
                        title = if (isArabic) "لوحة التحكم والإدارة (Admin)" else "Admin Dashboard",
                        subtitle = if (isArabic) "التحكم بالأعضاء، الرقابة، والأسعار" else "Manage members, moderation & campaigns",
                        icon = Icons.Default.AdminPanelSettings,
                        onClick = onOpenAdminDashboard
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Option 4: Privacy Policy & Terms
                    ProfileMenuRow(
                        title = if (isArabic) "سياسة الخصوصية والضوابط الشرعية" else "Privacy Policy & Halal Terms",
                        subtitle = if (isArabic) "ميثاق الأمانة والخصوصية الشرعية" else "Terms of service and data protection",
                        icon = Icons.Default.MenuBook,
                        onClick = onOpenPrivacyPolicy
                    )

                    Divider(color = Color.LightGray.copy(alpha = 0.3f))

                    // Option 5: Reset Discovery
                    ProfileMenuRow(
                        title = if (isArabic) "إعادة تصفح جميع المرشحين" else "Reset Swiped Candidates",
                        subtitle = if (isArabic) "إعادة إظهار الملفات التي تم تخطيها" else "Reset candidate card feed",
                        icon = Icons.Default.Refresh,
                        onClick = onResetDiscovery
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Privacy & Halal Safety Toggles
            Text(
                text = if (isArabic) "الخصوصية السريعة والولي 🛡️" else "Quick Privacy & Wali 🛡️",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Photo blur toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Lock, contentDescription = null, tint = PetroleumGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "إخفاء صوري (صور خاصة)" else "Blur My Photos",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (isArabic) "تبقى صورك مموهة للعامة" else "Keeps photos blurred to public",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Switch(
                            checked = user.isPhotoBlurred,
                            onCheckedChange = { onTogglePhotoBlur() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PetroleumGreen
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    // Chaperone toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = PetroleumGreen, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isArabic) "إشراف الولي الشرعي (Wali)" else "Chaperone / Wali Active",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (isArabic) "مشاركة نسخة المحادثات مع: ${user.chaperoneEmail}" else "Forwarding to: ${user.chaperoneEmail}",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Switch(
                            checked = user.isChaperoneActive,
                            onCheckedChange = { onToggleChaperone() },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PetroleumGreen
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 1: App Presence & Available Hours (أوقات التواجد على التطبيق)
            Text(
                text = if (isArabic) "أوقات التواجد على التطبيق 🕒" else "App Presence & Available Hours 🕒",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Online presence toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .background(if (user.isOnlineNow) Color(0xFF4CAF50) else Color.Gray, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (user.isOnlineNow) {
                                        if (isArabic) "متواجدة حالياً (Online)" else "Currently Online"
                                    } else {
                                        if (isArabic) "غير متصلة حالياً" else "Offline"
                                    },
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = if (isArabic) "إظهار النقطة الخضراء للآخرين أثناء تصفحك" else "Display green online badge to candidates",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )
                        }

                        Switch(
                            checked = user.isOnlineNow,
                            onCheckedChange = { onUpdatePresence(user.activeHours, it) },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = PetroleumGreen
                            )
                        )
                    }

                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.3f))

                    // Presence Hours schedule
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = RadiantGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isArabic) "ساعات التواجد المعتادة:" else "Usual Active Hours:",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Text(
                        text = user.activeHours,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreenDark,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )

                    // Quick schedule selector chips
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf(
                            "طوال اليوم",
                            "مساءً (7-11 م)",
                            "عصراً (3-7 م)"
                        ).forEach { preset ->
                            val actualValue = when (preset) {
                                "طوال اليوم" -> "طوال اليوم (متاحة دائماً)"
                                "مساءً (7-11 م)" -> "مساءً (من 7:00 م إلى 11:00 م)"
                                else -> "عصراً (من 3:00 م إلى 7:00 م)"
                            }
                            FilterChip(
                                selected = user.activeHours.contains(preset),
                                onClick = { onUpdatePresence(actualValue, user.isOnlineNow) },
                                label = { Text(preset, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = PetroleumGreen,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 2: Account Language Selection (خيارات المستخدم فى الحساب)
            Text(
                text = if (isArabic) "لغة التطبيق (خيارات الحساب) 🌐" else "App Language (Account Settings) 🌐",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreen,
                modifier = Modifier.padding(horizontal = 20.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Arabic Language Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isArabic) PetroleumGreen else Color.White)
                            .clickable {
                                if (!isArabic) onToggleLanguage()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🇸🇦", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "العربية",
                                fontWeight = FontWeight.Bold,
                                color = if (isArabic) Color.White else PetroleumGreenDark,
                                fontSize = 14.sp
                            )
                        }
                    }

                    // English Language Option
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isArabic) PetroleumGreen else Color.White)
                            .clickable {
                                if (isArabic) onToggleLanguage()
                            }
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "🇬🇧", fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "English",
                                fontWeight = FontWeight.Bold,
                                color = if (!isArabic) Color.White else PetroleumGreenDark,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Section 3: GPS Location & Proximity Information
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = RadiantGoldContainer.copy(alpha = 0.35f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.NearMe,
                        contentDescription = null,
                        tint = PetroleumGreenDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isArabic) "الموقع الجغرافي: ${user.locationCity}" else "Location: ${user.locationCity}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = PetroleumGreenDark
                        )
                        Text(
                            text = if (isArabic)
                                "مُفعل عبر Google Play Services Location لحساب الأقرب إليك"
                            else
                                "Enabled via Google Play Services Location for proximity match",
                            fontSize = 11.sp,
                            color = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Logout Button
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    modifier = Modifier
                        .testTag("logout_button")
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ExitToApp, contentDescription = null, tint = Color(0xFFD32F2F))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تسجيل الخروج من الحساب" else "Log Out",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = {
                Text(
                    text = if (isArabic) "تسجيل الخروج؟" else "Log Out?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "هل ترغب بالفعل في تسجيل الخروج من تطبيق سوا سوا؟"
                    else
                        "Are you sure you want to log out of Sawa Sawa?",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD32F2F))
                ) {
                    Text(if (isArabic) "نعم، خروج" else "Yes, Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel", color = Color.Gray)
                }
            }
        )
    }

    // Audio Bio Recording & Playback Modal Dialog
    if (showAudioBioDialog) {
        AlertDialog(
            onDismissRequest = {
                isRecordingBio = false
                showAudioBioDialog = false
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎙️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "تسجيل النبذة الصوتية (30 ثانية)" else "Record Audio Bio (30s)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = PetroleumGreenDark
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isArabic)
                            "تحدث بنبرة واضحة ومحتشمة عن طموحاتك ومواصفات شريك الحياة المنشود."
                        else
                            "Speak clearly and modestly about your aspirations and spouse preferences.",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Timer & Waveform Box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isRecordingBio) Color(0xFF1E282D) else PetroleumGreenContainer)
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                if (isRecordingBio) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .background(Color(0xFFE53935), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                }
                                Text(
                                    text = "00:${String.format("%02d", bioSeconds)} / 00:30",
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Black,
                                    color = if (isRecordingBio) Color.White else PetroleumGreenDark
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = if (isRecordingBio) "ılıll|llılı|l|ll|llılı" else "•••••••••••••••••",
                                fontSize = 16.sp,
                                color = if (isRecordingBio) RadiantGold else PetroleumGreen,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Record / Stop Trigger Button
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (!isRecordingBio) {
                            Button(
                                onClick = { isRecordingBio = true },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE53935)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Mic, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "بدء التسجيل" else "Start Recording", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        } else {
                            Button(
                                onClick = { isRecordingBio = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E2E2E)),
                                shape = RoundedCornerShape(20.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Stop, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(if (isArabic) "إيقاف التسجيل" else "Stop", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val durationStr = String.format("00:%02d", bioSeconds.coerceAtLeast(5))
                        onSaveAudioBio(durationStr)
                        showAudioBioDialog = false
                    },
                    enabled = bioSeconds > 0 && !isRecordingBio,
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(if (isArabic) "حفظ في الملف الشخصي ✓" else "Save to Profile ✓", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    isRecordingBio = false
                    showAudioBioDialog = false
                }) {
                    Text(if (isArabic) "إلغاء" else "Cancel", color = Color.Gray)
                }
            },
            shape = RoundedCornerShape(22.dp)
        )
    }
}

@Composable
fun PerkBalanceBadge(
    iconEmoji: String,
    title: String,
    count: String,
    actionText: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.35f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = iconEmoji, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = count,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black,
                color = PetroleumGreenDark
            )
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(PetroleumGreenContainer)
                    .clickable { onClick() }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = actionText,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreenDark
                )
            }
        }
    }
}

@Composable
fun ProfileMenuRow(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PetroleumGreenContainer, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PetroleumGreenDark,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }
        }

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier.size(14.dp)
        )
    }
}
