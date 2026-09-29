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
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var showLogoutDialog by remember { mutableStateOf(false) }

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
                    Image(
                        painter = painterResource(id = R.drawable.profile_sarah),
                        contentDescription = user.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .then(
                                if (user.isPhotoBlurred) Modifier.blur(20.dp) else Modifier
                            )
                    )

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
