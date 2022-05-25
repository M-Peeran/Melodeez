package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import com.peeranm.melodeez.feature_music_playback.model.ArtistWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetArtistWithTracksUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(artistId: Long): ArtistWithTracks = withContext(Dispatchers.IO)  {
        repository.getArtistWithTracks(artistId)
    }
}