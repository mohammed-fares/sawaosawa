package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.CandidateProfile
import com.example.ui.components.CandidateCard
import com.example.ui.components.SawaTopBar
import com.example.ui.components.SwipeActionButtons
import com.example.ui.theme.MuzzPurpleChat
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.RadiantGold

@Composable
fun DiscoveryScreen(
    currentCandidate: CandidateProfile?,
    remainingCount: Int,
    language: AppLanguage,
    boostActive: Boolean,
    boostsCount: Int,
    onFilterClick: () -> Unit,
    onBoostClick: () -> Unit,
    onGoldClick: () -> Unit,
    onCardClick: (CandidateProfile) -> Unit,
    onToggleBlur: (CandidateProfile) -> Unit,
    onPass: () -> Unit,
    onInstantChat: (CandidateProfile, String) -> Unit,
    onLike: () -> Unit,
    onResetDiscovery: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var showInstantChatDialog by remember { mutableStateOf(false) }
    var instantMessageText by remember {
        mutableStateOf(
            if (isArabic)
                "السلام عليكم ورحمة الله وبركاته، أعجبني ملفك وأتطلع للتعارف بنية الزواج الحلال."
            else
                "As-salamu alaykum, I was inspired by your profile and would love to connect for halal marriage."
        )
    }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(boostActive) {
        if (boostActive) {
            snackbarHostState.showSnackbar(
                if (isArabic) "🚀 تم تفعيل تعزيز سوا سوا الذهبي! ملفك يظهر للجميع أولاً" else "🚀 Sawa Sawa Gold Boost active! Your profile is prioritized"
            )
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) {
        // Top Bar
        SawaTopBar(
            isArabic = isArabic,
            onFilterClick = onFilterClick,
            onBoostClick = onBoostClick,
            onGoldClick = onGoldClick,
            boostActive = boostActive,
            boostsCount = boostsCount
        )

        // Main Card / Empty State Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            if (currentCandidate != null) {
                CandidateCard(
                    candidate = currentCandidate,
                    isArabic = isArabic,
                    onCardClick = { onCardClick(currentCandidate) },
                    onToggleBlur = { onToggleBlur(currentCandidate) },
                    onPass = onPass,
                    onInstantChat = { showInstantChatDialog = true },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                EmptyDiscoveryState(
                    isArabic = isArabic,
                    onReset = onResetDiscovery
                )
            }
        }

        // Bottom Action buttons row
        if (currentCandidate != null) {
            SwipeActionButtons(
                onPass = onPass,
                onInstantChat = { showInstantChatDialog = true },
                onLike = onLike
            )
        } else {
            Spacer(modifier = Modifier.height(76.dp))
        }

        SnackbarHost(hostState = snackbarHostState)
    }

    // Instant Chat Dialog
    if (showInstantChatDialog && currentCandidate != null) {
        AlertDialog(
            onDismissRequest = { showInstantChatDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(MuzzPurpleChat, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isArabic) "دردشة فورية (كسر الجليد)" else "Instant Message",
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = if (isArabic)
                            "أرسل رسالة فورية مهذبة مباشرة إلى ${currentCandidate.name} للبدء في التعارف الحلال:"
                        else
                            "Send a polite direct icebreaker message to ${currentCandidate.nameEn}:",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = instantMessageText,
                        onValueChange = { instantMessageText = it },
                        modifier = Modifier
                            .testTag("instant_message_input")
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PetroleumGreen,
                            unfocusedBorderColor = Color.LightGray
                        ),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onInstantChat(currentCandidate, instantMessageText)
                        showInstantChatDialog = false
                    },
                    modifier = Modifier.testTag("send_instant_message_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isArabic) "إرسال وتوافق" else "Send & Match")
                }
            },
            dismissButton = {
                TextButton(onClick = { showInstantChatDialog = false }) {
                    Text(
                        text = if (isArabic) "إلغاء" else "Cancel",
                        color = Color.Gray
                    )
                }
            }
        )
    }
}

@Composable
fun EmptyDiscoveryState(
    isArabic: Boolean,
    onReset: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .background(PetroleumGreenContainer, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.AllInclusive,
                contentDescription = null,
                tint = PetroleumGreen,
                modifier = Modifier.size(46.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isArabic) "لقد شاهدت جميع الملفات المتاحة!" else "You've seen all profiles for now!",
            fontSize = 19.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = if (isArabic)
                "يمكنك إعادة تصفح الملفات السابقة أو تعديل فلاتر البحث لإيجاد مرشحين إضافيين."
            else
                "You can reset discovery or adjust filters to explore more candidates.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onReset,
            modifier = Modifier
                .testTag("reset_discovery_button")
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
        ) {
            Icon(imageVector = Icons.Default.Refresh, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isArabic) "إعادة تصفح الملفات" else "Reset Discovery",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
