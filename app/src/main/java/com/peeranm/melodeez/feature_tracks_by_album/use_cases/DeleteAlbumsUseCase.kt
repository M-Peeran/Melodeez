package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import com.peeranm.melodeez.feature_tracks_by_album.data.AlbumRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteAlbumsUseCase(private val repository: AlbumRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.deleteAlbums()
    }
}