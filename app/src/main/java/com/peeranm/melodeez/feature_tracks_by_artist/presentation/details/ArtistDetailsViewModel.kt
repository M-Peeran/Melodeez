package com.peeranm.melodeez.feature_tracks_by_artist.presentation.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_tracks_by_artist.use_cases.ArtistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val artistUseCases: ArtistUseCases
) : ViewModel() {

    suspend fun getArtistWithTracks(key: String) = artistUseCases.getArtistWithTracks(key)

}