package com.example.data.local

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.data.model.AccessibilityInfo
import com.example.data.model.ServiceStatus
import com.example.data.model.TransportPlace
import com.example.data.model.TransportType
import com.example.data.model.VerificationStatus

/**
 * Room database entity for caching transport places offline with indexing on coordinate ranges.
 */
@Entity(
    tableName = "transport_places",
    indices = [
        Index(value = ["latitude", "longitude"]),
        Index(value = ["type"])
    ]
)
data class TransportPlaceEntity(
    @PrimaryKey
    val id: String,
    val type: String,
    val nameMm: String,
    val nameTh: String,
    val nameEn: String,
    val romanized: String = "",
    val latitude: Double,
    val longitude: Double,
    val addressMm: String = "",
    val addressTh: String = "",
    val openingTime: String = "05:00",
    val closingTime: String = "24:00",
    val fareMin: Int = 8,
    val fareMax: Int = 45,
    val phone: String = "",
    val liveStatus: String = "NORMAL",
    val verificationStatus: String = "VERIFIED",
    val wheelchair: Boolean = true,
    val elevator: Boolean = true,
    val escalator: Boolean = true,
    val source: String = "BMTA / BTS / SRT Official",
    val lastVerifiedAt: String = "2026-10-10",
    val confidenceScore: Int = 98,
    val exitDetailsMm: String = "",
    val exitDetailsTh: String = "",
    val driverPhraseTh: String = "",
    val cachedAtTimestamp: Long = System.currentTimeMillis()
)

/**
 * Maps a domain TransportPlace model to a Room TransportPlaceEntity.
 */
fun TransportPlace.toEntity(cachedAtTimestamp: Long = System.currentTimeMillis()): TransportPlaceEntity {
    return TransportPlaceEntity(
        id = id,
        type = type.name,
        nameMm = nameMm,
        nameTh = nameTh,
        nameEn = nameEn,
        romanized = romanized,
        latitude = latitude,
        longitude = longitude,
        addressMm = addressMm,
        addressTh = addressTh,
        openingTime = openingTime,
        closingTime = closingTime,
        fareMin = fareMin,
        fareMax = fareMax,
        phone = phone,
        liveStatus = liveStatus.name,
        verificationStatus = verificationStatus.name,
        wheelchair = accessibility.wheelchair,
        elevator = accessibility.elevator,
        escalator = accessibility.escalator,
        source = source,
        lastVerifiedAt = lastVerifiedAt,
        confidenceScore = confidenceScore,
        exitDetailsMm = exitDetailsMm,
        exitDetailsTh = exitDetailsTh,
        driverPhraseTh = driverPhraseTh,
        cachedAtTimestamp = cachedAtTimestamp
    )
}

/**
 * Maps a cached Room TransportPlaceEntity back to domain TransportPlace model.
 */
fun TransportPlaceEntity.toDomain(): TransportPlace {
    val parsedType = try {
        TransportType.valueOf(type)
    } catch (_: Exception) {
        TransportType.BUS
    }
    val parsedStatus = try {
        ServiceStatus.valueOf(liveStatus)
    } catch (_: Exception) {
        ServiceStatus.NORMAL
    }
    val parsedVerification = try {
        VerificationStatus.valueOf(verificationStatus)
    } catch (_: Exception) {
        VerificationStatus.VERIFIED
    }

    return TransportPlace(
        id = id,
        type = parsedType,
        nameMm = nameMm,
        nameTh = nameTh,
        nameEn = nameEn,
        romanized = romanized,
        latitude = latitude,
        longitude = longitude,
        addressMm = addressMm,
        addressTh = addressTh,
        openingTime = openingTime,
        closingTime = closingTime,
        fareMin = fareMin,
        fareMax = fareMax,
        phone = phone,
        liveStatus = parsedStatus,
        verificationStatus = parsedVerification,
        accessibility = AccessibilityInfo(
            wheelchair = wheelchair,
            elevator = elevator,
            escalator = escalator
        ),
        source = source,
        lastVerifiedAt = lastVerifiedAt,
        confidenceScore = confidenceScore,
        exitDetailsMm = exitDetailsMm,
        exitDetailsTh = exitDetailsTh,
        driverPhraseTh = driverPhraseTh
    )
}
