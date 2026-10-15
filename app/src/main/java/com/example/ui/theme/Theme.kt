package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BurmeseGold,
    onPrimary = TextDark,
    primaryContainer = BangkokBlueDark,
    onPrimaryContainer = Color.White,
    secondary = BurmeseGold,
    onSecondary = TextDark,
    tertiary = ThaiRed,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = DarkText,
    onSurface = DarkText,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = DarkTextMuted,
    outline = DarkBorder
)

private val LightColorScheme = lightColorScheme(
    primary = BangkokBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDCEBFE),
    onPrimaryContainer = BangkokBlueDark,
    secondary = BurmeseGold,
    onSecondary = TextDark,
    secondaryContainer = Color(0xFFFEF3C7),
    onSecondaryContainer = Color(0xFF78350F),
    tertiary = ThaiRed,
    background = BackgroundLight,
    surface = SurfaceLight,
    onBackground = TextDark,
    onSurface = TextDark,
    surfaceVariant = SurfaceCard,
    onSurfaceVariant = TextMuted,
    outline = BorderLight
)

@Composable
fun BangkokGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
