package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "albums")
data class Album(
    @PrimaryKey(autoGenerate = true)
    val albumId: Long = 0L,
    val name: String,
    val releaseYear: Int,
    val isAlbumArtAvailable: Boolean,
    val albumArtRef: String = name
)