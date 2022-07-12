package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.data.repository.AlbumRepository
import com.peeranm.melodeez.feature_music_playback.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SynchronizeAlbumsUseCase(
    private val musicSource: MusicSource,
    private val albumRepository: AlbumRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val newAlbums = musicSource.getAlbumsFromStorage()

        if (newAlbums.isEmpty()) {
            albumRepository.deleteAlbums()
            return@withContext
        }

        val cachedAlbums = albumRepository.getAlbums()
        if (cachedAlbums.isEmpty()) {
            newAlbums.forEach { albumRepository.insertAlbum(it) }
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
                albumRepository.deleteAlbum(album)
            }
        }

        for ((albumName, album) in newAlbumsMap) {
            if (!cachedAlbumsMap.containsKey(albumName)) {
                albumRepository.insertAlbum(album)
            }
        }
    }
}