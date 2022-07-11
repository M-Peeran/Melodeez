package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Embedded
import androidx.room.Relation

data class AlbumWithTracks(
    @Embedded
    val album: Album,
    @Relation(parentColumn = "name", entityColumn = "album")
    val tracks: List<Track>
)