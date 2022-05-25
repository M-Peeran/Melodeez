package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreatePlaylistViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    fun onEvent(event: Event) {
        when (event) {
            is Event.CreatePlaylistWithName -> viewModelScope.launch {
                playlistUseCases.createPlaylist(event.name)
            }
        }
    }


}