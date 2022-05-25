package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.PlaylistTrackCrossRef
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertTracksToLastCreatedPlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(tracksIds: List<Long>)
    = repository.insertTracksToLastCreatedPlaylist(tracksIds)
}