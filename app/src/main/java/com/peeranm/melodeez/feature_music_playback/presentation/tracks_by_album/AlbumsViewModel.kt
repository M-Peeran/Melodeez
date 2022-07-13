package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases.AlbumUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumUseCases: AlbumUseCases
) : ViewModel() {

    val albums: StateFlow<List<Album>>
    get() = albumUseCases.getAlbumsFromCacheForUi().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun synchronizeAlbums() {
        viewModelScope.launch {
            albumUseCases.synchronizeAlbums()
        }
    }
}