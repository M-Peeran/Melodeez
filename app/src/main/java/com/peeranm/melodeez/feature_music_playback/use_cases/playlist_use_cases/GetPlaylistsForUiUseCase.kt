package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import kotlinx.coroutines.flow.Flow

class GetPlaylistsForUiUseCase(private val repository: PlaylistRepository) {

    operator fun invoke(): Flow<List<Playlist>>
    = repository.getPlaylistsAsFlow()
}