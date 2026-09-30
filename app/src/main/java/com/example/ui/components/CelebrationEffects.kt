package com.example.ui.components

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.ui.theme.PetroleumGreenDark
import com.example.ui.theme.RadiantGold
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * Gold Sparkle Shower Animation
 * Scattered shimmering golden flakes, stars, and radiant dust over matching profiles.
 */
@Composable
fun GoldSparkleShowerOverlay(
    visible: Boolean,
    onFinished: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val particles = remember {
        List(40) {
            SparkleParticle(
                xPct = Random.nextFloat(),
                yPct = Random.nextFloat(),
                radius = Random.nextFloat() * 6f + 3f,
                speed = Random.nextFloat() * 0.8f + 0.4f,
                alpha = Random.nextFloat() * 0.6f + 0.4f,
                color = if (Random.nextBoolean()) RadiantGold else Color(0xFFFFE57F)
            )
        }
    }

    val animProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 3000, easing = LinearEasing)
        )
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .zIndex(99f)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            particles.forEach { p ->
                val currY = ((p.yPct + animProgress.value * p.speed) % 1.0f) * h
                val currX = (p.xPct * w) + kotlin.math.sin((animProgress.value * 10f + p.xPct * 10f)) * 25f

                drawCircle(
                    color = p.color.copy(alpha = p.alpha * (1f - animProgress.value * 0.3f)),
                    radius = p.radius,
                    center = Offset(currX, currY)
                )
            }
        }
    }
}

private data class SparkleParticle(
    val xPct: Float,
    val yPct: Float,
    val radius: Float,
    val speed: Float,
    val alpha: Float,
    val color: Color
)

/**
 * High-End Rocket Boost Takeoff with Device Haptic Vibration
 * Simulates intense jet engine vibration and rocket soaring into the sky.
 */
@Composable
fun RocketBoostTakeoffOverlay(
    visible: Boolean,
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (!visible) return

    val context = LocalContext.current
    val rocketY = remember { Animatable(300f) }
    val rocketScale = remember { Animatable(1f) }
    val glowAlpha = remember { Animatable(0.2f) }

    // Haptic device vibration effect
    LaunchedEffect(Unit) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        if (vibrator != null) {
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    val timings = longArrayOf(0, 100, 50, 150, 50, 250, 80, 400)
                    val amplitudes = intArrayOf(0, 80, 0, 150, 0, 220, 0, 255)
                    vibrator.vibrate(VibrationEffect.createWaveform(timings, amplitudes, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator.vibrate(1000)
                }
            } catch (_: Exception) {}
        }

        // Rocket flight sequence
        launch {
            glowAlpha.animateTo(1f, tween(400))
        }
        launch {
            delay(350)
            rocketScale.animateTo(1.4f, tween(300))
            rocketY.animateTo(-700f, tween(1400, easing = FastOutSlowInEasing))
        }

        delay(2200)
        onFinished()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.70f))
            .zIndex(100f),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(24.dp)
        ) {
            // Speed Lines & Aura
            Box(
                modifier = Modifier
                    .size(160.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .scale(rocketScale.value)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    RadiantGold.copy(alpha = glowAlpha.value * 0.8f),
                                    Color(0xFFFF5722).copy(alpha = glowAlpha.value * 0.4f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Rocket emoji
                Text(
                    text = "🚀",
                    fontSize = 72.sp,
                    modifier = Modifier
                        .offset { IntOffset(0, rocketY.value.toInt()) }
                        .scale(rocketScale.value)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(PetroleumGreenDark)
                    .padding(horizontal = 24.dp, vertical = 12.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "تم إطلاق تعزيز الحساب بنجاح! 🚀⚡",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = RadiantGold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ملفك الشخصي الآن يظهر في صدارة المستكشف لآلاف الأعضاء",
                        fontSize = 12.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}
