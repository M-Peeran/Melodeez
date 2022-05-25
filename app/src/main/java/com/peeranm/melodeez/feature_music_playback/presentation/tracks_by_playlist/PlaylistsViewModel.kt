package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>>(emptyList())
    val playlists: StateFlow<List<Playlist>> = _playlists

    fun onEvent(event: Event) {
        when (event) {
            is Event.GetPlaylists -> playlistUseCases.getPlaylistsForUi()
                .onEach { _playlists.value = it }
                .launchIn(viewModelScope)

            is Event.DeletePlaylist -> viewModelScope.launch {
                playlistUseCases.deletePlaylist(event.playlistId)
            }
        }
    }

}