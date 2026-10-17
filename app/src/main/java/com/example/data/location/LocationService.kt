package com.example.data.location

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Looper
import androidx.core.content.ContextCompat
import com.example.data.model.TransportPlace
import com.example.data.model.TransportType
import com.example.data.model.TransitHub
import com.google.android.gms.location.*
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.*

/**
 * LocationService handles GPS detection, coordinate distance & bearing calculations,
 * and nearby place filtering within a defined radius using Google Play Services FusedLocationProviderClient.
 */
class LocationService(private val context: Context) {

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)

    private val locationManager: LocationManager? =
        context.getSystemService(Context.LOCATION_SERVICE) as? LocationManager

    // Bangkok Victory Monument default coordinate
    companion object {
        const val DEFAULT_BANGKOK_LAT = 13.7628
        const val DEFAULT_BANGKOK_LNG = 100.5372
        const val EARTH_RADIUS_METERS = 6371000.0
    }

    /**
     * Checks if fine or coarse location permission is granted.
     */
    fun hasLocationPermission(): Boolean {
        val fineGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val coarseGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return fineGranted || coarseGranted
    }

    /**
     * Checks if GPS or Network Location provider is enabled on the device hardware.
     */
    fun isGpsEnabled(): Boolean {
        val lm = locationManager ?: return false
        val gpsEnabled = runCatching { lm.isProviderEnabled(LocationManager.GPS_PROVIDER) }.getOrDefault(false)
        val networkEnabled = runCatching { lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER) }.getOrDefault(false)
        return gpsEnabled || networkEnabled
    }

    /**
     * Retrieves the last known location from Google Play Services Fused Location Client.
     */
    @SuppressLint("MissingPermission")
    suspend fun getLastKnownLocation(): Location? {
        if (!hasLocationPermission()) return null

        return suspendCancellableCoroutine { continuation ->
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { loc ->
                        continuation.resume(loc)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
                    .addOnCanceledListener {
                        continuation.resume(null)
                    }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    /**
     * Requests a fresh high-accuracy current location from Google Play Services.
     */
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(highAccuracy: Boolean = true): Location? {
        if (!hasLocationPermission()) return null

        val priority = if (highAccuracy) {
            Priority.PRIORITY_HIGH_ACCURACY
        } else {
            Priority.PRIORITY_BALANCED_POWER_ACCURACY
        }

        return suspendCancellableCoroutine { continuation ->
            try {
                val cancellationTokenSource = com.google.android.gms.tasks.CancellationTokenSource()

                continuation.invokeOnCancellation {
                    cancellationTokenSource.cancel()
                }

                fusedLocationClient.getCurrentLocation(priority, cancellationTokenSource.token)
                    .addOnSuccessListener { loc ->
                        if (loc != null) {
                            continuation.resume(loc)
                        } else {
                            // Fallback to last known location if fresh location is null
                            fusedLocationClient.lastLocation
                                .addOnSuccessListener { lastLoc -> continuation.resume(lastLoc) }
                                .addOnFailureListener { continuation.resume(null) }
                        }
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            } catch (e: SecurityException) {
                continuation.resume(null)
            }
        }
    }

    /**
     * Streams continuous location updates as a Kotlin Flow using callbackFlow.
     */
    @SuppressLint("MissingPermission")
    fun getLocationUpdatesFlow(intervalMs: Long = 10000L, minDistanceMeters: Float = 10f): Flow<Location> = callbackFlow {
        if (!hasLocationPermission()) {
            close()
            return@callbackFlow
        }

        val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, intervalMs)
            .setMinUpdateDistanceMeters(minDistanceMeters)
            .setWaitForAccurateLocation(false)
            .build()

        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { trySend(it) }
            }
        }

        try {
            fusedLocationClient.requestLocationUpdates(request, callback, Looper.getMainLooper())
        } catch (e: SecurityException) {
            close(e)
        }

        awaitClose {
            fusedLocationClient.removeLocationUpdates(callback)
        }
    }

    // ==========================================
    // Coordinate & Geodesic Calculations
    // ==========================================

    /**
     * Calculates geodesic distance between two points in meters using Haversine formula.
     */
    fun calculateDistanceMeters(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return EARTH_RADIUS_METERS * c
    }

    /**
     * Calculates the initial bearing from point 1 to point 2 in degrees (0..360).
     */
    fun calculateBearing(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Float {
        val phi1 = Math.toRadians(lat1)
        val phi2 = Math.toRadians(lat2)
        val deltaLambda = Math.toRadians(lon2 - lon1)

        val y = sin(deltaLambda) * cos(phi2)
        val x = cos(phi1) * sin(phi2) - sin(phi1) * cos(phi2) * cos(deltaLambda)
        val bearingRad = atan2(y, x)
        val bearingDeg = Math.toDegrees(bearingRad)
        return ((bearingDeg + 360) % 360).toFloat()
    }

    /**
     * Returns true if target coordinate is within the defined radius from origin coordinate.
     */
    fun isWithinRadius(
        originLat: Double,
        originLng: Double,
        targetLat: Double,
        targetLng: Double,
        radiusMeters: Double
    ): Boolean {
        val distance = calculateDistanceMeters(originLat, originLng, targetLat, targetLng)
        return distance <= radiusMeters
    }

    /**
     * Human-readable distance format (e.g., "350 m" or "2.4 km").
     */
    fun formatDistance(distanceMeters: Double): String {
        return if (distanceMeters < 1000.0) {
            "${distanceMeters.roundToInt()} m"
        } else {
            String.format(java.util.Locale.US, "%.1f km", distanceMeters / 1000.0)
        }
    }

    // ==========================================
    // Nearby Place Filtering
    // ==========================================

    /**
     * Filters a list of TransportPlace within the given radiusMeters of the user coordinate.
     * Optionally filters by TransportType.
     * Returns sorted by distance ascending.
     */
    fun filterNearbyPlaces(
        places: List<TransportPlace>,
        userLat: Double,
        userLng: Double,
        radiusMeters: Double,
        selectedType: TransportType? = null
    ): List<Pair<TransportPlace, Double>> {
        return places
            .asSequence()
            .filter { selectedType == null || it.type == selectedType }
            .map { place ->
                val dist = calculateDistanceMeters(userLat, userLng, place.latitude, place.longitude)
                Pair(place, dist)
            }
            .filter { (_, dist) -> dist <= radiusMeters }
            .sortedBy { it.second }
            .toList()
    }

    /**
     * Filters a list of TransitHub within the defined radius in meters, sorted by distance ascending.
     */
    fun filterNearbyTransitHubs(
        hubs: List<TransitHub>,
        userLat: Double,
        userLng: Double,
        radiusMeters: Double
    ): List<Pair<TransitHub, Double>> {
        return hubs
            .asSequence()
            .map { hub ->
                val dist = calculateDistanceMeters(userLat, userLng, hub.latitude, hub.longitude)
                Pair(hub, dist)
            }
            .filter { (_, dist) -> dist <= radiusMeters }
            .sortedBy { it.second }
            .toList()
    }

    /**
     * Generic nearby filter for any item with latitude and longitude extractors.
     */
    fun <T> filterNearbyItems(
        items: List<T>,
        userLat: Double,
        userLng: Double,
        radiusMeters: Double,
        getLat: (T) -> Double,
        getLng: (T) -> Double
    ): List<Pair<T, Double>> {
        return items
            .asSequence()
            .map { item ->
                val dist = calculateDistanceMeters(userLat, userLng, getLat(item), getLng(item))
                Pair(item, dist)
            }
            .filter { (_, dist) -> dist <= radiusMeters }
            .sortedBy { it.second }
            .toList()
    }
}
