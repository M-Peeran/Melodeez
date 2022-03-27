package com.peeranm.melodeez.feature_tracks.data


import com.peeranm.melodeez.feature_tracks.model.Track

interface TrackRepository {

    suspend fun insert(track: Track)

    suspend fun update(track: Track)

    suspend fun deleteTrackByKey(trackUri: String)

    suspend fun getTrack(trackUri: String): Track

    suspend fun getTracks(): List<Track>

    suspend fun deleteTracks()
}