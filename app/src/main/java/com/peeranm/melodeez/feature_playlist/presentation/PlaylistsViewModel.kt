package com.peeranm.melodeez.feature_playlist.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_tracks.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    val playlists: LiveData<List<Playlist>>
    get() = playlistUseCases.getPlaylistsForUi()

    suspend fun insertPlaylist(playlist: Playlist) = playlistUseCases.insertPlaylist(playlist)

    suspend fun insertTrackToPlaylist(playlist: Playlist, track: Track) {
        val trackRef = PlaylistTrackCrossRef(playlist.name, track.uri.toString())
        playlistUseCases.insertTrackToPlaylist(trackRef)
        playlist.noOfTracks += 1
        Log.i("APP_LOGS", playlist.noOfTracks.toString())
        playlistUseCases.updatePlaylist(playlist)
    }

    suspend fun deletePlaylist(key: String) = playlistUseCases.deletePlaylist(key)

}