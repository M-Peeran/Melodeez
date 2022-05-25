package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.add_track

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.core.utils.DataState
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases.TrackUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTracksViewModel @Inject constructor(
    private val tracksUseCases: TrackUseCases,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val selectedTracks = mutableMapOf<Long, Boolean>()

    val tracksState: StateFlow<List<Track>>
    get() = tracksUseCases.getTracksFromCacheForUi().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isAnyTrackSelected = MutableStateFlow(false)
    val isAnyTrackSelected: StateFlow<Boolean> = _isAnyTrackSelected

    fun onEvent(event: Event) {
        when (event) {
            is Event.AddSelectedTracksToPlaylist -> {
                viewModelScope.launch {
                    val playlist = playlistUseCases.getPlaylist(event.playlistId ?: -1)
                    val trackIds = selectedTracks.keys.toList()
                    if (playlist != null && event.playlistId != null) {
                        playlistUseCases.insertTracksToPlaylist(trackIds, event.playlistId)
                    } else {
                        playlistUseCases.insertTracksToLastCreatedPlaylist(trackIds)
                    }
                }
            }

            is Event.ToggleTrackSelection -> {
                val isSelectedAlready = selectedTracks[event.trackId] ?: false
                when {
                    !isSelectedAlready -> selectedTracks[event.trackId] = true
                    !event.isSelected -> selectedTracks.remove(event.trackId)
                }
                _isAnyTrackSelected.value = selectedTracks.isNotEmpty()
            }

            is Event.Clear -> selectedTracks.clear()
        }
    }
}