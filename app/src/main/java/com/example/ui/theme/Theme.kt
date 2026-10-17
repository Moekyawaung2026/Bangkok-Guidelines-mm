package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * CompositionLocal providing whether the current theme is Dark Mode.
 */
val LocalIsDarkMode = staticCompositionLocalOf { false }

/**
 * High-Contrast Dark Color Scheme conforming to WCAG AAA/AA accessibility specifications.
 * Ensures minimum 7:1 contrast for critical content and visible outlines on dark surfaces.
 */
val HighContrastDarkColorScheme: ColorScheme = darkColorScheme(
    primary = HighContrastDarkPrimary,
    onPrimary = HighContrastDarkOnPrimary,
    primaryContainer = HighContrastDarkPrimaryContainer,
    onPrimaryContainer = HighContrastDarkOnPrimaryContainer,
    inversePrimary = BangkokBlue,
    secondary = HighContrastDarkSecondary,
    onSecondary = HighContrastDarkOnSecondary,
    secondaryContainer = HighContrastDarkSecondaryContainer,
    onSecondaryContainer = HighContrastDarkOnSecondaryContainer,
    tertiary = HighContrastDarkTertiary,
    onTertiary = HighContrastDarkOnTertiary,
    tertiaryContainer = HighContrastDarkTertiaryContainer,
    onTertiaryContainer = HighContrastDarkOnTertiaryContainer,
    background = HighContrastDarkBackground,
    onBackground = HighContrastDarkOnBackground,
    surface = HighContrastDarkSurface,
    onSurface = HighContrastDarkOnSurface,
    surfaceVariant = HighContrastDarkSurfaceVariant,
    onSurfaceVariant = HighContrastDarkOnSurfaceVariant,
    surfaceTint = HighContrastDarkPrimary,
    inverseSurface = HighContrastLightSurface,
    inverseOnSurface = HighContrastLightOnSurface,
    outline = HighContrastDarkOutline,
    outlineVariant = HighContrastDarkOutlineVariant,
    error = HighContrastDarkError,
    onError = HighContrastDarkOnError,
    errorContainer = HighContrastDarkErrorContainer,
    onErrorContainer = HighContrastDarkOnErrorContainer
)

/**
 * High-Contrast Light Color Scheme conforming to WCAG AAA/AA accessibility specifications.
 * Features high-density contrast text (>16:1 ratio against white) and distinctive primary colors.
 */
val HighContrastLightColorScheme: ColorScheme = lightColorScheme(
    primary = HighContrastLightPrimary,
    onPrimary = HighContrastLightOnPrimary,
    primaryContainer = HighContrastLightPrimaryContainer,
    onPrimaryContainer = HighContrastLightOnPrimaryContainer,
    inversePrimary = HighContrastDarkPrimary,
    secondary = HighContrastLightSecondary,
    onSecondary = HighContrastLightOnSecondary,
    secondaryContainer = HighContrastLightSecondaryContainer,
    onSecondaryContainer = HighContrastLightOnSecondaryContainer,
    tertiary = HighContrastLightTertiary,
    onTertiary = HighContrastLightOnTertiary,
    tertiaryContainer = HighContrastLightTertiaryContainer,
    onTertiaryContainer = HighContrastLightOnTertiaryContainer,
    background = HighContrastLightBackground,
    onBackground = HighContrastLightOnBackground,
    surface = HighContrastLightSurface,
    onSurface = HighContrastLightOnSurface,
    surfaceVariant = HighContrastLightSurfaceVariant,
    onSurfaceVariant = HighContrastLightOnSurfaceVariant,
    surfaceTint = HighContrastLightPrimary,
    inverseSurface = HighContrastDarkSurface,
    inverseOnSurface = HighContrastDarkOnSurface,
    outline = HighContrastLightOutline,
    outlineVariant = HighContrastLightOutlineVariant,
    error = HighContrastLightError,
    onError = HighContrastLightOnError,
    errorContainer = HighContrastLightErrorContainer,
    onErrorContainer = HighContrastLightOnErrorContainer
)

/**
 * Application Theme entry point supporting real-time dynamic Dark Mode toggle
 * with high-contrast accessibility configurations.
 */
@Composable
fun BangkokGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme

    CompositionLocalProvider(
        LocalIsDarkMode provides darkTheme
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Helper object to access current theme state and accessibility contrast checks.
 */
object BangkokTheme {
    val isDarkMode: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkMode.current
}
