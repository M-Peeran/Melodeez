package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _uiAction = MutableStateFlow<UiAction>(UiAction.None)
    val uiAction = _uiAction.asStateFlow()

    fun createPlaylist(playlistName: String) {
        viewModelScope.launch {
            val playlistId = playlistUseCases.insertPlaylist(playlistName)
            _uiAction.value = UiAction.NavigateWithPlaylistId(playlistId)
        }
    }

}