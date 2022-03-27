package com.peeranm.melodeez.feature_tracks_by_album.model

import androidx.room.Embedded
import androidx.room.Relation
import com.peeranm.melodeez.feature_tracks.model.TrackEntity

data class AlbumWithTracks(
    @Embedded
    var album: AlbumEntity,
    @Relation(parentColumn = "name", entityColumn = "album")
    var tracks: List<TrackEntity>
)