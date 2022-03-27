package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepository
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist

class DeleteArtistUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(artist: Artist) {
        repository.deleteArtist(artist)
    }
}