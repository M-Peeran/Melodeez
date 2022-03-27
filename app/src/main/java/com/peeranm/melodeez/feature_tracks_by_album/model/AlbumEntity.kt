package com.peeranm.melodeez.feature_tracks_by_album.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class AlbumEntity (
    @PrimaryKey(autoGenerate = false)
    var name: String,
    var releaseYear: Int,
    var isAlbumArtAvailable: Boolean,
    var albumArtRef: String = name
)