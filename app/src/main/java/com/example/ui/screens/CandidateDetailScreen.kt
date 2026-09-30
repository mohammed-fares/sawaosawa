package com.example.ui.screens

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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Height
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.components.ProfileBadge
import com.example.ui.components.RoseShowerOverlay
import com.example.ui.components.SwipeActionButtons
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CandidateDetailScreen(
    candidate: CandidateProfile,
    language: AppLanguage,
    onBack: () -> Unit,
    onPass: () -> Unit,
    onInstantChat: () -> Unit,
    onLike: () -> Unit,
    onToggleBlur: () -> Unit,
    onSendRose: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var showRoseShower by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = 90.dp)
            ) {
                // Header Image Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(430.dp)
                ) {
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

                    // Top Bar (مطابقة لصورة 8.webp: Rocket, Name, Back)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier
                                .testTag("detail_back_button")
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isArabic) candidate.name else candidate.nameEn,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            if (candidate.isVerified) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = RadiantGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Privacy Blur Toggle
                        IconButton(
                            onClick = onToggleBlur,
                            modifier = Modifier
                                .size(40.dp)
                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                        ) {
                            Icon(
                                imageVector = if (candidate.isPhotoBlurred) Icons.Default.Lock else Icons.Default.LockOpen,
                                contentDescription = "Privacy toggle",
                                tint = Color.White
                            )
                        }
                    }

                    // Sound badge on bottom right of photo
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(16.dp)
                            .size(38.dp)
                            .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VolumeUp,
                            contentDescription = "Voice note available",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Bottom image fade
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(70.dp)
                            .align(Alignment.BottomCenter)
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                                )
                            )
                    )
                }

                // Profile Details Body (مطابقة لصورة 8.webp)
                Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                    // Voice prompt player: "Adnan يقول" (مطابقة لصورة 4.webp)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = RadiantGoldContainer)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(PetroleumGreen, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (isArabic) "${candidate.name} يقول..." else "${candidate.nameEn} says...",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Text(
                                    text = "ılıll|llılı|l|ll||| (00:${candidate.audioDurationSec})",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = RadiantGoldDark
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 1. التعليم والوظيفة (مطابقة لصورة 8.webp)
                    Text(
                        text = if (isArabic) "التعليم والوظيفة 💼" else "Education & Career 💼",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileBadge(
                            text = candidate.education,
                            icon = Icons.Default.School,
                            backgroundColor = PetroleumGreenContainer,
                            textColor = PetroleumGreenDark
                        )
                        ProfileBadge(
                            text = candidate.profession,
                            icon = Icons.Default.Work,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 2. اللغات والأصل العرقي (مطابقة لصورة 8.webp)
                    Text(
                        text = if (isArabic) "اللغات والأصل العرقي 🌐" else "Languages & Ethnicity 🌐",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ProfileBadge(
                            text = candidate.ethnicity,
                            backgroundColor = RadiantGoldContainer,
                            textColor = RadiantGoldDark
                        )
                        ProfileBadge(
                            text = "اللغات: ${candidate.languages}",
                            icon = Icons.Default.Translate,
                            backgroundColor = MaterialTheme.colorScheme.surfaceVariant,
                            textColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // 3. الالتزام الديني ونمط الحياة
                    Text(
                        text = if (isArabic) "الدين والالتزام الشرعي 🕌" else "Faith & Religious Practice 🕌",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            InfoRowItem(
                                title = if (isArabic) "الالتزام الديني:" else "Religious practice:",
                                value = candidate.religiousPractice
                            )
                            InfoRowItem(
                                title = if (isArabic) "اللباس:" else "Dress:",
                                value = candidate.islamicDress
                            )
                            InfoRowItem(
                                title = if (isArabic) "المحافظة على الصلاة:" else "Prayers:",
                                value = candidate.prayersHabit
                            )
                            InfoRowItem(
                                title = if (isArabic) "الطعام الحلال:" else "Halal food:",
                                value = candidate.halalFood
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Bio
                    Text(
                        text = if (isArabic) "عن نفسي 📖" else "About Me 📖",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = candidate.bio,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    // Marriage Goals
                    Text(
                        text = if (isArabic) "هدفي من الزواج 💍" else "Marriage Goal 💍",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = candidate.marriageGoal,
                        fontSize = 14.sp,
                        lineHeight = 22.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }

            // Bottom Sticky Swipe Action Buttons
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding(),
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                SwipeActionButtons(
                    onPass = onPass,
                    onSendRose = {
                        showRoseShower = true
                        onSendRose()
                    },
                    onInstantChat = onInstantChat,
                    onLike = onLike
                )
            }

            RoseShowerOverlay(
                visible = showRoseShower,
                onFinished = { showRoseShower = false }
            )
        }
    }
}

@Composable
fun InfoRowItem(title: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = title, fontSize = 13.sp, color = Color.Gray)
        Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
    }
}
