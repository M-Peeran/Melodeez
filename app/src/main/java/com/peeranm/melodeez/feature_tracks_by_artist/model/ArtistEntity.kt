package com.peeranm.melodeez.feature_tracks_by_artist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class ArtistEntity(
    @PrimaryKey
    var name: String
)