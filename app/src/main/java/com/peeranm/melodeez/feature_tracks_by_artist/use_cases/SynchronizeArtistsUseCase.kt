package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import android.util.Log
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import com.peeranm.melodeez.feature_tracks_by_artist.model.ArtistEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext

class SynchronizeArtistsUseCase(
    private val insertArtist: InsertArtistUseCase,
    private val getArtistsFromCache: GetArtistsFromCacheUseCase,
    private val getArtistsFromStorage: GetArtistsFromStorageUseCase,
    private val deleteArtist: DeleteArtistUseCase,
    private val deleteArtists: DeleteArtistsUseCase
) {
    suspend operator fun invoke(): Flow<DataState<List<Artist>>> = flow {
        emit(DataState.Synchronizing)
        val newArtists = getArtistsFromStorage()

        if (newArtists.isEmpty()) {
            deleteArtists()
            emit(DataState.Failure("No Artists found on the device storage"))
            return@flow
        }

        val cachedArtists = getArtistsFromCache()
        if (cachedArtists.isEmpty()) {
            Log.i("APP_LOGS", "NO ARTISTS IN CACHE!")
            newArtists.forEach { insertArtist(it) }
            emit(DataState.SynchronizationCompleted)
            return@flow
        }

        val cachedArtistMap = mutableMapOf<String, Artist>().apply {
            cachedArtists.forEach { artist -> put(artist.name, artist) }
        }

        val newArtistsMap = mutableMapOf<String, Artist>().apply {
            newArtists.forEach { artist -> put(artist.name, artist) }
        }

        for ((artistName, artist) in cachedArtistMap) {
            if (!newArtistsMap.containsKey(artistName)) {
                deleteArtist(artist)
            }
        }

        for ((artistName, artist) in newArtistsMap) {
            if (!cachedArtistMap.containsKey(artistName)) {
                insertArtist(artist)
            }
        }
        emit(DataState.SynchronizationCompleted)
    }
}