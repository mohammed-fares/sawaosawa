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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import coil.compose.AsyncImage
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.example.model.CandidateProfile
import com.example.model.ChatMessage
import com.example.ui.components.ChaperoneBadgeBanner
import com.example.ui.theme.PetroleumGreen
import com.example.ui.theme.PetroleumGreenContainer
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold

import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.TextButton
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll

import com.example.ui.components.VipBadgeOverlay

@Composable
fun ChatDetailScreen(
    candidate: CandidateProfile,
    messages: List<ChatMessage>,
    language: AppLanguage,
    chaperoneActive: Boolean,
    chaperoneName: String,
    onBack: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendVoiceNote: () -> Unit,
    onSendVoiceNoteWithDuration: (String) -> Unit = {},
    onSendImage: (String) -> Unit = {},
    onStartVideoCall: () -> Unit,
    onOpenChaperoneNotice: () -> Unit,
    onToggleAudio: (Long) -> Unit,
    activeAudioId: Long?,
    isAudioPlaying: Boolean,
    onBlockCandidate: (String) -> Unit = {},
    onReportCandidate: (String, String) -> Unit = { _, _ -> },
    onToggleRevealPhotos: (String) -> Unit = {},
    onSendRose: () -> Unit = {},
    onSendCallInvitation: (Boolean) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var inputText by remember { mutableStateOf("") }
    var selectedChatTab by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    var isRecordingVoiceNote by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

    var showMoreMenu by remember { mutableStateOf(false) }
    var showBlockDialog by remember { mutableStateOf(false) }
    var showReportDialog by remember { mutableStateOf(false) }
    var selectedReportReason by remember {
        mutableStateOf(if (isArabic) "عدم الجدية في نية الزواج الحلال" else "Not serious about halal marriage")
    }
    var showWaliDialog by remember { mutableStateOf(false) }
    var showCallInvitationDialog by remember { mutableStateOf(false) }
    var callIsVideo by remember { mutableStateOf(false) }
    var callInvitationSent by remember { mutableStateOf(false) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onSendImage(uri.toString())
        }
    }

    LaunchedEffect(isRecordingVoiceNote) {
        if (isRecordingVoiceNote) {
            recordingSeconds = 0
            while (isRecordingVoiceNote && recordingSeconds < 30) {
                kotlinx.coroutines.delay(1000)
                recordingSeconds++
            }
            if (isRecordingVoiceNote && recordingSeconds >= 30) {
                isRecordingVoiceNote = false
                onSendVoiceNoteWithDuration("00:30")
            }
        }
    }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
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
        ) {
            // Header Bar (مطابقة لصورة 2.webp: Video, Call, More, Name, Avatar)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.testTag("chat_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = PetroleumGreen
                    )
                }

                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .border(1.5.dp, RadiantGold, CircleShape)
                ) {
                    Image(
                        painter = painterResource(id = candidate.photoRes),
                        contentDescription = candidate.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isArabic) candidate.name else candidate.nameEn,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (candidate.isVip || candidate.isGoldMember) {
                            Spacer(modifier = Modifier.width(6.dp))
                            VipBadgeOverlay(text = "VIP 👑")
                        }
                    }
                    Text(
                        text = if (isArabic) "متصل الآن • زواج شرعي" else "Online • Halal Match",
                        fontSize = 11.sp,
                        color = PetroleumGreen
                    )
                }

                // Call Icons (Video Call & Audio Call with Halal Invitation Flow)
                IconButton(
                    onClick = {
                        if (callInvitationSent) {
                            onStartVideoCall()
                        } else {
                            callIsVideo = true
                            showCallInvitationDialog = true
                        }
                    },
                    modifier = Modifier.testTag("chat_video_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Videocam,
                        contentDescription = "Video Call",
                        tint = PetroleumGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                IconButton(
                    onClick = {
                        if (callInvitationSent) {
                            onStartVideoCall()
                        } else {
                            callIsVideo = false
                            showCallInvitationDialog = true
                        }
                    },
                    modifier = Modifier.testTag("chat_audio_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = PetroleumGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Box {
                    IconButton(
                        onClick = { showMoreMenu = true },
                        modifier = Modifier.testTag("chat_more_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menu",
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMoreMenu,
                        onDismissRequest = { showMoreMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(if (isArabic) "🛡️ معلومات وتواصل ولي الأمر" else "Wali / Guardian Info") },
                            onClick = {
                                showMoreMenu = false
                                showWaliDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Shield, contentDescription = null, tint = PetroleumGreen)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isArabic) "👁️ السماح برؤية صوري الخاصة" else "Reveal My Photos") },
                            onClick = {
                                showMoreMenu = false
                                onToggleRevealPhotos(candidate.id)
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Visibility, contentDescription = null, tint = PetroleumGreen)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isArabic) "🌹 إرسال باقة ورد VIP" else "Send VIP Rose") },
                            onClick = {
                                showMoreMenu = false
                                onSendRose()
                            },
                            leadingIcon = {
                                Text("🌹", fontSize = 16.sp)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text(if (isArabic) "⚠️ إرسال بلاغ عن المستخدم" else "Report Candidate") },
                            onClick = {
                                showMoreMenu = false
                                showReportDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Flag, contentDescription = null, tint = Color(0xFFE65100))
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text(if (isArabic) "🚫 حظر المستخدم نهائياً" else "Block Candidate", color = Color.Red) },
                            onClick = {
                                showMoreMenu = false
                                showBlockDialog = true
                            },
                            leadingIcon = {
                                Icon(Icons.Default.Block, contentDescription = null, tint = Color.Red)
                            }
                        )
                    }
                }
            }

            // Tabs Row: "دردشة" | "ملف شخصي" (مطابقة لصورة 2.webp)
            TabRow(
                selectedTabIndex = selectedChatTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = PetroleumGreen,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedChatTab]),
                        color = PetroleumGreen,
                        height = 3.dp
                    )
                }
            ) {
                Tab(
                    selected = selectedChatTab == 0,
                    onClick = { selectedChatTab = 0 },
                    text = {
                        Text(
                            text = if (isArabic) "دردشة" else "Chat",
                            fontWeight = if (selectedChatTab == 0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
                Tab(
                    selected = selectedChatTab == 1,
                    onClick = { selectedChatTab = 1 },
                    text = {
                        Text(
                            text = if (isArabic) "ملف شخصي" else "Profile",
                            fontWeight = if (selectedChatTab == 1) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 14.sp
                        )
                    }
                )
            }

            // Chaperone Guardian Banner
            if (chaperoneActive) {
                ChaperoneBadgeBanner(
                    isArabic = isArabic,
                    chaperoneName = chaperoneName,
                    onNoticeClick = onOpenChaperoneNotice,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                )
            }

            if (selectedChatTab == 0) {
                // Messages List (Waveforms, Text, Photo message)
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(messages) { msg ->
                        ChatBubbleItem(
                            message = msg,
                            isArabic = isArabic,
                            onToggleAudio = { onToggleAudio(msg.id) },
                            isPlaying = isAudioPlaying && activeAudioId == msg.id
                        )
                    }
                }

                // Chat Input Bar (Gallery photo, 30s Mic, Capsule Input, Send - with proper imePadding)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .imePadding()
                ) {
                    if (isRecordingVoiceNote) {
                        // Active 30-Second Voice Recording Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            tonalElevation = 6.dp,
                            color = Color(0xFF1E282D)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(12.dp)
                                            .background(Color(0xFFE53935), CircleShape)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "00:${String.format("%02d", recordingSeconds)} / 00:30",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text(
                                        text = "ılıll|llılı|l|ll",
                                        fontSize = 14.sp,
                                        color = RadiantGold,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    IconButton(
                                        onClick = { isRecordingVoiceNote = false },
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Cancel",
                                            tint = Color.LightGray
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    IconButton(
                                        onClick = {
                                            isRecordingVoiceNote = false
                                            val dur = String.format("00:%02d", recordingSeconds.coerceAtLeast(1))
                                            onSendVoiceNoteWithDuration(dur)
                                        },
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(PetroleumGreen, CircleShape)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.Send,
                                            contentDescription = "Send Voice Note",
                                            tint = Color.White,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Normal Input Bar
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            tonalElevation = 4.dp,
                            color = MaterialTheme.colorScheme.surface
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // 1. Gallery Photo Picker Button (إرسال صورة من الاستوديو)
                                IconButton(
                                    onClick = {
                                        photoPickerLauncher.launch(
                                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        )
                                    },
                                    modifier = Modifier.size(38.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Image,
                                        contentDescription = "Send Photo from Gallery",
                                        tint = PetroleumGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // 2. 30-Second Voice Recorder Button (التسجيل فى المايك بمدة لا تتجاوز 30 ثانية)
                                IconButton(
                                    onClick = { isRecordingVoiceNote = true },
                                    modifier = Modifier
                                        .testTag("record_voice_note_button")
                                        .size(38.dp)
                                    ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = "Voice Note (max 30s)",
                                        tint = PetroleumGreen,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }

                                // 3. Message Input (Capsule without GIF or Attachments)
                                OutlinedTextField(
                                    value = inputText,
                                    onValueChange = { inputText = it },
                                    placeholder = {
                                        Text(
                                            text = if (isArabic) "رسالة ${candidate.name}..." else "Message ${candidate.nameEn}...",
                                            fontSize = 13.sp,
                                            color = Color.Gray
                                        )
                                    },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("chat_message_input"),
                                    shape = RoundedCornerShape(24.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = PetroleumGreen,
                                        unfocusedBorderColor = Color.LightGray.copy(alpha = 0.5f)
                                    ),
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.width(6.dp))

                                // 4. Send Message Button
                                IconButton(
                                    onClick = {
                                        if (inputText.isNotBlank()) {
                                            onSendMessage(inputText)
                                            inputText = ""
                                        }
                                    },
                                    modifier = Modifier
                                        .testTag("chat_send_button")
                                        .size(42.dp)
                                        .background(PetroleumGreen, CircleShape)
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.Send,
                                        contentDescription = "Send",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Complete candidate profile tab: photos, attendance times, goals, religiosity, family details
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Profile Photo Card with verified & VIP badges
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp),
                        shape = RoundedCornerShape(22.dp)
                    ) {
                        Box(modifier = Modifier.fillMaxSize()) {
                            Image(
                                painter = painterResource(id = candidate.photoRes),
                                contentDescription = candidate.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier.fillMaxSize()
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                                        )
                                    )
                            )
                            Column(
                                modifier = Modifier
                                    .align(Alignment.BottomStart)
                                    .padding(16.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "${if (isArabic) candidate.name else candidate.nameEn}، ${candidate.age}",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                    if (candidate.isVerified) {
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Icon(
                                            imageVector = Icons.Default.Verified,
                                            contentDescription = "Verified",
                                            tint = RadiantGold,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Text(
                                    text = "📍 ${if (isArabic) candidate.city else candidate.cityEn} • يبعد ${candidate.distanceKm} كم",
                                    fontSize = 13.sp,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }

                            // Quick Rose Button Overlay
                            Button(
                                onClick = onSendRose,
                                colors = ButtonDefaults.buttonColors(containerColor = RadiantGold),
                                shape = RoundedCornerShape(16.dp),
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .padding(16.dp)
                            ) {
                                Text("🌹 إهداء ورد", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E2204))
                            }
                        }
                    }

                    // Presence & Active Hours Card (مواعيد الحضور)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = PetroleumGreenContainer)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Schedule,
                                contentDescription = null,
                                tint = PetroleumGreen,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = if (isArabic) "مواعيد الحضور والتواجد ⏰" else "Active Presence Hours ⏰",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreenDark
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = candidate.activeHours,
                                    fontSize = 12.sp,
                                    color = PetroleumGreenDark.copy(alpha = 0.85f)
                                )
                                Text(
                                    text = if (candidate.isOnlineNow) "🟢 متصل الآن بالمنصة" else "⚪ غير متصل حالياً",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (candidate.isOnlineNow) Color(0xFF2E7D32) else Color.Gray
                                )
                            }
                        }
                    }

                    // Marriage Intentions Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isArabic) "هدف الزواج والنية 💍" else "Marriage Goal 💍",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) candidate.marriageGoal else candidate.marriageGoalEn,
                                fontSize = 13.sp,
                                lineHeight = 19.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Religious Practice & Halal Commitment Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isArabic) "الالتزام الديني والشرعي 🕌" else "Religious Commitment 🕌",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreen
                            )
                            Text(text = "• الالتزام: ${if (isArabic) candidate.religiousPractice else candidate.religiousPracticeEn}", fontSize = 12.sp)
                            Text(text = "• اللباس الإسلامي: ${if (isArabic) candidate.islamicDress else candidate.islamicDressEn}", fontSize = 12.sp)
                            Text(text = "• المحافظة على الصلوات: ${if (isArabic) candidate.prayersHabit else candidate.prayersHabitEn}", fontSize = 12.sp)
                            Text(text = "• الأكل الحلال: ${candidate.halalFood}", fontSize = 12.sp)
                        }
                    }

                    // Personal Background & Family
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (isArabic) "البيانات الشخصية والاجتماعية 📋" else "Personal & Background 📋",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreen
                            )
                            Text(text = "• المهنة: ${if (isArabic) candidate.profession else candidate.professionEn}", fontSize = 12.sp)
                            Text(text = "• المؤهل التعليمي: ${candidate.education}", fontSize = 12.sp)
                            Text(text = "• الأصل العرقي: ${if (isArabic) candidate.ethnicity else candidate.ethnicityEn}", fontSize = 12.sp)
                            Text(text = "• الحالة الاجتماعية: ${if (isArabic) candidate.maritalStatus else candidate.maritalStatusEn}", fontSize = 12.sp)
                            Text(text = "• وجود أطفال: ${if (candidate.hasChildren) "نعم" else "لا"}", fontSize = 12.sp)
                            Text(text = "• الاستعداد للانتقال: ${if (candidate.willingToRelocate) "نعم" else "لا"}", fontSize = 12.sp)
                            Text(text = "• الطول: ${candidate.heightCm} سم", fontSize = 12.sp)
                            Text(text = "• اللغات المتحدث بها: ${candidate.languages}", fontSize = 12.sp)
                            Text(text = "• باقات الورد المستلمة: ${candidate.rosesReceivedCount} باقة ورد 🌹", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = RadiantGoldDark)
                        }
                    }

                    // Icebreaker Question & Answer
                    if (candidate.icebreakerQuestion.isNotBlank()) {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "💡 ${candidate.icebreakerQuestion}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PetroleumGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "«${candidate.icebreakerAnswer}»",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    // Bio
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Text(
                                text = if (isArabic) "نبذة عني" else "About Me",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = PetroleumGreen
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = if (isArabic) candidate.bio else candidate.bioEn,
                                fontSize = 12.sp,
                                lineHeight = 18.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Contact Wali Guardian Action
                    Button(
                        onClick = { showWaliDialog = true },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                    ) {
                        Icon(imageVector = Icons.Default.Shield, contentDescription = null, tint = RadiantGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isArabic) "التواصل مع ولي الأمر الشرعي 🛡️" else "Contact Wali / Guardian",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }

    // 1. Block Confirmation Dialog
    if (showBlockDialog) {
        AlertDialog(
            onDismissRequest = { showBlockDialog = false },
            title = {
                Text(
                    text = if (isArabic) "حظر المستخدم نهائياً 🚫" else "Block Candidate",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Text(
                    text = if (isArabic)
                        "هل أنت متأكد من رغبتك في حظر ${candidate.name}؟ لن يتمكن من مراسلتك أو رؤية حسابك مجدداً وسيتم إخفاؤه من جميع القوائم."
                    else
                        "Are you sure you want to block ${candidate.nameEn}? You will not see each other's profiles or messages.",
                    fontSize = 13.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showBlockDialog = false
                        onBlockCandidate(candidate.id)
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) {
                    Text(if (isArabic) "تأكيد الحظر" else "Confirm Block")
                }
            },
            dismissButton = {
                TextButton(onClick = { showBlockDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // 2. Report User Dialog with reasons
    if (showReportDialog) {
        val reasons = if (isArabic) listOf(
            "عدم الجدية في نية الزواج الحلال",
            "محتوى غير لائق أو رسائل مسيئة",
            "طلب معلومات مالية أو شخصية حساسة",
            "انتحال شخصية أو صور وهمية"
        ) else listOf(
            "Not serious about halal marriage",
            "Inappropriate content or offensive messages",
            "Requesting financial or sensitive details",
            "Fake profile or impersonation"
        )

        AlertDialog(
            onDismissRequest = { showReportDialog = false },
            title = {
                Text(
                    text = if (isArabic) "إرسال بلاغ رقابي شرعي ⚠️" else "Submit Report",
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = if (isArabic) "حدد سبب البلاغ لإرساله للمشرفين الشرعيين:" else "Select the reason for reporting:",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    reasons.forEach { r ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedReportReason = r }
                                .padding(vertical = 4.dp)
                        ) {
                            RadioButton(
                                selected = selectedReportReason == r,
                                onClick = { selectedReportReason = r },
                                colors = RadioButtonDefaults.colors(selectedColor = PetroleumGreen)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = r, fontSize = 13.sp)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showReportDialog = false
                        onReportCandidate(candidate.id, selectedReportReason)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Text(if (isArabic) "إرسال البلاغ" else "Submit Report")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReportDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }

    // 3. Wali / Guardian Contact Dialog
    if (showWaliDialog) {
        AlertDialog(
            onDismissRequest = { showWaliDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("🛡️", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "بيانات ولي الأمر الشرعي" else "Wali / Guardian Information",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = if (isArabic)
                            "تطبيق سوا سوا يلتزم بضوابط الزواج الشرعي ويوفر وسيلة رسمية للتواصل مع ولي الأمر مباشرة:"
                        else
                            "Sawa Sawa supports full halal marriage values by enabling direct communication with the guardian:",
                        fontSize = 12.sp,
                        color = Color.DarkGray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = PetroleumGreenContainer),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "اسم الولي: $chaperoneName", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = PetroleumGreenDark)
                            Text(text = "رقم الهاتف المعتمد: +20 10 1234 5678", fontSize = 12.sp, color = PetroleumGreenDark)
                            Text(text = "البريد الإلكتروني: wali.guardian@sawasawa.app", fontSize = 12.sp, color = PetroleumGreenDark)
                            Text(text = "الحالة: موثق ومعتمد رسمياً ✓", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2E7D32))
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showWaliDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Text(if (isArabic) "حسناً، فهمت" else "Understood")
                }
            }
        )
    }

    // 4. Halal Call Invitation Dialog
    if (showCallInvitationDialog) {
        AlertDialog(
            onDismissRequest = { showCallInvitationDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(if (callIsVideo) "📹" else "📞", fontSize = 22.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isArabic) "طلب دعوة اتصال شرعي" else "Call Invitation Request",
                        fontWeight = FontWeight.Bold
                    )
                }
            },
            text = {
                Text(
                    text = if (isArabic)
                        "حفاظاً على الضوابط والخصوصية في سوا سوا، يتطلب الاتصال إرسال دعوة رسمية مسبقة إلى ${candidate.name}. عند قبولها ستتمكن من بدء ${if (callIsVideo) "مكالمة الفيديو" else "المكالمة الصوتية"} مباشرة مع إشعار الولي. هل ترغب في إرسال الدعوة الآن؟"
                    else
                        "To respect modesty, calling sends a formal invitation to ${candidate.nameEn} first. Once accepted, the call will connect. Send invitation now?",
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        showCallInvitationDialog = false
                        callInvitationSent = true
                        onSendCallInvitation(callIsVideo)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PetroleumGreen)
                ) {
                    Text(if (isArabic) "إرسال الدعوة الآن ✉️" else "Send Invitation ✉️")
                }
            },
            dismissButton = {
                TextButton(onClick = { showCallInvitationDialog = false }) {
                    Text(if (isArabic) "إلغاء" else "Cancel")
                }
            }
        )
    }
}

@Composable
fun ChatBubbleItem(
    message: ChatMessage,
    isArabic: Boolean,
    onToggleAudio: () -> Unit,
    isPlaying: Boolean
) {
    val isUser = message.isFromUser

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (message.isAudioVoiceNote) {
            // Audio Voice Note Waveform Bubble (مطابقة لصورة 2.webp: Waveform player)
            Box(
                modifier = Modifier
                    .widthIn(min = 220.dp, max = 290.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(
                        if (isUser) PetroleumGreenContainer else Color(0xFF263238)
                    )
                    .clickable { onToggleAudio() }
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(
                                if (isUser) PetroleumGreen else Color.White,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = "Play/Pause",
                            tint = if (isUser) Color.White else PetroleumGreenDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Audio Waveform graphic representation
                    Text(
                        text = if (isPlaying) "ılıll|llılı|l|ll" else "|||||||||||||||",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isUser) PetroleumGreenDark else RadiantGold
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = message.audioDuration,
                        fontSize = 12.sp,
                        color = if (isUser) PetroleumGreenDark else Color.White.copy(alpha = 0.8f)
                    )

                    Icon(
                        imageVector = Icons.Default.DoneAll,
                        contentDescription = "Delivered",
                        tint = if (isUser) PetroleumGreen else RadiantGold,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        } else if (message.imageUrl != null) {
            // Photo Message Bubble (صورة من الاستوديو)
            Box(
                modifier = Modifier
                    .widthIn(max = 260.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) PetroleumGreen else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(6.dp)
            ) {
                Column {
                    AsyncImage(
                        model = message.imageUrl,
                        contentDescription = "Shared photo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                    if (message.text.isNotBlank() && message.text != "📷 صورة مرفقة") {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = message.text,
                            fontSize = 13.sp,
                            color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        } else {
            // Standard Text Message Bubble
            Box(
                modifier = Modifier
                    .widthIn(max = 280.dp)
                    .clip(
                        RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = if (isUser) 16.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 16.dp
                        )
                    )
                    .background(
                        if (isUser) PetroleumGreen else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Text(
                    text = message.text,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = if (isUser) (if (isArabic) "أنت" else "You") else "",
            fontSize = 10.sp,
            color = Color.Gray,
            modifier = Modifier.padding(horizontal = 4.dp)
        )
    }
}
