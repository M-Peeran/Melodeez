package com.peeranm.melodeez.feature_tracks_by_artist.model

import androidx.room.Embedded
import androidx.room.Relation
import com.peeranm.melodeez.feature_tracks.model.TrackEntity

data class ArtistWithTracks(
    @Embedded
    var artist: ArtistEntity,
    @Relation(parentColumn = "name", entityColumn = "artist")
    var tracks: List<TrackEntity>
)