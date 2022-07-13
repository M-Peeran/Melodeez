package com.peeranm.melodeez.feature_music_playback.data.repository

import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.ArtistWithTracks
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow

interface ArtistRepository {

    suspend fun insertArtist(artist: Artist): Long

    suspend fun deleteArtist(artist: Artist): Int

    suspend fun deleteArtists(): Int

    suspend fun getArtists(): List<Artist>

    fun getArtistsAsFlow(): Flow<List<Artist>>

    suspend fun getArtistWithTracks(artistId: Long): ArtistWithTracks?
}