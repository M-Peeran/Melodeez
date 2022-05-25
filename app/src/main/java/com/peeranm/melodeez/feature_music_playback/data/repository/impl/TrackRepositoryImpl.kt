package com.peeranm.melodeez.feature_music_playback.data.repository.impl

import com.peeranm.melodeez.feature_music_playback.data.local.daos.TrackDao
import com.peeranm.melodeez.feature_music_playback.data.repository.TrackRepository
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow

class TrackRepositoryImpl(private val trackDao: TrackDao, ) : TrackRepository {

    override suspend fun insert(track: Track) {
        trackDao.insertTrack(track)
    }

    override suspend fun deleteTrack(trackId: Long) {
        trackDao.deleteTrack(trackId)
    }

    override suspend fun getTrack(trackId: Long): Track {
        return trackDao.getTrack(trackId)
    }

    override suspend fun getTracks(): List<Track> {
        return trackDao.getTracks()
    }

    override fun getTracksAsFlow(): Flow<List<Track>> {
       return trackDao.getTracksAsFlow()
    }

    override suspend fun deleteTracks() {
        trackDao.deleteTracks()
    }
}