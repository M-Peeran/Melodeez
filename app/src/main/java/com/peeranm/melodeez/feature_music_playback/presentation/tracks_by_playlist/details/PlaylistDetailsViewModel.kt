package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.core.ARG_PLAYLIST_ID
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _playlistWithTracks = MutableStateFlow<PlaylistWithTracks?>(null)
    val playlistWithTracks: StateFlow<PlaylistWithTracks?> = _playlistWithTracks

    private val _isDeletionSuccess = MutableStateFlow<Boolean?>(null)
    val isDeletionSuccess: StateFlow<Boolean?> = _isDeletionSuccess

    init {
        val playlistId = savedStateHandle.get<Long>(ARG_PLAYLIST_ID)
        playlistId?.let { id -> playlistUseCases.getPlaylistWithTracksForUi(id)
            .onEach { _playlistWithTracks.value = it }
            .launchIn(viewModelScope)
        }
    }

    fun onEvent(event: Event) {
        when (event) {
            is Event.DeletePlaylist -> {
                viewModelScope.launch {
                    val deletedPlaylistCount = playlistUseCases.deletePlaylist(event.playlistId)
                    _isDeletionSuccess.value = deletedPlaylistCount != 0
                }
            }
        }
    }

}