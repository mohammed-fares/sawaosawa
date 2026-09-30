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
    modifier: Modifier = Modifier
) {
    val isArabic = language == AppLanguage.ARABIC
    var inputText by remember { mutableStateOf("") }
    var selectedChatTab by remember { mutableIntStateOf(0) }
    val listState = rememberLazyListState()

    var isRecordingVoiceNote by remember { mutableStateOf(false) }
    var recordingSeconds by remember { mutableIntStateOf(0) }

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
                .navigationBarsPadding()
                .imePadding()
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

                // Call Icons (Video Call & Audio Call)
                IconButton(
                    onClick = onStartVideoCall,
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
                    onClick = onStartVideoCall,
                    modifier = Modifier.testTag("chat_audio_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Call,
                        contentDescription = "Audio Call",
                        tint = PetroleumGreen,
                        modifier = Modifier.size(22.dp)
                    )
                }

                IconButton(onClick = {}) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Menu",
                        tint = Color.Gray,
                        modifier = Modifier.size(22.dp)
                    )
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

                // Chat Input Bar (Gallery photo, 30s Mic, Capsule Input, Send - No GIF or external attachments)
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
            } else {
                // Profile summary tab within chat
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(16.dp)
                ) {
                    Text(
                        text = if (isArabic) "تفاصيل الملف الشخصي" else "Profile Details",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = PetroleumGreen
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(text = "• ${candidate.profession}", fontSize = 14.sp)
                    Text(text = "• ${candidate.religiousPractice}", fontSize = 14.sp)
                    Text(text = "• ${candidate.education}", fontSize = 14.sp)
                    Text(text = "• اللغات: ${candidate.languages}", fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(text = candidate.bio, fontSize = 13.sp, color = Color.DarkGray, lineHeight = 20.sp)
                }
            }
        }
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
