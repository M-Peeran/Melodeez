package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases.ArtistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistUseCases: ArtistUseCases
) : ViewModel() {

    val artists: StateFlow<List<Artist>>
    get() = artistUseCases.getArtistsForUi().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun setStateEvent(event: Event) {
        when (event) {
            Event.Synchronize -> {
                viewModelScope.launch {
                    artistUseCases.synchronizeArtists()
                }
            }
        }
    }
}