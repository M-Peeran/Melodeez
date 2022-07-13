package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsDialogViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases,
    private val playbackSourceHelper: PlaybackSourceHelper
) : ViewModel() {

    private val _message = MutableStateFlow("")
    val message: StateFlow<String> = _message

    fun addToQueue(track: Track) {
        val tracks = playbackSourceHelper.getCurrentSource()
        val isNotEmpty = tracks.isNotEmpty()
        val trackNotExists = !tracks.contains(track)
        when {
            isNotEmpty && trackNotExists -> {
                (tracks as MutableList).add(track)
                playbackSourceHelper.setCurrentSource(tracks)
                _message.value = "Added successfully"
            }
            !isNotEmpty -> _message.value = "Failed : Queue is empty"
            !trackNotExists -> _message.value = "Already added to the queue!"
        }
    }

    fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        viewModelScope.launch {
            playlistUseCases.deleteTrackFromPlaylist(
                playlistId = playlistId,
                trackId = trackId
            )
        }.invokeOnCompletion { _message.value = "Deleted successfully!" }
    }

}