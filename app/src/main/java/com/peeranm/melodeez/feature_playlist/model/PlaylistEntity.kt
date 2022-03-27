package com.peeranm.melodeez.feature_playlist.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "playlists")
data class PlaylistEntity(
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var noOfTracks: Int
)