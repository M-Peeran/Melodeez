package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.Playlist

class GetPlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long): Playlist?
    = repository.getPlaylist(playlistId)
}