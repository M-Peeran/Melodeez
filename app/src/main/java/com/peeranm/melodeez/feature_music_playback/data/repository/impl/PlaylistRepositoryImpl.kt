package com.peeranm.melodeez.feature_music_playback.data.repository.impl

import androidx.room.withTransaction
import com.peeranm.melodeez.feature_music_playback.data.local.MusicDatabase
import com.peeranm.melodeez.feature_music_playback.data.local.daos.PlaylistDao
import com.peeranm.melodeez.feature_music_playback.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_music_playback.data.repository.PlaylistRepository
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class PlaylistRepositoryImpl(private val database: MusicDatabase) : PlaylistRepository {

    override suspend fun insertPlaylist(playlist: Playlist): Long {
        return database.playlistDao().insertPlaylist(playlist)
    }

    override suspend fun deletePlaylist(playlistId: Long): Int {
        return withContext(Dispatchers.IO) {
            database.withTransaction {
                // We must delete all tracks that playlist is referencing before deleting the playlist itself.
                database.playlistDao().deleteAllTracksFromPlaylist(playlistId)
                database.playlistDao().deletePlaylist(playlistId)
            }
        }
    }

    override suspend fun getPlaylist(playlistId: Long): Playlist? {
        return database.playlistDao().getPlaylist(playlistId)
    }

    override suspend fun getPlaylists(): List<Playlist> {
        return database.playlistDao().getPlaylists()
    }

    override suspend fun insertTrackToPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            database.withTransaction {
                val playlist = getPlaylist(playlistId)
                if (playlist != null) {
                    val trackRef = PlaylistTrackCrossRef(playlist.playlistId, trackId)
                    database.playlistDao().insertTrackToPlaylist(trackRef)
                    val noOfTracks = playlist.noOfTracks + 1
                    database.playlistDao().insertPlaylist((playlist.copy(noOfTracks = noOfTracks)))
                }
            }
        }
    }

    override suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long) {
        withContext(Dispatchers.IO) {
            database.withTransaction {
                val crossRef = PlaylistTrackCrossRef(playlistId, trackId)
                database.playlistDao().deleteTrackFromPlaylist(crossRef)

                // name is the primary key belonging to the playlist
                val playlist = database.playlistDao().getPlaylist(crossRef.playlistId)

                // update playlist with decreased track count
                playlist?.let {
                    val noOfTracks = database.playlistDao().getPlaylistWithTracks(playlistId).tracks.size
                    database.playlistDao().insertPlaylist(playlist.copy(noOfTracks = noOfTracks))
                }
            }
        }
    }

    override suspend fun deleteTracksFromPlaylist(playlistId: Long) {
        database.playlistDao().deleteAllTracksFromPlaylist(playlistId)
    }

    override suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks {
        return database.playlistDao().getPlaylistWithTracks(playlistId)
    }

    override fun getPlaylistsAsFlow(): Flow<List<Playlist>> {
        return database.playlistDao().getPlaylistsAsFlow()
    }

    override suspend fun insertTracksToPlaylist(tracksIds: List<Long>, playlistId: Long) {
        withContext(Dispatchers.IO) {
            database.withTransaction {
                tracksIds.forEach { trackId ->
                    val trackRef = PlaylistTrackCrossRef(playlistId, trackId)
                    database.playlistDao().insertTrackToPlaylist(trackRef)
                }
                // Update no of tracks after inserting them to playlist
                val playlist = database.playlistDao().getPlaylist(playlistId)
                playlist?.let {
                    val noOfTracks = database.playlistDao().getPlaylistWithTracks(playlistId).tracks.size
                    database.playlistDao().insertPlaylist(
                        playlist.copy(
                            playlistId = playlist.playlistId,
                            name = playlist.name,
                            noOfTracks = noOfTracks
                        )
                    )
                }
            }
        }
    }
}