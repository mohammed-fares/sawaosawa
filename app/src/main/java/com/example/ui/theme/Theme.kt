package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = RadiantGold,
    onPrimary = Color.Black,
    primaryContainer = PetroleumGreenDark,
    onPrimaryContainer = RadiantGoldLight,
    secondary = PetroleumGreenLight,
    onSecondary = Color.White,
    tertiary = RadiantGoldLight,
    background = SurfaceDark,
    surface = CardBackgroundDark,
    onBackground = TextPrimaryDark,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF142B2A),
    onSurfaceVariant = TextSecondaryDark
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PetroleumGreen,
    onPrimary = Color.White,
    primaryContainer = PetroleumGreenContainer,
    onPrimaryContainer = PetroleumGreenDark,
    secondary = RadiantGold,
    onSecondary = Color.Black,
    secondaryContainer = RadiantGoldContainer,
    onSecondaryContainer = RadiantGoldDark,
    tertiary = MuzzPink,
    background = SurfaceLight,
    surface = CardBackgroundLight,
    onBackground = TextPrimaryLight,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEDF5F4),
    onSurfaceVariant = TextSecondaryLight
  )

@Composable
fun SawaSawaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    customPrimaryColor: Color? = null,
    customGoldColor: Color? = null,
    content: @Composable () -> Unit,
) {
    val baseScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    val finalScheme = if (customPrimaryColor != null || customGoldColor != null) {
        val primary = customPrimaryColor ?: baseScheme.primary
        val secondary = customGoldColor ?: baseScheme.secondary
        baseScheme.copy(
            primary = primary,
            secondary = secondary,
            primaryContainer = primary.copy(alpha = 0.15f),
            secondaryContainer = secondary.copy(alpha = 0.15f)
        )
    } else {
        baseScheme
    }

    MaterialTheme(
        colorScheme = finalScheme,
        typography = Typography,
        content = content
    )
}
