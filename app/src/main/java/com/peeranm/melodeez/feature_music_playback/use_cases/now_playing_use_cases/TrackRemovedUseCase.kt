package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

import com.peeranm.melodeez.feature_music_playback.utils.TrackRemovedListener

class TrackRemovedUseCase(private val listener: TrackRemovedListener?) {
    operator fun invoke(removedPosition: Int) {
        listener?.notifyOnRemove(removedPosition)
    }
}