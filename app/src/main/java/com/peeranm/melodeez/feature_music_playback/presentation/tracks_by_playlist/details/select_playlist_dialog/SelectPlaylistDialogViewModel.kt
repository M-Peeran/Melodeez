package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.select_playlist_dialog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SelectPlaylistDialogViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _playlists = MutableStateFlow<List<Playlist>?>(null)
    val playlists: StateFlow<List<Playlist>?> = _playlists

    fun onEvent(event: Event) {
        when (event) {
            is Event.GetPlaylists -> viewModelScope.launch {
                _playlists.value = playlistUseCases.getPlaylists()
            }

            is Event.InsertTrackToPlaylist -> viewModelScope.launch {
                playlistUseCases.insertTrackToPlaylist(event.playlistId, event.trackId)
            }

        }
    }
}