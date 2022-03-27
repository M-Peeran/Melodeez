package com.peeranm.melodeez.feature_music_playback.utils

interface TrackRemovedObserver {
    fun setListener(listener: TrackRemovedListener)
    fun getListener(): TrackRemovedListener?
}