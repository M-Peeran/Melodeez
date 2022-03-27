package com.peeranm.melodeez.feature_playlist.model

import androidx.room.Entity

@Entity(tableName = "playlist_track_junction", primaryKeys = ["name", "uri"])
data class PlaylistTrackCrossRef(
    val name: String,
    val uri: String
)