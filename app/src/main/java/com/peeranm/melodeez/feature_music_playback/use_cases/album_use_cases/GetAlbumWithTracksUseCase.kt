package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.AlbumRepository
import com.peeranm.melodeez.feature_music_playback.model.AlbumWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAlbumWithTracksUseCase(private val repository: AlbumRepository) {
    suspend operator fun invoke(albumId: Long): AlbumWithTracks? = withContext(Dispatchers.IO) {
        repository.getAlbumWithTracks(albumId)
    }
}