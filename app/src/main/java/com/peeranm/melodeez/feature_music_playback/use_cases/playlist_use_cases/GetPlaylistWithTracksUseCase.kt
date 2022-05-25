package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPlaylistWithTracksUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistId: Long): PlaylistWithTracks = withContext(Dispatchers.IO) {
        repository.getPlaylistWithTracks(playlistId)
    }
}