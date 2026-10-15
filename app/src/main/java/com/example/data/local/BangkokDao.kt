package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BangkokDao {
    @Query("SELECT * FROM saved_places ORDER BY savedAtTimestamp DESC")
    fun getAllSavedPlaces(): Flow<List<SavedPlaceEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePlace(place: SavedPlaceEntity)

    @Query("DELETE FROM saved_places WHERE id = :placeId")
    suspend fun deletePlace(placeId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM saved_places WHERE id = :placeId)")
    suspend fun isPlaceSaved(placeId: String): Boolean

    @Query("SELECT * FROM saved_routes ORDER BY savedAtTimestamp DESC")
    fun getAllSavedRoutes(): Flow<List<SavedRouteEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRoute(route: SavedRouteEntity)

    @Query("DELETE FROM saved_routes WHERE id = :routeId")
    suspend fun deleteRoute(routeId: String)

    @Query("SELECT * FROM user_submitted_reports ORDER BY timestamp DESC")
    fun getAllReports(): Flow<List<UserReportEntity>>

    @Insert
    suspend fun insertReport(report: UserReportEntity)
}
