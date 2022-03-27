package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepository
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetArtistWithTracksUseCase(private val repository: ArtistRepository) {
    suspend operator fun invoke(artistKey: String): Pair<Artist, List<Track>>
    = withContext(Dispatchers.IO) { repository.getArtistWithTracks(artistKey) }
}