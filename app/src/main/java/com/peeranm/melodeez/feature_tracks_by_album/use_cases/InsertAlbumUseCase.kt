package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import com.peeranm.melodeez.feature_tracks_by_album.data.AlbumRepository
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertAlbumUseCase(private val repository: AlbumRepository) {
    suspend operator fun invoke(album: Album) = withContext(Dispatchers.IO) {
        repository.insertAlbum(album)
    }
}