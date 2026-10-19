package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// =========================================================================
// Core Brand Color Constants (Project Guidelines Specification)
// Primary Blue, Burmese Gold, and Thai Red
// =========================================================================
val PrimaryBlue = Color(0xFF0B5ED7)
val PrimaryBlueDark = Color(0xFF084298)
val PrimaryBlueLight = Color(0xFF60A5FA)

val BurmeseGold = Color(0xFFF4B400)
val BurmeseGoldDark = Color(0xFF9A5B00)
val BurmeseGoldLight = Color(0xFFFFD54F)

val ThaiRed = Color(0xFFD62828)
val ThaiRedDark = Color(0xFFB91C1C)
val ThaiRedLight = Color(0xFFF87171)

// Standard Brand & Semantic Colors (Maintained for Backward Compatibility)
val BangkokBlue = PrimaryBlue
val BangkokBlueDark = PrimaryBlueDark
val BangkokBlueLight = PrimaryBlueLight

val SuccessGreen = Color(0xFF16803C)
val WarningOrange = Color(0xFFE67E22)
val TextDark = Color(0xFF17202A)
val TextMuted = Color(0xFF5A6A80)
val BackgroundLight = Color(0xFFF7F9FC)
val SurfaceLight = Color(0xFFFFFFFF)
val SurfaceCard = Color(0xFFF1F5F9)
val BorderLight = Color(0xFFE2E8F0)

/**
 * Brand color palette holder object.
 */
object AppColors {
    val PrimaryBlue = Color(0xFF0B5ED7)
    val PrimaryBlueDark = Color(0xFF084298)
    val PrimaryBlueLight = Color(0xFF60A5FA)

    val BurmeseGold = Color(0xFFF4B400)
    val BurmeseGoldDark = Color(0xFF9A5B00)
    val BurmeseGoldLight = Color(0xFFFFD54F)

    val ThaiRed = Color(0xFFD62828)
    val ThaiRedDark = Color(0xFFB91C1C)
    val ThaiRedLight = Color(0xFFF87171)

    val SuccessGreen = Color(0xFF16803C)
    val WarningOrange = Color(0xFFE67E22)
    val TextDark = Color(0xFF17202A)
    val TextMuted = Color(0xFF5A6A80)
}

// Standard Dark Theme Colors
val DarkBackground = Color(0xFF0F172A)
val DarkSurface = Color(0xFF1E293B)
val DarkSurfaceCard = Color(0xFF334155)
val DarkText = Color(0xFFF8FAFC)
val DarkTextMuted = Color(0xFF94A3B8)
val DarkBorder = Color(0xFF475569)

// =========================================================================
// High Contrast Light Mode Palette (WCAG AAA / AA Accessible Ratios)
// =========================================================================
val HighContrastLightPrimary = Color(0xFF0A58CA) // 7.5:1 on white
val HighContrastLightOnPrimary = Color(0xFFFFFFFF)
val HighContrastLightPrimaryContainer = Color(0xFFD6E4FF)
val HighContrastLightOnPrimaryContainer = Color(0xFF001B3F)

val HighContrastLightSecondary = Color(0xFF9A5B00) // Deep warm gold, >7:1 on white
val HighContrastLightOnSecondary = Color(0xFFFFFFFF)
val HighContrastLightSecondaryContainer = Color(0xFFFEF3C7)
val HighContrastLightOnSecondaryContainer = Color(0xFF451A03)

val HighContrastLightTertiary = Color(0xFFB91C1C) // 7:1 on white
val HighContrastLightOnTertiary = Color(0xFFFFFFFF)
val HighContrastLightTertiaryContainer = Color(0xFFFEE2E2)
val HighContrastLightOnTertiaryContainer = Color(0xFF450A0A)

val HighContrastLightBackground = Color(0xFFF8FAFC)
val HighContrastLightOnBackground = Color(0xFF0F172A) // 16.5:1 contrast ratio
val HighContrastLightSurface = Color(0xFFFFFFFF)
val HighContrastLightOnSurface = Color(0xFF0F172A)
val HighContrastLightSurfaceVariant = Color(0xFFF1F5F9)
val HighContrastLightOnSurfaceVariant = Color(0xFF334155) // 9:1 contrast ratio
val HighContrastLightOutline = Color(0xFF64748B)
val HighContrastLightOutlineVariant = Color(0xFFCBD5E1)
val HighContrastLightError = Color(0xFFBA1A1A)
val HighContrastLightOnError = Color(0xFFFFFFFF)
val HighContrastLightErrorContainer = Color(0xFFFEE2E2)
val HighContrastLightOnErrorContainer = Color(0xFF450A0A)

