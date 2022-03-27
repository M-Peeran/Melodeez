package com.peeranm.melodeez.feature_music_playback.utils


class TrackRemovedObserverImpl : TrackRemovedObserver {

    private var listener: TrackRemovedListener? = null

    override fun setListener(listener: TrackRemovedListener) {
        this.listener = listener
    }

    override fun getListener() = listener
}