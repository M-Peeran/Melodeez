package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteArtistsUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        repository.deleteArtists()
    }
}