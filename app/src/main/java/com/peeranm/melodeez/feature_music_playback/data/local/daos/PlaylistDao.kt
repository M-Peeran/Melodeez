package com.peeranm.melodeez.feature_music_playback.data.local.daos

import androidx.room.*
import com.peeranm.melodeez.feature_music_playback.model.Playlist
import com.peeranm.melodeez.feature_music_playback.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_music_playback.model.PlaylistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: Playlist): Long

    @Query("delete from playlists where playlistId =:playlistId")
    suspend fun deletePlaylist(playlistId: Long): Int

    @Query("delete from playlists")
    suspend fun deleteAllPlaylists()

    @Query("select * from playlists where playlistId =:playlistId")
    suspend fun getPlaylist(playlistId: Long): Playlist?

    @Query("select * from playlists")
    suspend fun getPlaylists(): List<Playlist>

    @Query("select * from playlists")
    fun getPlaylistsAsFlow(): Flow<List<Playlist>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Transaction
    @Delete
    suspend fun deleteTrackFromPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("delete from playlist_track_junction where playlistId =:playlistId")
    suspend fun deleteAllTracksFromPlaylist(playlistId: Long)

    @Transaction
    @Query("select * from playlists where playlistId =:playlistId")
    suspend fun getPlaylistWithTracks(playlistId: Long): PlaylistWithTracks

    @Query("select playlistId from playlists order by playlistId desc limit 1")
    suspend fun getLastCreatedPlaylistId(): Long?
}