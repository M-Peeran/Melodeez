package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks

class GetPlaylistWithTracksUseCase(private val repository: PlaylistRepository) {

    suspend operator fun invoke(playlistId: Long): PlaylistWithTracks?
    = repository.getPlaylistWithTracks(playlistId)

}