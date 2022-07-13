package com.peeranm.melodeez.feature_music_playback.data.local.daos

import androidx.room.*
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.ArtistWithTracks
import kotlinx.coroutines.flow.Flow

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtist(artist: Artist): Long

    @Delete
    suspend fun deleteArtist(artist: Artist): Int

    @Query("delete from artists")
    suspend fun deleteAllArtists(): Int

    @Query("select * from artists")
    suspend fun getArtists(): List<Artist>

    @Query("select * from artists")
    fun getArtistsAsFlow(): Flow<List<Artist>>

    @Transaction
    @Query("select * from artists where artistId =:artistId")
    suspend fun getArtistWithTracks(artistId: Long): ArtistWithTracks?

}