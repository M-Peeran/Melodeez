package com.peeranm.melodeez.feature_tracks_by_artist.data

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist

interface ArtistRepository {

    suspend fun insertArtist(artist: Artist)

    suspend fun updateArtist(artist: Artist)

    suspend fun deleteArtist(artist: Artist)

    suspend fun deleteArtists()

    suspend fun getArtists(): List<Artist>

    suspend fun getArtistWithTracks(artistKey: String): Pair<Artist, List<Track>>
}