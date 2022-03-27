package com.peeranm.melodeez.feature_tracks_by_album.data

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.utils.TrackMapper
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import com.peeranm.melodeez.feature_tracks_by_album.utils.AlbumMapper

class AlbumRepositoryImpl(
    private val albumDao: AlbumDao,
    private val albumMapper: AlbumMapper,
    private val trackMapper: TrackMapper
) : AlbumRepository {

    override suspend fun insertAlbum(album: Album) {
        albumDao.insertAlbum(albumMapper.toEntity(album))
    }

    override suspend fun updateAlbum(album: Album) {
        albumDao.updateAlbum(albumMapper.toEntity(album))
    }

    override suspend fun deleteAlbum(album: Album) {
        albumDao.deleteAlbum(albumMapper.toEntity(album))
    }

    override suspend fun deleteAlbums() {
        albumDao.deleteAlbums()
    }

    override suspend fun getAlbums(): List<Album> {
        return albumMapper.fromEntities(albumDao.getAlbums())
    }

    override suspend fun getAlbumWithTracks(albumKey: String): Pair<Album, List<Track>> {
        val (albumEntity, trackEntities) = albumDao.getAlbumWithTracks(albumKey)
        val album = albumMapper.fromEntity(albumEntity)
        val tracks = trackMapper.fromEntities(trackEntities)
        return album to tracks
    }
}