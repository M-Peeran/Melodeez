package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.AlbumRepository
import com.peeranm.melodeez.feature_music_playback.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class GetAlbumsFromCacheForUiUseCase(private val repository: AlbumRepository) {
    operator fun invoke(): Flow<List<Album>> {
        return repository.getAlbumsAsFlow()
    }
}