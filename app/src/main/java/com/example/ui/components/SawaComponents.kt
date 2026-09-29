package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.NightlightRound
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.model.MainNavigationTab
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.MuzzPink
import com.example.ui.theme.MuzzPurpleChat
import com.example.ui.theme.PetroleumBrush
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@Composable
fun SawaSawaLogoBadge(
    modifier: Modifier = Modifier,
    isArabic: Boolean = true,
    showSubtitle: Boolean = true,
    darkStyle: Boolean = false
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        // Infinity loop emblem representing Halal marriage rings in Petroleum Green & Radiant Gold
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(
                    brush = Brush.linearGradient(listOf(PetroleumGreen, RadiantGold)),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = "سوا سوا",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column(horizontalAlignment = Alignment.Start) {
            Text(
                text = if (isArabic) "سوا سوا" else "SAWA SAWA",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 20.sp,
                color = if (darkStyle) Color.White else PetroleumGreen,
                letterSpacing = if (isArabic) 0.sp else 1.5.sp
            )
            if (showSubtitle) {
                Text(
                    text = if (isArabic) "زواج إسلامي حلال" else "Halal Muslim Marriage",
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = RadiantGoldDark
                )
            }
        }
    }
}

@Composable
fun SawaTopBar(
    isArabic: Boolean,
    onFilterClick: () -> Unit,
    onBoostClick: () -> Unit,
    onGoldClick: () -> Unit,
    onLanguageClick: () -> Unit,
    boostActive: Boolean = false,
    boostsCount: Int = 3,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        // Filters button
        IconButton(
            onClick = onFilterClick,
            modifier = Modifier
                .testTag("filter_button")
                .size(40.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = if (isArabic) "الفلاتر" else "Filters",
                tint = PetroleumGreen,
                modifier = Modifier.size(20.dp)
            )
        }

        // Center Logo
        SawaSawaLogoBadge(isArabic = isArabic, showSubtitle = true)

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Gold Center crown button
            IconButton(
                onClick = onGoldClick,
                modifier = Modifier
                    .testTag("gold_center_button")
                    .size(38.dp)
                    .background(RadiantGoldContainer, CircleShape)
            ) {
                Text(text = "👑", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Boost Rocket pill: "3 🚀" (مطابقة لصورة 1.webp وصورة 6.webp)
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(if (boostActive) RadiantGold else PetroleumGreen)
                    .clickable { onBoostClick() }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "$boostsCount",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.RocketLaunch,
                    contentDescription = "Boost",
                    tint = Color.White,
                    modifier = Modifier.size(15.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Language switch button
            IconButton(
                onClick = onLanguageClick,
                modifier = Modifier
                    .testTag("language_toggle_button")
                    .size(34.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
            ) {
                Text(
                    text = if (isArabic) "EN" else "عربي",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = PetroleumGreen
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CandidateCard(
    candidate: CandidateProfile,
    isArabic: Boolean,
    onCardClick: () -> Unit,
    onToggleBlur: () -> Unit,
    onPass: () -> Unit,
    onInstantChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    CardContainer(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(26.dp))
            .clickable { onCardClick() }
            .shadow(8.dp, RoundedCornerShape(26.dp))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Candidate Photo with Blur Option
            Image(
                painter = painterResource(id = candidate.photoRes),
                contentDescription = candidate.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (candidate.isPhotoBlurred) Modifier.blur(28.dp) else Modifier
                    )
            )

            // Blurred Private Photo Action Overlay (مطابقة لصورة 6.webp)
            if (candidate.isPhotoBlurred) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.35f)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Request photo reveal (white round button with red camera)
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(Color.White)
                                    .clickable { onToggleBlur() }
                                    .shadow(6.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PhotoCamera,
                                    contentDescription = "Request reveal",
                                    tint = MuzzPink,
                                    modifier = Modifier.size(32.dp)
                                )
                            }

                            // Pass / Block (pink round button with cancel icon)
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .clip(CircleShape)
                                    .background(MuzzPink)
                                    .clickable { onPass() }
                                    .shadow(6.dp, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Block,
                                    contentDescription = "Block / Pass",
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Box(
                            modifier = Modifier
                                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isArabic) "صورة خاصة • انقر لطلب الكشف" else "Private photo • Tap to request",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Dark gradient overlay at bottom
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.25f),
                                Color.Black.copy(alpha = 0.90f)
                            ),
                            startY = 350f
                        )
                    )
            )

            // Top Badges
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Verified Badge
                if (candidate.isVerified) {
                    Row(
                        modifier = Modifier
                            .background(Color.Black.copy(alpha = 0.55f), RoundedCornerShape(20.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "موثّق" else "Verified",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Blur toggle
                IconButton(
                    onClick = onToggleBlur,
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (candidate.isPhotoBlurred) Icons.Default.Lock else Icons.Default.LockOpen,
                        contentDescription = "Toggle blur",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Bottom Profile Info
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 18.dp)
            ) {
                // Name & Age & Flag (مطابقة لصورة 1.webp و 5.webp)
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "${candidate.age}",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Normal,
                        color = Color.White.copy(alpha = 0.9f)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isArabic) candidate.name else candidate.nameEn,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    if (candidate.isVerified) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = RadiantGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Location & Distance
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(top = 3.dp, bottom = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = RadiantGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isArabic)
                            "${candidate.distanceKm} كيلومتر، ${candidate.city} 🇦🇪"
                        else
                            "${candidate.distanceKm} km, ${candidate.cityEn} 🇦🇪",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 13.sp
                    )
                }

                // Badges Row: "ذهبي Gold 👑", "طبيب", "ملتزم دينياً 🌙"
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Gold Member Badge (شعار ذهبي Gold 👑)
                    if (candidate.isGoldMember) {
                        ProfileBadge(
                            text = if (isArabic) "ذهبي Gold 👑" else "Gold Member 👑",
                            backgroundColor = RadiantGold,
                            textColor = Color(0xFF261D05)
                        )
                    }

                    ProfileBadge(
                        text = if (isArabic) candidate.profession else candidate.professionEn,
                        icon = Icons.Default.Work,
                        backgroundColor = Color.Black.copy(alpha = 0.6f),
                        textColor = Color.White
                    )

                    ProfileBadge(
                        text = if (isArabic) candidate.religiousPractice else candidate.religiousPracticeEn,
                        backgroundColor = PetroleumGreen.copy(alpha = 0.85f),
                        textColor = Color.White
                    )

                    if (candidate.isPhotoBlurred) {
                        ProfileBadge(
                            text = if (isArabic) "صور خاصة 🔒" else "Private Photos 🔒",
                            backgroundColor = Color(0xFF880E4F).copy(alpha = 0.7f),
                            textColor = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Profile Audio Prompt (مطابقة لصورة 4.webp: "Adnan يقول" مع موجة صوتية)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play voice note",
                            tint = PetroleumGreenDark,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = if (isArabic)
                            "${candidate.name} يقول عن أهدافه (00:${candidate.audioDurationSec})"
                        else
                            "${candidate.nameEn} says about marriage (00:${candidate.audioDurationSec})",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    Text(
                        text = " ılılıllı ",
                        color = RadiantGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun CardContainer(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Box(modifier = modifier) {
        content()
    }
}

@Composable
fun ProfileBadge(
    text: String,
    icon: ImageVector? = null,
    backgroundColor: Color,
    textColor: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .padding(horizontal = 9.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(13.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
        }
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SwipeActionButtons(
    onPass: () -> Unit,
    onInstantChat: () -> Unit,
    onLike: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Pass Button (X in gray/petroleum)
        IconButton(
            onClick = onPass,
            modifier = Modifier
                .testTag("pass_button")
                .size(54.dp)
                .background(Color(0xFF263238), CircleShape)
                .shadow(4.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Pass",
                tint = Color.White,
                modifier = Modifier.size(26.dp)
            )
        }

        // Instant Chat Button (Purple / Instant Icebreaker)
        IconButton(
            onClick = onInstantChat,
            modifier = Modifier
                .testTag("instant_chat_button")
                .size(50.dp)
                .background(MuzzPurpleChat, CircleShape)
                .shadow(4.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.ChatBubble,
                contentDescription = "Instant Chat",
                tint = Color.White,
                modifier = Modifier.size(22.dp)
            )
        }

        // Like Button (Heart Pink / Radiant Gold border)
        IconButton(
            onClick = onLike,
            modifier = Modifier
                .testTag("like_button")
                .size(62.dp)
                .background(MuzzPink, CircleShape)
                .border(2.dp, RadiantGold, CircleShape)
                .shadow(6.dp, CircleShape)
        ) {
            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = "Like",
                tint = Color.White,
                modifier = Modifier.size(32.dp)
            )
        }
    }
}

@Composable
fun SawaBottomNavBar(
    currentTab: MainNavigationTab,
    onTabSelected: (MainNavigationTab) -> Unit,
    isArabic: Boolean,
    unreadMatchesCount: Int = 0,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        shadowElevation = 12.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Tab 1: Ring icon for Discovery / Matrimonial match (مطابقة لصورة 1.webp و 2.webp)
            BottomNavItem(
                icon = Icons.Default.AllInclusive,
                label = if (isArabic) "التوافق" else "Match",
                isSelected = currentTab == MainNavigationTab.DISCOVER,
                onClick = { onTabSelected(MainNavigationTab.DISCOVER) },
                testTag = "nav_discover"
            )

            // Tab 2: Explore
            BottomNavItem(
                icon = Icons.Default.Explore,
                label = if (isArabic) "استكشف" else "Explore",
                isSelected = currentTab == MainNavigationTab.EXPLORE,
                onClick = { onTabSelected(MainNavigationTab.EXPLORE) },
                testTag = "nav_explore"
            )

            // Tab 3: Chats / Messages
            BottomNavItem(
                icon = Icons.Default.ChatBubble,
                label = if (isArabic) "المحادثات" else "Chats",
                isSelected = currentTab == MainNavigationTab.CHATS,
                badgeCount = unreadMatchesCount,
                onClick = { onTabSelected(MainNavigationTab.CHATS) },
                testTag = "nav_chats"
            )

            // Tab 4: Profile
            BottomNavItem(
                icon = Icons.Default.Person,
                label = if (isArabic) "حسابي" else "Profile",
                isSelected = currentTab == MainNavigationTab.PROFILE,
                onClick = { onTabSelected(MainNavigationTab.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
fun BottomNavItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String,
    badgeCount: Int = 0
) {
    Column(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BadgedBox(
            badge = {
                if (badgeCount > 0) {
                    Badge(
                        containerColor = MuzzPink,
                        contentColor = Color.White
                    ) {
                        Text(text = "$badgeCount", fontSize = 10.sp)
                    }
                }
            }
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isSelected) PetroleumGreen else Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) PetroleumGreen else Color.Gray
        )
    }
}

@Composable
fun ChaperoneBadgeBanner(
    isArabic: Boolean,
    chaperoneName: String,
    onNoticeClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onNoticeClick() }
            .background(PetroleumGreenContainer, RoundedCornerShape(12.dp))
            .border(1.dp, PetroleumGreen.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Shield,
            contentDescription = null,
            tint = PetroleumGreen,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = if (isArabic) "ميزة الولي / المرافق مفعلة 🛡️" else "Chaperone / Wali Active 🛡️",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = PetroleumGreenDark
            )
            Text(
                text = if (isArabic)
                    "المحادثة خاضعة لمتابعة: $chaperoneName (انقر للتفاصيل)"
                else
                    "Monitored by: $chaperoneName (tap for details)",
                fontSize = 10.sp,
                color = PetroleumGreenDark.copy(alpha = 0.8f)
            )
        }
    }
}
