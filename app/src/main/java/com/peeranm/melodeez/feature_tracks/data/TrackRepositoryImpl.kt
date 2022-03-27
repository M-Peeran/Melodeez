package com.peeranm.melodeez.feature_tracks.data

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.utils.TrackMapper

class TrackRepositoryImpl(
    private val trackDao: TrackDao,
    private val trackMapper: TrackMapper
) : TrackRepository {

    override suspend fun insert(track: Track) {
        trackDao.insertTrack(trackMapper.toEntity(track))
    }

    override suspend fun update(track: Track) {
        trackDao.insertTrack(trackMapper.toEntity(track))
    }

    override suspend fun deleteTrackByKey(trackUri: String) {
        trackDao.deleteTrack(trackUri)
    }

    override suspend fun getTrack(trackUri: String): Track {
        return trackMapper.fromEntity(trackDao.getTrack(trackUri))
    }

    override suspend fun getTracks(): List<Track> {
        return trackMapper.fromEntities(trackDao.getTracks())
    }

    override suspend fun deleteTracks() {
        trackDao.deleteTracks()
    }
}