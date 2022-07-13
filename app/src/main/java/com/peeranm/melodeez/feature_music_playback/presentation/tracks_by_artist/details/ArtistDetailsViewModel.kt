package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.core.ARG_ARTIST_ID
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.ArtistWithTracks
import com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases.ArtistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val artistUseCases: ArtistUseCases
) : ViewModel() {

    private val _artistWithTracks = MutableStateFlow(getDummyArtistWithTracks())
    val artistWithTracks = _artistWithTracks.asStateFlow()

    init {
        val artistId = savedStateHandle.get<Long>(ARG_ARTIST_ID)
        if (artistId != null) {
            viewModelScope.launch {
                val artistWithTracks = artistUseCases.getArtistWithTracks(artistId)
                _artistWithTracks.value = artistWithTracks ?: return@launch
            }
        }
    }

    private fun getDummyArtistWithTracks() = ArtistWithTracks(Artist(name = ""), emptyList())

}