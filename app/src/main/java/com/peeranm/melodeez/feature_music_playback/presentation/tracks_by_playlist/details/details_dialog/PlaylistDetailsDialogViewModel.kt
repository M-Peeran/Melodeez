package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    private val _isSuccessful = MutableStateFlow<Boolean?>(null)
    val isSuccessful: StateFlow<Boolean?> = _isSuccessful

    fun onEvent(event: Event) {
        when (event) {
            is Event.DeleteTrackFromPlaylist -> {
                viewModelScope.launch {
                    playlistUseCases.deleteTrackFromPlaylist(
                        playlistId = event.playlistId,
                        trackId = event.trackId
                    )
                }
            }
            is Event.AddToQueue -> {
                val tracks = playbackSourceHelper.getCurrentSource()
                val isNotEmpty = (tracks.isNotEmpty())
                val trackNotExists = !tracks.contains(event.track)
                if (isNotEmpty && trackNotExists) {
                    (tracks as MutableList).add(event.track)
                    playbackSourceHelper.setCurrentSource(tracks)
                }
                _isSuccessful.value = isNotEmpty
            }
        }
    }

}