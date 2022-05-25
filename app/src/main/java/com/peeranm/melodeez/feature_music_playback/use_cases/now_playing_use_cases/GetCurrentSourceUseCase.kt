package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper

class GetCurrentSourceUseCase(
    private val playbackSourceHelper: PlaybackSourceHelper
) {
    operator fun invoke(): List<Track> {
        return playbackSourceHelper.getCurrentSource()
    }
}