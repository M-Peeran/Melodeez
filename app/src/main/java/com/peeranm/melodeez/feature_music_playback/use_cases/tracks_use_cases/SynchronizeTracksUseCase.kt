package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import android.util.Log
import com.peeranm.melodeez.core.utils.DataState
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SynchronizeTracksUseCase(
    private val insertTrack: InsertTrackUseCase,
    private val deleteTrack: DeleteTrackUseCase,
    private val deleteTracks: DeleteTracksUseCase,
    private val getTracksFromCache: GetTracksFromCacheUseCase,
    private val getTracksFromStorage: GetTracksFromStorageUseCase
){
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val newTracks = getTracksFromStorage()

        if (newTracks.isEmpty()) {
            deleteTracks()
            return@withContext
        }

        val cachedTracks = getTracksFromCache()

        if (cachedTracks.isEmpty()) {
            Log.i("APP_LOGS", "NO TRACKS IN CACHE!")
            newTracks.forEach { insertTrack(it) }
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
                deleteTrack(track.trackId)
            }
        }

        for ((uri, track) in newTracksMap) {
            if (!cachedTracksMap.containsKey(uri)) {
                insertTrack(track)
            }
        }
    }
}