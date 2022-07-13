package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteArtistsUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(): Int = withContext(Dispatchers.IO)  {
        repository.deleteArtists()
    }
}