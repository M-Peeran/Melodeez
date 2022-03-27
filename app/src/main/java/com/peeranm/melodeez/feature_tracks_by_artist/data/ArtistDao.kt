package com.peeranm.melodeez.feature_tracks_by_artist.data

import androidx.room.*
import com.peeranm.melodeez.feature_tracks_by_artist.model.ArtistEntity
import com.peeranm.melodeez.feature_tracks_by_artist.model.ArtistWithTracks

@Dao
interface ArtistDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertArtist(artistEntity: ArtistEntity)

    @Update
    suspend fun updateArtist(artistEntity: ArtistEntity)

    @Delete
    suspend fun deleteArtist(artistEntity: ArtistEntity)

    @Query("delete from artists")
    suspend fun deleteAllArtists()

    @Query("select * from artists")
    suspend fun getAllArtists(): List<ArtistEntity>

    @Transaction
    @Query("select * from artists where name =:artistKey")
    suspend fun getArtistWithTracks(artistKey: String): ArtistWithTracks

}