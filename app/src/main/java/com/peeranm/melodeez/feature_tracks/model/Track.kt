package com.peeranm.melodeez.feature_tracks.model

import android.net.Uri

data class Track(
    var uri: Uri,
    var title: String,
    var artist: String,
    var album: String,
    var duration: Long,
    var isAlbumArtAvailable: Boolean,
    var albumArtRef: String = album
)