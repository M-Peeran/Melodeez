package com.peeranm.melodeez.feature_music_playback.data.repository.impl

import com.peeranm.melodeez.feature_music_playback.data.local.daos.PlaylistDao
import com.peeranm.melodeez.feature_music_playback.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(private val playlistDao: PlaylistDao) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist): Long
    = withContext(Dispatchers.IO) { playlistDao.insertPlaylist(playlist) }

    override suspend fun insertTrackToPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            val playlist = getPlaylist(playlistId)
            if (playlist != null) {
                val trackRef = PlaylistTrackCrossRef(playlist.playlistId, trackId)
                playlistDao.insertTrackToPlaylist(trackRef)
                val noOfTracks = playlist.noOfTracks + 1
                playlistDao.insertPlaylist((playlist.copy(noOfTracks = noOfTracks)))
            }
        }
    }

    override suspend fun insertTracksToPlaylist(tracksIds: List<Long>, playlistId: Long) {
        withContext(Dispatchers.IO) {
            tracksIds.forEach { trackId ->
                val trackRef = PlaylistTrackCrossRef(playlistId, trackId)
                playlistDao.insertTrackToPlaylist(trackRef)
            }
            // Update no of tracks after inserting them to playlist
            val playlist = playlistDao.getPlaylist(playlistId)
            val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlistId)
            if (playlist != null && playlistWithTracks != null) {
                playlistDao.insertPlaylist(playlist.copy(noOfTracks = playlistWithTracks.tracks.size))
            }
        }
    }

    override suspend fun deletePlaylist(playlistId: Long): Int {
        return withContext(Dispatchers.IO) {
            // We must delete all tracks that playlist is referencing before deleting the playlist itself.
            playlistDao.deleteAllTracksFromPlaylist(playlistId)
            playlistDao.deletePlaylist(playlistId)
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            val crossRef = PlaylistTrackCrossRef(playlistId, trackId)
            playlistDao.deleteTrackFromPlaylist(crossRef)
            // update playlist with track count - 1
            val playlist = playlistDao.getPlaylist(playlistId)
            val playlistWithTracks = playlistDao.getPlaylistWithTracks(playlistId)
            if (playlist != null && playlistWithTracks != null) {
                playlistDao.insertPlaylist(playlist.copy(noOfTracks = playlistWithTracks.tracks.size))
            }
        }
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist?
    = withContext(Dispatchers.IO) { playlistDao.getPlaylist(playlistId) }

    override suspend fun getPlaylists(): List<Playlist> {
        return withContext(Dispatchers.IO) { playlistDao.getPlaylists() }
    }

    override suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks?
    = playlistDao.getPlaylistWithTracks(playlistId)

    override fun getPlaylistsAsFlow(): Flow<List<Playlist>>
    = playlistDao.getPlaylistsAsFlow()

    override fun getPlaylistWithTracksAsFlow(playlistId: Long): Flow<PlaylistWithTracks?>
    = playlistDao.getPlaylistWithTracksAsFlow(playlistId)
}