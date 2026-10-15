package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_places")
data class SavedPlaceEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val nameMm: String,
    val nameTh: String,
    val nameEn: String,
    val latitude: Double,
    val longitude: Double,
    val addressMm: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "saved_routes")
data class SavedRouteEntity(
    @PrimaryKey
    val id: String,
    val titleMm: String,
    val titleTh: String,
    val totalTimeMinutes: Int,
    val fareThbText: String,
    val stepsSummaryMm: String,
    val savedAtTimestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "user_submitted_reports")
data class UserReportEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val placeOrRouteName: String,
    val issueType: String,
    val details: String,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "Pending Review"
)
