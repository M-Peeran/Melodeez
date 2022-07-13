package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import com.peeranm.melodeez.feature_music_playback.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertArtistUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(artist: Artist): Long = withContext(Dispatchers.IO)  {
        repository.insertArtist(artist)
    }
}