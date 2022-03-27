package com.peeranm.melodeez.feature_playlist.presentation.details

import android.util.Log
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackSourceHelper
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_tracks.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsDialogViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases,
    private val playbackSourceHelper: PlaybackSourceHelper
) : ViewModel() {

    fun addToQueue(track: Track): Boolean {
        // Potential to be a use case
        val tracks = playbackSourceHelper.getCurrentSource() as MutableList
        val isNotEmpty = (tracks.isNotEmpty())
        val trackNotExists = !tracks.contains(track)
        if (isNotEmpty && trackNotExists) {
            tracks.add(track)
            playbackSourceHelper.setCurrentSource(tracks)
        }
        return isNotEmpty
    }

    suspend fun getPlaylists() = playlistUseCases.getPlaylists()

    suspend fun insertTrackToPlaylist(playlist: Playlist, track: Track) {
        val trackRef = PlaylistTrackCrossRef(playlist.name, track.uri.toString())
        playlistUseCases.insertTrackToPlaylist(trackRef)
        playlist.noOfTracks += 1
        Log.i("APP_LOGS", playlist.noOfTracks.toString())
        playlistUseCases.updatePlaylist(playlist)
    }

    suspend fun deleteTrackFromPlaylist(playlistKey: String, trackUri: String) {
        val trackRef = PlaylistTrackCrossRef(playlistKey, trackUri)
        playlistUseCases.deleteTrackFromPlaylist(trackRef)
    }
}