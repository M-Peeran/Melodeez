package com.peeranm.melodeez.feature_tracks.data

import androidx.room.*
import com.peeranm.melodeez.feature_tracks.model.TrackEntity

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(trackEntity: TrackEntity)

    @Query("delete from tracks where uri =:trackUri")
    suspend fun deleteTrack(trackUri: String)

    @Query("select * from tracks where uri =:trackUri")
    suspend fun getTrack(trackUri: String): TrackEntity

    @Query("select * from tracks")
    suspend fun getTracks(): List<TrackEntity>

    @Query("delete from tracks")
    suspend fun deleteTracks()

}