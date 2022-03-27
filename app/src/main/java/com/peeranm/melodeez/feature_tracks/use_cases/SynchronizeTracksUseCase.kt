package com.peeranm.melodeez.feature_tracks.use_cases

import android.net.Uri
import android.util.Log
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SynchronizeTracksUseCase(
    private val insertTrack: InsertTrackUseCase,
    private val deleteTrack: DeleteTrackUseCase,
    private val deleteTracks: DeleteTracksUseCase,
    private val getTracksFromCache: GetTracksFromCacheUseCase,
    private val getTracksFromStorage: GetTracksFromStorageUseCase
){
    suspend operator fun invoke(): Flow<DataState<List<Track>>> = flow {
        emit(DataState.Synchronizing)
        val newTracks = getTracksFromStorage()

        if (newTracks.isEmpty()) {
            deleteTracks()
            emit(DataState.Failure("No tracks found in the device storage"))
            return@flow
        }

        val cachedTracks = getTracksFromCache()

        if (cachedTracks.isEmpty()) {
            Log.i("APP_LOGS", "NO TRACKS IN CACHE!")
            newTracks.forEach { insertTrack(it) }
            emit(DataState.SynchronizationCompleted)
            return@flow
        }

        val cachedTracksMap = mutableMapOf<Uri, Track>().apply {
            cachedTracks.forEach { track -> put(track.uri, track) }
        }

        val newTracksMap = mutableMapOf<Uri, Track>().apply {
            newTracks.forEach { track -> put(track.uri, track) }
        }

        for ((uri, _) in cachedTracksMap) {
            if (!newTracksMap.containsKey(uri)) {
                deleteTrack(uri)
            }
        }

        for ((uri, track) in newTracksMap) {
            if (!cachedTracksMap.containsKey(uri)) {
                insertTrack(track)
            }
        }
        emit(DataState.SynchronizationCompleted)
    }
}