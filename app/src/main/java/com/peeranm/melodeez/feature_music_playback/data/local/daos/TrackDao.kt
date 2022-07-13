package com.peeranm.melodeez.feature_music_playback.data.local.daos

import androidx.room.*
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrack(track: Track): Long

    @Query("delete from tracks where trackId =:trackId")
    suspend fun deleteTrack(trackId: Long): Int

    @Query("select * from tracks where uri =:trackId")
    suspend fun getTrack(trackId: Long): Track?

    @Query("select * from tracks")
    suspend fun getTracks(): List<Track>

    @Query("select * from tracks")
    fun getTracksAsFlow(): Flow<List<Track>>

    @Query("delete from tracks")
    suspend fun deleteTracks(): Int

}