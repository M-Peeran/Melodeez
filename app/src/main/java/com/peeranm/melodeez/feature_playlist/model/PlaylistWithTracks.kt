package com.peeranm.melodeez.feature_playlist.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.peeranm.melodeez.feature_tracks.model.TrackEntity

data class PlaylistWithTracks(
    @Embedded
    var playlist: PlaylistEntity,
    @Relation(
        parentColumn = "name",
        entityColumn = "uri",
        associateBy = Junction(PlaylistTrackCrossRef::class)
    )
    var tracks: List<TrackEntity>
)