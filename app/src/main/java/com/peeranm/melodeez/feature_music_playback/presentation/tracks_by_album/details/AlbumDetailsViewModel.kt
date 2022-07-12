package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.core.ARG_ALBUM_ID
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.model.AlbumWithTracks
import com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases.AlbumUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val albumUseCases: AlbumUseCases
) : ViewModel() {

    private val _albumWithTracks = MutableStateFlow(getDummyAlbumWithTracks())
    val albumWithTracks = _albumWithTracks.asStateFlow()

    init {
        viewModelScope.launch {
            val albumId = savedStateHandle.get<Long>(ARG_ALBUM_ID)!!
            _albumWithTracks.value = albumUseCases.getAlbumWithTracks(albumId)
        }
    }

    private fun getDummyAlbumWithTracks() = AlbumWithTracks(
        Album(name = "", isAlbumArtAvailable = false, releaseYear = 0),
        emptyList()
    )
}