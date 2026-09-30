package com.example.ui.screens

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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.theme.MuzzPink
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.RadiantGold
import com.example.ui.components.GoldSparkleShowerOverlay

@Composable
fun MatchCelebrationScreen(
    candidate: CandidateProfile,
    language: AppLanguage,
    onStartChat: () -> Unit,
    onKeepBrowsing: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var quickMessage by remember { mutableStateOf("") }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val scale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    // Vibrant background in Muzz / Sawa Pink & Petroleum Green gradient (مطابقة لصورة 1.webp اليمنى)
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        MuzzPink,
                        Color(0xFFE91E63),
                        Color(0xFFC2185B)
                    )
                )
            )
    ) {
        // Luxury golden sparkles showered across match photos
        GoldSparkleShowerOverlay(visible = true)

        // Top close and logo
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onKeepBrowsing,
                modifier = Modifier
                    .size(36.dp)
                    .background(Color.White.copy(alpha = 0.25f), CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close",
                    tint = Color.White
                )
            }

            // Sawa Sawa butterfly / infinity emblem
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .scale(scale)
                    .background(Color.White, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.AllInclusive,
                    contentDescription = null,
                    tint = PetroleumGreen,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.size(36.dp))
        }

        // Center Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Center)
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Title (مطابقة لصورة 1.webp: "لديك توافق مع !Aaliyah")
            Text(
                text = if (isArabic) "لديك توافق مع" else "You matched with",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Text(
                text = if (isArabic) "!${candidate.name}" else "!${candidate.nameEn}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Overlapping tilted cards (مطابقة لصورة 1.webp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Card 1: Candidate
                Box(
                    modifier = Modifier
                        .size(width = 125.dp, height = 175.dp)
                        .rotate(-8f)
                        .clip(RoundedCornerShape(20.dp))
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp))
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = candidate.photoRes),
                        contentDescription = candidate.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Card 2: User
                Box(
                    modifier = Modifier
                        .size(width = 125.dp, height = 175.dp)
                        .rotate(8f)
                        .clip(RoundedCornerShape(20.dp))
                        .border(3.dp, RadiantGold, RoundedCornerShape(20.dp))
                        .shadow(12.dp, RoundedCornerShape(20.dp))
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.profile_ahmed),
                        contentDescription = "User",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Name, Age & Distance
            Text(
                text = "${candidate.age} ${candidate.name}",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = if (isArabic)
                    "${candidate.distanceKm} كيلومتر، ${candidate.city} 🇦🇪"
                else
                    "${candidate.distanceKm} km, ${candidate.cityEn} 🇦🇪",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f),
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Quick Icebreaker Input Capsule (مطابقة لصورة 1.webp: "اسأل عن صوره" مع سهم إرسال وردي)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(26.dp))
                    .background(Color.White)
                    .padding(horizontal = 6.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Send button on side
                IconButton(
                    onClick = onStartChat,
                    modifier = Modifier
                        .size(42.dp)
                        .background(MuzzPink, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = quickMessage,
                    onValueChange = { quickMessage = it },
                    placeholder = {
                        Text(
                            text = if (isArabic) "اسأل عن صورة أو اهتمام..." else "Ask about a photo...",
                            fontSize = 13.sp,
                            color = Color.Gray
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent
                    ),
                    maxLines = 1
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = if (isArabic)
                    "ابدأ المحادثة الآن بنية الزواج الحلال لتتعرفا على بعضكما"
                else
                    "Start a conversation now with sincere matrimonial intentions",
                color = Color.White.copy(alpha = 0.85f),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }

        // Bottom keep browsing button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .navigationBarsPadding()
                .padding(20.dp)
        ) {
            Button(
                onClick = onKeepBrowsing,
                modifier = Modifier
                    .testTag("match_keep_browsing_button")
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(25.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PetroleumGreen,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = if (isArabic) "متابعة التصفح" else "Keep browsing",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
        }
    }
}
