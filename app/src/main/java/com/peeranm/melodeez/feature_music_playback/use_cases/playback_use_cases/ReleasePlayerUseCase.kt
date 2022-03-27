package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

import com.peeranm.melodeez.feature_music_playback.utils.PlaybackHelper

class ReleasePlayerUseCase(private val playbackHelper: PlaybackHelper) {
    operator fun invoke() {
        playbackHelper.release()
    }
}