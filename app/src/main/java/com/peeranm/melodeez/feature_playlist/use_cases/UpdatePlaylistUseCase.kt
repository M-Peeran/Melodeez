package com.peeranm.melodeez.feature_playlist.use_cases

import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import com.peeranm.melodeez.feature_playlist.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UpdatePlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlist: Playlist)
    = withContext(Dispatchers.IO) { repository.updatePlaylist(playlist) }
}