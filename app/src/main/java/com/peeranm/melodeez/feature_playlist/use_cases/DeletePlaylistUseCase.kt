package com.peeranm.melodeez.feature_playlist.use_cases

import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import com.peeranm.melodeez.feature_playlist.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeletePlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistKey: String) = withContext(Dispatchers.IO) {
        // We must delete all tracks that playlist is referencing before deleting the playlist itself.
        repository.deleteTracksFromPlaylist(playlistKey)
        repository.deletePlaylistByKey(playlistKey)
    }
}