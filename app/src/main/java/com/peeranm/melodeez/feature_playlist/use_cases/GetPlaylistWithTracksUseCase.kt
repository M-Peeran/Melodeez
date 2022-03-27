package com.peeranm.melodeez.feature_playlist.use_cases

import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetPlaylistWithTracksUseCase(private val repository: PlaylistRepository) {
    suspend operator fun invoke(playlistKey: String): Pair<Playlist, List<Track>>
    = withContext(Dispatchers.IO) { repository.getPlaylistWithTracks(playlistKey) }
}