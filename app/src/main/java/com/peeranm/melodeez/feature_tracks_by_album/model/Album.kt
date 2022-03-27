package com.peeranm.melodeez.feature_tracks_by_album.model

data class Album(
    var name: String,
    var releaseYear: Int,
    var isAlbumArtAvailable: Boolean,
    var albumArtRef: String = name
)