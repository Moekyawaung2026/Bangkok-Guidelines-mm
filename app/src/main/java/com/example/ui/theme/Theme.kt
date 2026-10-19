package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.remember
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
 * Custom AppTheme using Material 3 with support for light/dark modes.
 * Built with Primary Blue, Burmese Gold, and Thai Red as specified in the project guidelines.
 */
@Composable
fun AppTheme(
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
 * Custom AppTheme with support for light/dark modes and global language state.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    language: AppLanguage = AppLanguage.BURMESE,
    onToggleLanguage: () -> Unit = {},
    onSetLanguage: (AppLanguage) -> Unit = {},
    content: @Composable () -> Unit
) {
    BangkokGoTheme(
        darkTheme = darkTheme,
        language = language,
        onToggleLanguage = onToggleLanguage,
        onSetLanguage = onSetLanguage,
        content = content
    )
}

/**
 * Custom AppTheme convenience overload with boolean isBurmese.
 */
@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isBurmese: Boolean,
    onToggleLanguage: () -> Unit = {},
    onSetLanguage: (AppLanguage) -> Unit = {},
    content: @Composable () -> Unit
) {
    BangkokGoTheme(
        darkTheme = darkTheme,
        isBurmese = isBurmese,
        onToggleLanguage = onToggleLanguage,
        onSetLanguage = onSetLanguage,
        content = content
    )
}

/**
 * Application Theme entry point supporting real-time dynamic Dark Mode toggle
 * and global language state management via CompositionLocal.
 */
@Composable
fun BangkokGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    language: AppLanguage = AppLanguage.BURMESE,
    onToggleLanguage: () -> Unit = {},
    onSetLanguage: (AppLanguage) -> Unit = {},
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) HighContrastDarkColorScheme else HighContrastLightColorScheme
    val languageContext = remember(language, onToggleLanguage, onSetLanguage) {
        LanguageContext(
            language = language,
            onToggleLanguage = onToggleLanguage,
            onSetLanguage = onSetLanguage
        )
    }

    CompositionLocalProvider(
        LocalIsDarkMode provides darkTheme,
        LocalAppLanguage provides language,
        LocalIsBurmese provides language.isBurmese,
        LocalLanguageContext provides languageContext
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

/**
 * Convenience overload allowing boolean isBurmese configuration for backward compatibility.
 */
@Composable
fun BangkokGoTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    isBurmese: Boolean,
    onToggleLanguage: () -> Unit = {},
    onSetLanguage: (AppLanguage) -> Unit = {},
    content: @Composable () -> Unit
) {
    BangkokGoTheme(
        darkTheme = darkTheme,
        language = if (isBurmese) AppLanguage.BURMESE else AppLanguage.THAI,
        onToggleLanguage = onToggleLanguage,
        onSetLanguage = onSetLanguage,
        content = content
    )
}

/**
 * AppTheme helper object to access current theme state, color scheme, typography, and accessibility properties.
 */
object AppTheme {
    val isDarkMode: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkMode.current

    val colorScheme: ColorScheme
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.colorScheme

    val typography: androidx.compose.material3.Typography
        @Composable
        @ReadOnlyComposable
        get() = MaterialTheme.typography

    val language: AppLanguage
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLanguage.current

    val isBurmese: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsBurmese.current

    val languageContext: LanguageContext
        @Composable
        @ReadOnlyComposable
        get() = LocalLanguageContext.current
}

/**
 * Backward-compatible helper object to access current theme state, accessibility contrast, and global language.
 */
object BangkokTheme {
    val isDarkMode: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsDarkMode.current

    val language: AppLanguage
        @Composable
        @ReadOnlyComposable
        get() = LocalAppLanguage.current

    val isBurmese: Boolean
        @Composable
        @ReadOnlyComposable
        get() = LocalIsBurmese.current

    val languageContext: LanguageContext
        @Composable
        @ReadOnlyComposable
        get() = LocalLanguageContext.current
}
