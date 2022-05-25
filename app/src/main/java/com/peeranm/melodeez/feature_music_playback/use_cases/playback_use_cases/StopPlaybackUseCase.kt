package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackHelper

class StopPlaybackUseCase(private val playbackHelper: PlaybackHelper) {
    operator fun invoke() {
        playbackHelper.stopPlayback()
    }
}