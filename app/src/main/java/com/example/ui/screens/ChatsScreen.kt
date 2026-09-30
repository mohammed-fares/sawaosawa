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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.theme.*
import com.example.ui.components.VipBadgeOverlay

@Composable
fun ChatsScreen(
    matchedCandidates: List<CandidateProfile>,
    allCandidates: List<CandidateProfile>,
    language: AppLanguage,
    chaperoneActive: Boolean,
    onOpenChat: (CandidateProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC

    val candidatesToShow = if (matchedCandidates.isNotEmpty()) matchedCandidates else allCandidates

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header Bar (مطابقة لصورة 2.webp: "محادثاتك")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (isArabic) "محادثاتك" else "Your Chats",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )

                if (chaperoneActive) {
                    Row(
                        modifier = Modifier
                            .background(PetroleumGreenContainer, RoundedCornerShape(16.dp))
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = PetroleumGreen,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (isArabic) "الولي مفعل" else "Wali active",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreenDark
                        )
                    }
                }
            }

            // Halal Serious Marriage 5-chats limit banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 18.dp, vertical = 4.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isArabic) "المحادثات النشطة (${candidatesToShow.take(3).size}/5) 🔒" else "Active Chats (${candidatesToShow.take(3).size}/5) 🔒",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = PetroleumGreen
                        )
                        Text(
                            text = if (isArabic) "محادثة جادة وهادفة" else "Serious & Halal",
                            fontSize = 10.sp,
                            color = RadiantGoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isArabic)
                            "تحدث إلى 5 أشخاص فقط في وقت واحد لضمان الجدية والتركيز التام على الزواج الحلال."
                        else
                            "Chat with up to 5 people at once to ensure seriousness and focus on halal marriage.",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // New Matches Horizontal Carousel ("توافقات جديدة")
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
                Text(
                    text = if (isArabic) "توافقات جديدة ✨" else "New Matches ✨",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )

                androidx.compose.foundation.lazy.LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(allCandidates) { candidate ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { onOpenChat(candidate) }
                                .padding(vertical = 4.dp)
                        ) {
                            Box(modifier = Modifier.size(56.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape)
                                        .border(2.dp, RadiantGold, CircleShape)
                                ) {
                                    Image(
                                        painter = painterResource(id = candidate.photoRes),
                                        contentDescription = candidate.name,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }

                                if (candidate.isVip) {
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomCenter)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(RadiantGold)
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text("VIP", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) candidate.name else candidate.nameEn,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Conversations List Header
            Text(
                text = if (isArabic) "المحادثات الجارية" else "Recent Conversations",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            // Conversations List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                items(candidatesToShow) { candidate ->
                    ConversationItemView(
                        candidate = candidate,
                        isArabic = isArabic,
                        onClick = { onOpenChat(candidate) }
                    )
                    Divider(
                        color = Color.LightGray.copy(alpha = 0.2f),
                        modifier = Modifier.padding(horizontal = 20.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ConversationItemView(
    candidate: CandidateProfile,
    isArabic: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(54.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .border(2.dp, RadiantGold, CircleShape)
            ) {
                Image(
                    painter = painterResource(id = candidate.photoRes),
                    contentDescription = candidate.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Green online dot
            if (candidate.isOnlineNow) {
                Box(
                    modifier = Modifier
                        .size(13.dp)
                        .align(Alignment.BottomEnd)
                        .background(Color(0xFF4CAF50), CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )
            }
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (isArabic) candidate.name else candidate.nameEn,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (candidate.isVerified) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Default.Verified,
                            contentDescription = null,
                            tint = PetroleumGreen,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                    if (candidate.isVip || candidate.isGoldMember) {
                        Spacer(modifier = Modifier.width(6.dp))
                        VipBadgeOverlay(text = "VIP 👑")
                    }
                }

                Text(
                    text = "10:31",
                    fontSize = 11.sp,
                    color = Color.Gray
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isArabic)
                        "السلام عليكم ${candidate.name}، كيف حالك؟ مبارك التوافق الحلال!"
                    else
                        "As-salamu alaykum ${candidate.nameEn}, how are you?",
                    fontSize = 13.sp,
                    color = Color.DarkGray,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(6.dp))

                // +3 unread pill badge (مطابقة لصورة 2.webp)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(MuzzPink)
                        .padding(horizontal = 7.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "+3",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
