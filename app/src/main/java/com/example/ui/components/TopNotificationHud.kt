package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import kotlinx.coroutines.delay

data class TopNotificationData(
    val id: Long = System.currentTimeMillis(),
    val iconEmoji: String = "✨",
    val title: String,
    val message: String? = null,
    val isGoldAlert: Boolean = false,
    val durationMs: Long = 3200L
)

@Composable
fun TopNotificationHud(
    notification: TopNotificationData?,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(notification?.id) {
        if (notification != null) {
            delay(notification.durationMs)
            onDismiss()
        }
    }

    AnimatedVisibility(
        visible = notification != null,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(durationMillis = 350)
        ) + fadeIn(animationSpec = tween(350)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(durationMillis = 300)
        ) + fadeOut(animationSpec = tween(300)),
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .zIndex(1000f)
    ) {
        if (notification != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .shadow(12.dp, RoundedCornerShape(20.dp), spotColor = Color.Black.copy(alpha = 0.4f))
                    .background(
                        if (notification.isGoldAlert) {
                            Brush.horizontalGradient(
                                listOf(Color(0xFF2E2208), Color(0xFF1B1604), Color(0xFF352709))
                            )
                        } else {
                            Brush.horizontalGradient(
                                listOf(PetroleumGreenDark, Color(0xFF0C2725), Color(0xFF133B38))
                            )
                        }
                    )
                    .border(
                        width = 1.2.dp,
                        brush = Brush.horizontalGradient(
                            if (notification.isGoldAlert) {
                                listOf(RadiantGold, Color(0xFFFFE082), RadiantGold)
                            } else {
                                listOf(Color(0xFF26A69A), RadiantGold.copy(alpha = 0.6f))
                            }
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                    .clickable { onDismiss() }
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Notification Icon Circle
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (notification.isGoldAlert) RadiantGold.copy(alpha = 0.25f)
                                else Color.White.copy(alpha = 0.15f)
                            )
                            .border(
                                1.dp,
                                if (notification.isGoldAlert) RadiantGold else Color.White.copy(alpha = 0.4f),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = notification.iconEmoji,
                            fontSize = 18.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = notification.title,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (notification.isGoldAlert) RadiantGold else Color.White,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        if (!notification.message.isNullOrBlank()) {
                            Text(
                                text = notification.message,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.85f),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    // Subtle indicator to tap to dismiss
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(
                                if (notification.isGoldAlert) RadiantGold else Color(0xFF4CAF50)
                            )
                    )
                }
            }
        }
    }
}
