package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository

class GetArtistsForUiUseCase(private val repository: ArtistRepository) {
    operator fun invoke() = repository.getArtistsAsFlow()
}