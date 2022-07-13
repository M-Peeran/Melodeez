package com.peeranm.melodeez.feature_music_playback.data.repository

import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist): Long

    suspend fun deletePlaylist(playlistId: Long): Int

    suspend fun getPlaylist(playlistId: Long): Playlist?

    suspend fun insertTrackToPlaylist(playlistId: Long, trackId: Long)

    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun deleteTracksFromPlaylist(playlistId: Long)

    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks

    suspend fun getPlaylists(): List<Playlist>

    fun getPlaylistWithTracksAsFlow(playlistId: Long): Flow<PlaylistWithTracks?>

    fun getPlaylistsAsFlow(): Flow<List<Playlist>>

    suspend fun insertTracksToPlaylist(tracksIds: List<Long>, playlistId: Long)
}
