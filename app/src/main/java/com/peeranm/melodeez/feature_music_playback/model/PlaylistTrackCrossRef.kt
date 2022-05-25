package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Entity

@Entity(tableName = "playlist_track_junction", primaryKeys = ["playlistId", "trackId"])
data class PlaylistTrackCrossRef(
    val playlistId: Long,
    val trackId: Long
)