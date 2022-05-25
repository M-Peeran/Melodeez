package com.peeranm.melodeez.feature_music_playback.data.repository

import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.flow.Flow

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun deletePlaylist(playlistId: Long): Int

    suspend fun getPlaylist(playlistId: Long): Playlist?

    suspend fun insertTrackToPlaylist(playlistId: Long, trackId: Long)

    suspend fun deleteTrackFromPlaylist(playlistId: Long, trackId: Long)

    suspend fun deleteTracksFromPlaylist(playlistId: Long)

    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks

    suspend fun getPlaylists(): List<Playlist>

    fun getPlaylistsAsFlow(): Flow<List<Playlist>>

    suspend fun insertTracksToLastCreatedPlaylist(trackIds: List<Long>)

    suspend fun insertTracksToPlaylist(tracksIds: List<Long>, playlistId: Long)
}
