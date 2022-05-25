package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SynchronizeAlbumsUseCase(
    private val getAlbumsFromStorage: GetAlbumsFromStorageUseCase,
    private val getAlbumsFromCache: GetAlbumsFromCacheUseCase,
    private val insertAlbum: InsertAlbumUseCase,
    private val deleteAlbum: DeleteAlbumUseCase,
    private val deleteAlbums: DeleteAlbumsUseCase
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val newAlbums = getAlbumsFromStorage()

        if (newAlbums.isEmpty()) {
            deleteAlbums()
            return@withContext
        }

        val cachedAlbums = getAlbumsFromCache()
        if (cachedAlbums.isEmpty()) {
            newAlbums.forEach { insertAlbum(it) }
            return@withContext
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
    }
}