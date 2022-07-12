package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import android.util.Log
import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.data.repository.TrackRepository
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SynchronizeTracksUseCase(
    private val musicSource: MusicSource,
    private val trackRepository: TrackRepository
){
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val newTracks = musicSource.getTracksFromStorage()

        if (newTracks.isEmpty()) {
            trackRepository.deleteTracks()
            return@withContext
        }

        val cachedTracks = trackRepository.getTracks()

        if (cachedTracks.isEmpty()) {
            Log.i("APP_LOGS", "NO TRACKS IN CACHE!")
            newTracks.forEach { trackRepository.insert(it) }
            return@withContext
        }

        val cachedTracksMap = mutableMapOf<String, Track>().apply {
            cachedTracks.forEach { track -> put(track.uri, track) }
        }

        val newTracksMap = mutableMapOf<String, Track>().apply {
            newTracks.forEach { track -> put(track.uri, track) }
        }

        for ((uri, track) in cachedTracksMap) {
            if (!newTracksMap.containsKey(uri)) {
                trackRepository.deleteTrack(track.trackId)
            }
        }

        for ((uri, track) in newTracksMap) {
            if (!cachedTracksMap.containsKey(uri)) {
                trackRepository.insert(track)
            }
        }
    }
}