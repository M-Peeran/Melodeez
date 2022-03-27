package com.peeranm.melodeez.feature_tracks_by_album.data

import androidx.room.*
import com.peeranm.melodeez.feature_tracks_by_album.model.AlbumEntity
import com.peeranm.melodeez.feature_tracks_by_album.model.AlbumWithTracks

@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAlbum(albumEntity: AlbumEntity)

    @Update
    suspend fun updateAlbum(albumEntity: AlbumEntity)

    @Delete
    suspend fun deleteAlbum(albumEntity: AlbumEntity)

    @Query("delete from albums")
    suspend fun deleteAlbums()

    @Query("select * from albums")
    suspend fun getAlbums(): List<AlbumEntity>

    @Transaction
    @Query("select * from albums where name =:albumKey")
    suspend fun getAlbumWithTracks(albumKey: String): AlbumWithTracks

}