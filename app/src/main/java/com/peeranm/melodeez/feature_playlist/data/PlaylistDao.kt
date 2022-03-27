package com.peeranm.melodeez.feature_playlist.data

import androidx.lifecycle.LiveData
import androidx.room.*
import com.peeranm.melodeez.feature_playlist.model.PlaylistEntity
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.model.PlaylistWithTracks

@Dao
interface PlaylistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPlaylist(playlistEntity: PlaylistEntity)

    @Update
    suspend fun updatePlaylist(playlistEntity: PlaylistEntity)

    @Query("delete from playlists where name =:key")
    suspend fun deletePlaylistByKey(key: String): Int

    @Query("delete from playlists")
    suspend fun deleteAllPlaylists()

    @Query("select * from playlists where name =:key")
    suspend fun getPlaylist(key: String): PlaylistEntity

    @Query("select * from playlists")
    suspend fun getAllPlaylists(): List<PlaylistEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    @Transaction
    @Delete
    suspend fun deleteTrackFromPlaylist(crossRef: PlaylistTrackCrossRef)

    @Query("delete from playlist_track_junction where name =:playlistKey")
    suspend fun deleteAllTracksFromPlaylist(playlistKey: String)

    @Transaction
    @Query("select * from playlists where name =:key")
    suspend fun getPlaylistWithTracks(key: String): PlaylistWithTracks

    @Query("select * from playlists")
    fun getAllPlaylistsAsLiveData(): LiveData<List<PlaylistEntity>>
}