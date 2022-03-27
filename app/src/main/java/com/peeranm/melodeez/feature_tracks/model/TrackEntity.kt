package com.peeranm.melodeez.feature_tracks.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class TrackEntity(
    @PrimaryKey(autoGenerate = false)
    var uri: String,
    var title: String,
    var artist: String,
    var album: String,
    var duration: Long,
    var isAlbumArtAvailable: Boolean,
    var albumArtRef: String = album
)