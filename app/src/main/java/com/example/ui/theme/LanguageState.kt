package com.example.ui.theme

import androidx.compose.runtime.*
import java.util.Locale

/**
 * Supported global application languages for Bangkok Go MM.
 */
enum class AppLanguage(val code: String, val displayName: String, val locale: Locale) {
    BURMESE("my", "မြန်မာ", Locale("my", "MM")),
    THAI("th", "ไทย", Locale("th", "TH"));

    val isBurmese: Boolean get() = this == BURMESE
    val isThai: Boolean get() = this == THAI
}

/**
 * Context object holding global language state and manipulation handlers.
 */
data class LanguageContext(
    val language: AppLanguage = AppLanguage.BURMESE,
    val onToggleLanguage: () -> Unit = {},
    val onSetLanguage: (AppLanguage) -> Unit = {}
) {
    val isBurmese: Boolean get() = language.isBurmese
    val isThai: Boolean get() = language.isThai

    /**
     * Helper to retrieve localized string based on active global language.
     */
    fun localized(mm: String, th: String): String = if (isBurmese) mm else th

    /**
     * Helper to retrieve localized string with English fallback.
     */
    fun localized(mm: String, th: String, en: String): String = if (isBurmese) mm else th
}

/**
 * Global CompositionLocal providing the current AppLanguage.
 */
val LocalAppLanguage = staticCompositionLocalOf { AppLanguage.BURMESE }

/**
 * Global CompositionLocal providing boolean flag whether current language is Burmese.
 */
val LocalIsBurmese = staticCompositionLocalOf { true }

/**
 * Global CompositionLocal providing the full LanguageContext with mutation callbacks.
 */
val LocalLanguageContext = staticCompositionLocalOf { LanguageContext() }

/**
 * Convenient Composable getter for the active AppLanguage.
 */
val currentAppLanguage: AppLanguage
    @Composable
    @ReadOnlyComposable
    get() = LocalAppLanguage.current

/**
 * Convenient Composable getter checking if Burmese is currently active.
 */
val isBurmeseActive: Boolean
    @Composable
    @ReadOnlyComposable
    get() = LocalIsBurmese.current

/**
 * Resolves localized text between Burmese and Thai based on the global CompositionLocal.
 */
@Composable
@ReadOnlyComposable
fun localized(mm: String, th: String): String = if (LocalIsBurmese.current) mm else th

/**
 * Resolves localized text between Burmese, Thai, and English based on the global CompositionLocal.
 */
@Composable
@ReadOnlyComposable
fun localized(mm: String, th: String, en: String): String = if (LocalIsBurmese.current) mm else th

/**
 * Root or subtree provider for the global language state management system.
 */
@Composable
fun ProvideAppLanguage(
    language: AppLanguage,
    onToggleLanguage: () -> Unit = {},
    onSetLanguage: (AppLanguage) -> Unit = {},
    content: @Composable () -> Unit
) {
    val context = remember(language, onToggleLanguage, onSetLanguage) {
        LanguageContext(
            language = language,
            onToggleLanguage = onToggleLanguage,
            onSetLanguage = onSetLanguage
        )
    }

    CompositionLocalProvider(
        LocalAppLanguage provides language,
        LocalIsBurmese provides language.isBurmese,
        LocalLanguageContext provides context,
        content = content
    )
}
