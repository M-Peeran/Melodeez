package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import android.util.Log
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SynchronizeAlbumsUseCase(
    private val getAlbumsFromStorage: GetAlbumsFromStorageUseCase,
    private val getAlbumsFromCache: GetAlbumsFromCacheUseCase,
    private val insertAlbum: InsertAlbumUseCase,
    private val deleteAlbum: DeleteAlbumUseCase,
    private val deleteAlbums: DeleteAlbumsUseCase
) {
    suspend operator fun invoke(): Flow<DataState<List<Album>>> = flow {
        emit(DataState.Synchronizing)
        val newAlbums = getAlbumsFromStorage()

        if (newAlbums.isEmpty()) {
            deleteAlbums()
            emit(DataState.Failure("No Albums found on the device storage"))
            return@flow
        }

        val cachedAlbums = getAlbumsFromCache()
        if (cachedAlbums.isEmpty()) {
            Log.i("APP_LOGS", "NO ALBUMS IN CACHE!")
            newAlbums.forEach { insertAlbum(it) }
            emit(DataState.SynchronizationCompleted)
            return@flow
        }

        val cachedAlbumsMap = mutableMapOf<String, Album>().apply {
            cachedAlbums.forEach { album -> put(album.name, album) }
        }

        val newAlbumsMap = mutableMapOf<String, Album>().apply {
            newAlbums.forEach { album -> put(album.name, album) }
        }

        for ((albumName, album) in cachedAlbumsMap) {
            if (!newAlbumsMap.containsKey(albumName)) {
                deleteAlbum(album)
            }
        }

        for ((albumName, album) in newAlbumsMap) {
            if (!cachedAlbumsMap.containsKey(albumName)) {
                insertAlbum(album)
            }
        }
        emit(DataState.SynchronizationCompleted)
    }
}