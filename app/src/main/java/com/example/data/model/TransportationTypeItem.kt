package com.example.data.model

/**
 * Data class representing a core transportation type in Bangkok (BTS, MRT, Bus, SRT, Taxi).
 * Holds comprehensive bilingual information, fare estimates, operational hours,
 * route coverage highlights, accessibility features, and navigation routing targets.
 *
 * Design and create by Gemini, Development by Moekyawaung, GitHub -dev-moe-kyaw-aung
 */
data class TransportationTypeItem(
    val id: String, // "BTS", "MRT", "BUS", "SRT", "TAXI"
    val type: TransportType,
    val nameMm: String,
    val nameTh: String,
    val nameEn: String,
    val subtitleMm: String,
    val subtitleTh: String,
    val subtitleEn: String,
    val descriptionMm: String,
    val descriptionTh: String,
    val descriptionEn: String,
    val operatingHours: String,
    val fareRangeMm: String,
    val fareRangeTh: String,
    val fareRangeEn: String,
    val keyStationsMm: String,
    val keyStationsTh: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val badgeMm: String,
    val badgeTh: String,
    val travelTipsMm: String,
    val travelTipsTh: String,
    val highlightFeaturesMm: List<String> = emptyList(),
    val highlightFeaturesTh: List<String> = emptyList(),
    val isPopular: Boolean = true
)
