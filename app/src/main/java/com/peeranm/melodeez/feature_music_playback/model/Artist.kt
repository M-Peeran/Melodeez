package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "artists")
data class Artist(
    @PrimaryKey(autoGenerate = true)
    val artistId: Long = 0L,
    val name: String
)