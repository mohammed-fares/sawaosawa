package com.example.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// الأخضر البترولي الراقي (Deep Elegant Petroleum Green)
val PetroleumGreen = Color(0xFF0F4C47)
val PetroleumGreenDark = Color(0xFF082C29)
val PetroleumGreenLight = Color(0xFF1A6B64)
val PetroleumGreenSurface = Color(0xFF0B2120)
val PetroleumGreenContainer = Color(0xFFE3F1F0)

// الذهب اللامع الفخم (Radiant Sparkling Gold)
val RadiantGold = Color(0xFFD4AF37)
val RadiantGoldLight = Color(0xFFFFE699)
val RadiantGoldDark = Color(0xFFA17C16)
val RadiantGoldContainer = Color(0xFFFDF6E4)
val RadiantGoldBorder = Color(0xFFE2C46C)

// Gradients
val GoldShineBrush = Brush.linearGradient(
    listOf(
        Color(0xFFFFEFA7),
        Color(0xFFD4AF37),
        Color(0xFFA88219),
        Color(0xFFE4C366)
    )
)

val PetroleumBrush = Brush.linearGradient(
    listOf(
        Color(0xFF1E756C),
        Color(0xFF0F4C47),
        Color(0xFF0A3330)
    )
)

val CardOverlayBrush = Brush.verticalGradient(
    listOf(
        Color.Transparent,
        Color.Black.copy(alpha = 0.25f),
        Color.Black.copy(alpha = 0.90f)
    )
)

// Accent & Notification Colors
val MuzzPink = Color(0xFFFF3366)
val MuzzPinkLight = Color(0xFFFFE4EC)
val MuzzPurpleChat = Color(0xFF4C589E)
val MuzzCallGreen = Color(0xFF00C853)
val MuzzCallRed = Color(0xFFFF1744)

// Neutral Surfaces
val SurfaceLight = Color(0xFFF7FAF9)
val CardBackgroundLight = Color(0xFFFFFFFF)
val TextPrimaryLight = Color(0xFF112220)
val TextSecondaryLight = Color(0xFF5A706E)
val BorderLight = Color(0xFFE0EBEA)

val SurfaceDark = Color(0xFF0A1615)
val CardBackgroundDark = Color(0xFF122423)
val TextPrimaryDark = Color(0xFFF0F7F6)
val TextSecondaryDark = Color(0xFF98B3B0)
