package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepository
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertArtistUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(artist: Artist) = withContext(Dispatchers.IO) {
        repository.insertArtist(artist)
    }
}