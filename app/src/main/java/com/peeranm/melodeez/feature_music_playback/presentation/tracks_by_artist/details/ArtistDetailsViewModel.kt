package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_artist.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases.ArtistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ArtistDetailsViewModel @Inject constructor(
    private val artistUseCases: ArtistUseCases
) : ViewModel() {

    suspend fun getArtistWithTracks(artistId: Long) = artistUseCases.getArtistWithTracks(artistId)

}