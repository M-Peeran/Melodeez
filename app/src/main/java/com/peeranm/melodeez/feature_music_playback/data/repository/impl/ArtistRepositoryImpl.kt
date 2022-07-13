package com.peeranm.melodeez.feature_music_playback.data.repository.impl

import com.peeranm.melodeez.feature_music_playback.data.local.daos.ArtistDao
import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.ArtistWithTracks
import kotlinx.coroutines.flow.Flow

class ArtistRepositoryImpl(private val artistDao: ArtistDao) : ArtistRepository {

    override suspend fun insertArtist(artist: Artist): Long {
        return artistDao.insertArtist(artist)
    }

    override suspend fun deleteArtist(artist: Artist): Int {
        return artistDao.deleteArtist(artist)
    }

    override suspend fun deleteArtists(): Int {
        return artistDao.deleteAllArtists()
    }

    override suspend fun getArtists(): List<Artist> {
        return artistDao.getArtists()
    }

    override fun getArtistsAsFlow(): Flow<List<Artist>> {
        return artistDao.getArtistsAsFlow()
    }

    override suspend fun getArtistWithTracks(artistId: Long): ArtistWithTracks? {
        return artistDao.getArtistWithTracks(artistId)
    }
}