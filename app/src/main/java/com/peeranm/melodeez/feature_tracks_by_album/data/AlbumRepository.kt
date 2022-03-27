package com.peeranm.melodeez.feature_tracks_by_album.data

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_album.model.Album

interface AlbumRepository {

    suspend fun insertAlbum(album: Album)

    suspend fun updateAlbum(album: Album)

    suspend fun deleteAlbum(album: Album)

    suspend fun deleteAlbums()

    suspend fun getAlbums(): List<Album>

    suspend fun getAlbumWithTracks(albumKey: String): Pair<Album, List<Track>>
}