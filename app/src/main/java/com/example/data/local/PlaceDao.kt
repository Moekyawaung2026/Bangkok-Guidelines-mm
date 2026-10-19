package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for querying cached transport places and
 * filtering locations by bounding-box coordinate ranges for nearby search.
 */
@Dao
interface PlaceDao {

    /**
     * Queries transport locations within specified latitude and longitude coordinate ranges as a reactive Flow.
     */
    @Query("SELECT * FROM transport_places WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng ORDER BY latitude ASC, longitude ASC")
    fun getPlacesInCoordinateRange(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<TransportPlaceEntity>>

    /**
     * Direct suspend query for transport locations within specified coordinate ranges.
     */
    @Query("SELECT * FROM transport_places WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getPlacesInCoordinateRangeList(
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<TransportPlaceEntity>

    /**
     * Queries transport locations by specific transport type (e.g. BTS, MRT, BUS) within coordinate ranges.
     */
    @Query("SELECT * FROM transport_places WHERE type = :type AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    fun getPlacesByTypeInCoordinateRange(
        type: String,
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): Flow<List<TransportPlaceEntity>>

    /**
     * Direct suspend query for transport locations by type within coordinate ranges.
     */
    @Query("SELECT * FROM transport_places WHERE type = :type AND latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng")
    suspend fun getPlacesByTypeInCoordinateRangeList(
        type: String,
        minLat: Double,
        maxLat: Double,
        minLng: Double,
        maxLng: Double
    ): List<TransportPlaceEntity>

    /**
     * Bulk inserts a collection of transport places into the offline cache.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaces(places: List<TransportPlaceEntity>)

    /**
     * Inserts or updates a single transport place in the offline cache.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlace(place: TransportPlaceEntity)

    /**
     * Retrieves a cached transport location by its unique ID.
     */
    @Query("SELECT * FROM transport_places WHERE id = :id LIMIT 1")
    suspend fun getPlaceById(id: String): TransportPlaceEntity?

    /**
     * Returns all cached transport places as a reactive Flow.
     */
    @Query("SELECT * FROM transport_places ORDER BY nameEn ASC")
    fun getAllPlaces(): Flow<List<TransportPlaceEntity>>

    /**
     * Returns all cached transport places synchronously.
     */
    @Query("SELECT * FROM transport_places ORDER BY nameEn ASC")
    suspend fun getAllPlacesList(): List<TransportPlaceEntity>

    /**
     * Queries cached places by transport type (BTS, MRT, BUS, SRT, etc.).
     */
    @Query("SELECT * FROM transport_places WHERE type = :type ORDER BY nameEn ASC")
    fun getPlacesByType(type: String): Flow<List<TransportPlaceEntity>>

    /**
     * Searches cached transport places matching the search query in Burmese, Thai, or English.
     */
    @Query("SELECT * FROM transport_places WHERE nameMm LIKE '%' || :query || '%' OR nameTh LIKE '%' || :query || '%' OR nameEn LIKE '%' || :query || '%' OR romanized LIKE '%' || :query || '%' ORDER BY nameEn ASC")
    fun searchPlaces(query: String): Flow<List<TransportPlaceEntity>>

    /**
     * Counts the total number of cached transport places in the local database.
     */
    @Query("SELECT COUNT(*) FROM transport_places")
    suspend fun getPlacesCount(): Int

    /**
     * Deletes a cached transport place by its unique ID.
     */
    @Query("DELETE FROM transport_places WHERE id = :id")
    suspend fun deletePlaceById(id: String)

    /**
     * Clears all cached transport places from the table.
     */
    @Query("DELETE FROM transport_places")
    suspend fun clearAllPlaces()
}