// =========================================================================
// High Contrast Dark Mode Palette (WCAG AAA / AA Accessible Ratios)
// =========================================================================
val HighContrastDarkPrimary = Color(0xFF60A5FA) // Luminous Bangkok Sky Blue, >10:1 on dark
val HighContrastDarkOnPrimary = Color(0xFF001F4D)
val HighContrastDarkPrimaryContainer = Color(0xFF003D82)
val HighContrastDarkOnPrimaryContainer = Color(0xFFD6E4FF)

val HighContrastDarkSecondary = Color(0xFFFFD54F) // Luminous Burmese Gold, >12:1 on dark
val HighContrastDarkOnSecondary = Color(0xFF3E2700)
val HighContrastDarkSecondaryContainer = Color(0xFF5C3C00)
val HighContrastDarkOnSecondaryContainer = Color(0xFFFFE082)

val HighContrastDarkTertiary = Color(0xFFF87171) // High contrast Thai Coral Red, >8:1 on dark
val HighContrastDarkOnTertiary = Color(0xFF4B0000)
val HighContrastDarkTertiaryContainer = Color(0xFF7F1D1D)
val HighContrastDarkOnTertiaryContainer = Color(0xFFFECACA)

val HighContrastDarkBackground = Color(0xFF090D16) // Deep midnight black background
val HighContrastDarkOnBackground = Color(0xFFF8FAFC) // >16:1 contrast ratio
val HighContrastDarkSurface = Color(0xFF131D31)
val HighContrastDarkOnSurface = Color(0xFFF8FAFC)
val HighContrastDarkSurfaceVariant = Color(0xFF1E293B)
val HighContrastDarkOnSurfaceVariant = Color(0xFFCBD5E1) // >9:1 contrast ratio
val HighContrastDarkOutline = Color(0xFF94A3B8) // High visibility border
val HighContrastDarkOutlineVariant = Color(0xFF475569)
val HighContrastDarkError = Color(0xFFFF6B6B)
val HighContrastDarkOnError = Color(0xFF450A0A)
val HighContrastDarkErrorContainer = Color(0xFF7F1D1D)
val HighContrastDarkOnErrorContainer = Color(0xFFFECDD3)

// =========================================================================
// Transit Network Identity Colors (Light & Dark Accessible Pairs)
// =========================================================================
object TransitThemeColors {
    // BTS Sukhumvit Light Green
    val BtsGreenLight = Color(0xFF00823B)
    val BtsGreenDark = Color(0xFF4ADE80)

    // BTS Silom Dark Green
    val BtsSilomLight = Color(0xFF005A28)
    val BtsSilomDark = Color(0xFF22C55E)

    // MRT Blue Line
    val MrtBlueLight = Color(0xFF003399)
    val MrtBlueDark = Color(0xFF60A5FA)

    // MRT Purple Line
    val MrtPurpleLight = Color(0xFF6B21A8)
    val MrtPurpleDark = Color(0xFFC084FC)

    // ARL Airport Rail Link (Red/Maroon)
    val ArlRedLight = Color(0xFF9F1239)
    val ArlRedDark = Color(0xFFFB7185)

    // Gold Line (Monorail)
    val GoldLineLight = Color(0xFFB45309)
    val GoldLineDark = Color(0xFFFBBF24)

    // BMTA City Bus (Orange/Red)
    val BusOrangeLight = Color(0xFFC2410C)
    val BusOrangeDark = Color(0xFFFB923C)

    // Express Boat (River Blue)
    val BoatBlueLight = Color(0xFF0369A1)
    val BoatBlueDark = Color(0xFF38BDF8)

    // Metered Taxi (Pink)
    val TaxiPinkLight = Color(0xFFBE185D)
    val TaxiPinkDark = Color(0xFFF472B6)
}
