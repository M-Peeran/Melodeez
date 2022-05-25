package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository

class DeleteTrackFromPlaylistUseCase(private val repository: PlaylistRepository) {

    suspend operator fun invoke(playlistId: Long, trackId: Long)
    = repository.deleteTrackFromPlaylist(playlistId, trackId)
}