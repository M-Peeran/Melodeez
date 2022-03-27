package com.peeranm.melodeez.feature_tracks_by_artist.data

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.utils.TrackMapper
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import com.peeranm.melodeez.feature_tracks_by_artist.utils.ArtistMapper

class ArtistRepositoryImpl(
    private val artistDao: ArtistDao,
    private val artistMapper: ArtistMapper,
    private val trackMapper: TrackMapper
) : ArtistRepository {

    override suspend fun insertArtist(artist: Artist) {
        artistDao.insertArtist(artistMapper.toEntity(artist))
    }

    override suspend fun updateArtist(artist: Artist) {
        artistDao.updateArtist(artistMapper.toEntity(artist))
    }

    override suspend fun deleteArtist(artist: Artist) {
        artistDao.deleteArtist(artistMapper.toEntity(artist))
    }

    override suspend fun deleteArtists() {
        artistDao.deleteAllArtists()
    }

    override suspend fun getArtists(): List<Artist> {
        return artistMapper.fromEntities(artistDao.getAllArtists())
    }

    override suspend fun getArtistWithTracks(artistKey: String): Pair<Artist, List<Track>> {
        val (artistEntity, trackEntities) = artistDao.getArtistWithTracks(artistKey)
        val artist = artistMapper.fromEntity(artistEntity)
        val tracks = trackMapper.fromEntities(trackEntities)
        return artist to tracks
    }
}