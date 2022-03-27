package com.peeranm.melodeez.feature_playlist.data

import androidx.lifecycle.map
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.utils.PlaylistMapper
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.utils.TrackMapper

class PlaylistRepositoryImpl(
    private val playlistDao: PlaylistDao,
    private val playlistMapper: PlaylistMapper,
    private val trackMapper: TrackMapper
) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist) {
        playlistDao.insertPlaylist(playlistMapper.toEntity(playlist))
    }

    override suspend fun updatePlaylist(playlist: Playlist) {
        playlistDao.updatePlaylist(playlistMapper.toEntity(playlist))
    }

    override suspend fun deletePlaylistByKey(playlistKey: String): Int {
        return playlistDao.deletePlaylistByKey(playlistKey)
    }

    override suspend fun deletePlaylists() {
        playlistDao.deleteAllPlaylists()
    }

    override suspend fun getPlaylist(key: String): Playlist {
        return playlistMapper.fromEntity(playlistDao.getPlaylist(key))
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return playlistMapper.fromEntities(playlistDao.getAllPlaylists())
    }

    override suspend fun insertTrackToPlaylist(crossRef: PlaylistTrackCrossRef) {
        playlistDao.insertTrackToPlaylist(crossRef)
    }

    override suspend fun deleteTrackFromPlaylist(crossRef: PlaylistTrackCrossRef) {
        playlistDao.deleteTrackFromPlaylist(crossRef)
    }

    override suspend fun deleteTracksFromPlaylist(playlistKey: String) {
        playlistDao.deleteAllTracksFromPlaylist(playlistKey)
    }

    override suspend fun getPlaylistWithTracks(key: String): Pair<Playlist, List<Track>> {
        val (playlistEntity, trackEntities) = playlistDao.getPlaylistWithTracks(key)
        val playlist = playlistMapper.fromEntity(playlistEntity)
        val tracks = trackMapper.fromEntities(trackEntities)
        return playlist to tracks
    }

    override fun getAllPlaylistsAsLiveData() = playlistDao.getAllPlaylistsAsLiveData().map {
        playlistMapper.fromEntities(it)
    }
}