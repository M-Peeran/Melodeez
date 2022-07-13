package com.peeranm.melodeez.feature_music_playback.data.local.daos

import androidx.room.*
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.model.AlbumWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAlbum(album: Album): Long

    @Delete
    suspend fun deleteAlbum(album: Album): Int

    @Query("delete from albums")
    suspend fun deleteAlbums(): Int

    @Query("select * from albums")
    fun getAlbumsAsFlow(): Flow<List<Album>>

    @Query("select * from albums")
    suspend fun getAlbums(): List<Album>

    @Transaction
    @Query("select * from albums where albumId =:albumId")
    suspend fun getAlbumWithTracks(albumId: Long): AlbumWithTracks?

}