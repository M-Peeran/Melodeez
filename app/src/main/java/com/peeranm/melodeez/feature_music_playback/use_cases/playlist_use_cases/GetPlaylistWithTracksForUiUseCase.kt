package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

class GetPlaylistWithTracksForUiUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(playlistId: Long): Flow<PlaylistWithTracks?>
    = repository.getPlaylistWithTracksAsFlow(playlistId)
}