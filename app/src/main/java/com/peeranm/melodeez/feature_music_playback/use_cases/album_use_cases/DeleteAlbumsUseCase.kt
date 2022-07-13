package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAlbumsUseCase(private val repository: AlbumRepository) {
    suspend operator fun invoke(): Int = withContext(Dispatchers.IO)  {
        repository.deleteAlbums()
    }
}