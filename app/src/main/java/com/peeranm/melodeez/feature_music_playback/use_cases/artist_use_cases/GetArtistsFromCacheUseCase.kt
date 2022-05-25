package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import com.peeranm.melodeez.feature_music_playback.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetArtistsFromCacheUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(): List<Artist>  = withContext(Dispatchers.IO) {
        repository.getArtists()
    }
}