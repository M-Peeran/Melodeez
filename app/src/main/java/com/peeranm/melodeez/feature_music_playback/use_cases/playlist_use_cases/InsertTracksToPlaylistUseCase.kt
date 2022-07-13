package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository

class InsertTracksToPlaylistUseCase(private val repository: PlaylistRepository) {

    suspend operator fun invoke(tracksIds: List<Long>, playlistId: Long)
    = repository.insertTracksToPlaylist(tracksIds, playlistId)
}