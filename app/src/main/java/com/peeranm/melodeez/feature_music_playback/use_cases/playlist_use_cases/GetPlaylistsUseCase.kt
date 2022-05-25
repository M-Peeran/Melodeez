package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPlaylistsUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(): List<Playlist> = withContext(Dispatchers.IO) {
        repository.getPlaylists()
    }
}