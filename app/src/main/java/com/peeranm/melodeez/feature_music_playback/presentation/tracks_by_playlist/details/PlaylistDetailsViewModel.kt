package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.core.ARG_PLAYLIST_ID
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val _playlistWithTracks = MutableStateFlow(getDummyPlaylistWithTracks())
    val playlistWithTracks: StateFlow<PlaylistWithTracks> = _playlistWithTracks

    private val _isDeletionSuccess = MutableStateFlow<Boolean?>(null)
    val isDeletionSuccess: StateFlow<Boolean?> = _isDeletionSuccess

    private val _message = MutableStateFlow("")
    val message = _message.asStateFlow()

    init {
        val playlistId = savedStateHandle.get<Long>(ARG_PLAYLIST_ID)
        if (playlistId != null) {
            playlistUseCases.getPlaylistWithTracksForUi(playlistId).onEach { playlistWithTracks ->
                if (playlistWithTracks != null) {
                    _playlistWithTracks.value = playlistWithTracks
                } else _message.value = "The Playlist you are looking for is not found!"
            }.launchIn(viewModelScope)
        } else _message.value = "PlaylistId is null"
    }

    fun deletePlaylist(playlistId: Long) {
        viewModelScope.launch {
            val deletedPlaylistCount = playlistUseCases.deletePlaylist(playlistId)
            _isDeletionSuccess.value = deletedPlaylistCount != 0
        }
    }

    private fun getDummyPlaylistWithTracks() = PlaylistWithTracks(
        playlist = Playlist(name = ""),
        emptyList()
    )

}