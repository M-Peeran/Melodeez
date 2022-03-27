package com.peeranm.melodeez.feature_playlist.use_cases

import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertTrackToPlaylistUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(crossRef: PlaylistTrackCrossRef)
    = withContext(Dispatchers.IO) { repository.insertTrackToPlaylist(crossRef) }
}