package com.peeranm.melodeez.feature_music_playback.data.repository

import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.model.AlbumWithTracks
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow

interface AlbumRepository {

    suspend fun insertAlbum(album: Album): Long

    suspend fun deleteAlbum(album: Album): Int

    suspend fun deleteAlbums(): Int

    suspend fun getAlbums(): List<Album>

    fun getAlbumsAsFlow(): Flow<List<Album>>

    suspend fun getAlbumWithTracks(albumId: Long): AlbumWithTracks?
}