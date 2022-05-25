package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeletePlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long): Int
    = repository.deletePlaylist(playlistId)
}