package com.peeranm.melodeez.feature_music_playback.data.repository.impl

import com.peeranm.melodeez.feature_music_playback.data.local.daos.AlbumDao
import com.peeranm.melodeez.feature_music_playback.data.repository.AlbumRepository
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.model.AlbumWithTracks
import kotlinx.coroutines.flow.Flow

class AlbumRepositoryImpl(private val albumDao: AlbumDao) : AlbumRepository {

    override suspend fun insertAlbum(album: Album) {
        albumDao.insertAlbum(album)
    }

    override suspend fun deleteAlbum(album: Album) {
        albumDao.deleteAlbum(album)
    }

    override suspend fun deleteAlbums() {
        albumDao.deleteAlbums()
    }

    override fun getAlbumsAsFlow(): Flow<List<Album>> {
        return albumDao.getAlbumsAsFlow()
    }

    override suspend fun getAlbums(): List<Album> {
        return albumDao.getAlbums()
    }

    override suspend fun getAlbumWithTracks(albumId: Long): AlbumWithTracks {
        return albumDao.getAlbumWithTracks(albumId)
    }
}