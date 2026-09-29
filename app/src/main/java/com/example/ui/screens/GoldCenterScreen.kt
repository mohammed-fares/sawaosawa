package com.example.ui.screens

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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForwardIos
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CurrentUserProfile
import com.example.ui.theme.GoldShineBrush
import com.example.ui.theme.PetroleumBrush
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import com.example.ui.theme.RadiantGoldContainer
import com.example.ui.theme.RadiantGoldDark

@Composable
fun GoldCenterScreen(
    user: CurrentUserProfile,
    language: AppLanguage,
    onClose: () -> Unit,
    onActivateBoost: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .verticalScroll(rememberScrollState())
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(
                    onClick = onClose,
                    modifier = Modifier.testTag("gold_close_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }

                Text(
                    text = if (isArabic) "مركز Gold" else "Gold Center",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                Spacer(modifier = Modifier.size(40.dp))
            }

            // Top Gold Membership Card (مطابقة لصورة 10.webp)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 8.dp)
                    .shadow(10.dp, RoundedCornerShape(22.dp)),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(GoldShineBrush)
                        .padding(22.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = user.name,
                                    fontSize = 28.sp,
                                    fontWeight = FontWeight.Black,
                                    color = Color(0xFF261D05)
                                )
                                Text(
                                    text = if (isArabic) "عضو ذهبي GOLD 👑" else "GOLD MEMBER 👑",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF574106)
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .size(52.dp)
                                    .background(Color.White.copy(alpha = 0.35f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Stars,
                                    contentDescription = null,
                                    tint = Color(0xFF382903),
                                    modifier = Modifier.size(34.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.White,
                                contentColor = PetroleumGreenDark
                            ),
                            shape = RoundedCornerShape(24.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Text(
                                text = if (isArabic) "إدارة العضوية" else "Manage Membership",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2x2 Features Grid (مطابقة لصورة 10.webp)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Feature 1: Instant Chat
                    GoldFeatureCard(
                        count = "12",
                        title = if (isArabic) "دردشة فورية" else "Instant Chat",
                        description = if (isArabic) "محادثتك الفورية اليومية هنا" else "Your daily instant icebreakers",
                        actionLabel = if (isArabic) "شراء المزيد" else "Buy More",
                        actionColor = PetroleumGreenDark,
                        onClick = {},
                        modifier = Modifier.weight(1f)
                    )

                    // Feature 2: Profile Boost
                    GoldFeatureCard(
                        count = "3",
                        title = if (isArabic) "تعزيز للملف الشخصي" else "Profile Boost",
                        description = if (isArabic) "ينتهي التعزيز بحلول 6:22:57 🚀" else "Boost ends at 6:22:57 🚀",
                        actionLabel = if (isArabic) "تفعيل التعزيز" else "Activate Boost",
                        actionColor = PetroleumGreen,
                        onClick = onActivateBoost,
                        modifier = Modifier.weight(1f)
                    )
                }

                // Feature 3: Second Chance
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = null,
                                    tint = RadiantGold,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = if (isArabic) "فرصة ثانية" else "Second Chance",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic)
                                    "شاهد واجعلهم يشاهدونك من خلال فرصة ثانية كل شهر للملفات التي تم تخطيها."
                                else
                                    "Rewind and get a second chance with profiles you passed.",
                                fontSize = 11.sp,
                                color = Color.Gray,
                                lineHeight = 16.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {},
                            colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen),
                            shape = RoundedCornerShape(20.dp)
                        ) {
                            Text(
                                text = if (isArabic) "جرّب الآن" else "Try Now",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Feature 4: Stand out with Gold badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = RadiantGoldContainer)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(18.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(RadiantGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "👑", fontSize = 20.sp)
                        }
                        Spacer(modifier = Modifier.width(14.dp))
                        Column {
                            Text(
                                text = if (isArabic) "تميّز بالشارة الذهبية" else "Stand Out With Gold",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = RadiantGoldDark
                            )
                            Text(
                                text = if (isArabic)
                                    "تميّز عن الآخرين، شارة ذهبية ملكية تزيد من معدل التوافق والاهتمام"
                                else
                                    "A regal golden badge that dramatically increases mutual match rates",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun GoldFeatureCard(
    count: String,
    title: String,
    description: String,
    actionLabel: String,
    actionColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text = count,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                color = PetroleumGreen
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )

            Text(
                text = description,
                fontSize = 10.sp,
                color = Color.Gray,
                modifier = Modifier.padding(vertical = 4.dp),
                maxLines = 2,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = actionColor),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier.fillMaxWidth().height(36.dp)
            ) {
                Text(
                    text = actionLabel,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
