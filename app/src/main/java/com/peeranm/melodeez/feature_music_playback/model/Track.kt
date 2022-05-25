package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tracks")
data class Track(
    @PrimaryKey(autoGenerate = true)
    val trackId: Long = 0L,
    val uri: String,
    val title: String,
    val artist: String,
    val album: String,
    val duration: Long,
    val isAlbumArtAvailable: Boolean,
    val albumArtRef: String = album
)