package com.peeranm.melodeez.feature_music_playback.utils.helpers

import com.peeranm.melodeez.feature_music_playback.model.Track

interface PlaybackSourceHelper {
    fun getCurrentSourceKind(): String
    fun getCurrentTrackPosition(): Int
    fun getCurrentSourceKey(): String
    fun getCurrentSource(): List<Track>
    fun getCurrentSourceSize(): Int
    fun setSourceKind(sourceKind: String)
    fun setTrackPosition(position: Int)
    fun setSourceKey(sourceKey: String)
    fun setCurrentSource(tracks: List<Track>)
}