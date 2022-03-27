package com.peeranm.melodeez.feature_playlist.use_cases

import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTrackFromPlaylistUseCase(
    private val repository: PlaylistRepository,
    private val getPlaylist: GetPlaylistUseCase,
    private val updatePlaylist: UpdatePlaylistUseCase
) {
    suspend operator fun invoke(crossRef: PlaylistTrackCrossRef)
    = withContext(Dispatchers.IO) {
        repository.deleteTrackFromPlaylist(crossRef)
        val playlist = getPlaylist(crossRef.name) // name is the primary key belonging to the playlist
        val trackCount = playlist.noOfTracks
        playlist.noOfTracks = trackCount-1
        updatePlaylist(playlist) // update playlist with decreased track count
    }
}