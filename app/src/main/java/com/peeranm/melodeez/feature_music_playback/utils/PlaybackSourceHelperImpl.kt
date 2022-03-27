package com.peeranm.melodeez.feature_music_playback.utils

import com.peeranm.melodeez.feature_tracks.model.Track

class PlaybackSourceHelperImpl : PlaybackSourceHelper {

    private var sourceKind: String = ""
    private var sourceKey: String = ""
    private var trackPosition: Int = -1
    private var trackSource: List<Track> = emptyList()

    override fun getCurrentSourceKind() = sourceKind

    override fun getCurrentSourceKey() = sourceKey

    override fun getCurrentSource() = trackSource

    override fun getCurrentSourceSize() = trackSource.size

    override fun getCurrentTrackPosition() = trackPosition

    override fun setSourceKind(sourceKind: String) {
        this.sourceKind = sourceKind
    }

    override fun setTrackPosition(position: Int) {
        this.trackPosition = position
    }

    override fun setSourceKey(sourceKey: String) {
        this.sourceKey = sourceKey
    }

    override fun setCurrentSource(tracks: List<Track>) {
        this.trackSource = tracks
    }
}