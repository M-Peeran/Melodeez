package com.peeranm.melodeez.feature_music_playback.model

import androidx.room.Embedded
import androidx.room.Relation

data class ArtistWithTracks(
    @Embedded
    val artist: Artist,
    @Relation(parentColumn = "artistId", entityColumn = "trackId")
    val tracks: List<Track>
)