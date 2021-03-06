package com.peeranm.melodeez.feature_music_playback.data.repository

import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow


interface TrackRepository {

    suspend fun insert(track: Track): Long

    suspend fun deleteTrack(trackId: Long): Int

    suspend fun getTrack(trackId: Long): Track?

    fun getTracksAsFlow(): Flow<List<Track>>

    suspend fun getTracks(): List<Track>

    suspend fun deleteTracks(): Int
}