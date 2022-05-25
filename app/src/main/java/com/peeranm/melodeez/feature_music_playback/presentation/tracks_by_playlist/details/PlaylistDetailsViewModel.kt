package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _playlistWithTracks = MutableStateFlow<PlaylistWithTracks?>(null)
    val playlistWithTracks: StateFlow<PlaylistWithTracks?> = _playlistWithTracks

    private val _isDeletionSuccess = MutableStateFlow<Boolean?>(null)
    val isDeletionSuccess: StateFlow<Boolean?> = _isDeletionSuccess

    fun onEvent(event: Event) {
        when (event) {
            is Event.GetPlaylistWithTracks -> viewModelScope.launch {
                _playlistWithTracks.value = playlistUseCases.getPlaylistWithTracks(event.playlistId)
            }

            is Event.DeletePlaylist -> {
                viewModelScope.launch {
                    val deletedPlaylistCount = playlistUseCases.deletePlaylist(event.playlistId)
                    _isDeletionSuccess.value = deletedPlaylistCount != 0
                }
            }
        }
    }

}