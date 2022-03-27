package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

import com.peeranm.melodeez.feature_music_playback.utils.TrackRemovedListener
import com.peeranm.melodeez.feature_music_playback.utils.TrackRemovedObserver

class SetTrackRemovedListenerUseCase(private val observer: TrackRemovedObserver) {
    operator fun invoke(listener: TrackRemovedListener) {
        observer.setListener(listener)
    }
}