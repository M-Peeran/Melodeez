package com.peeranm.melodeez.feature_music_playback.use_cases.media_player_state_use_cases

import com.peeranm.melodeez.feature_music_playback.utils.PlaybackHelper

class GetPlaybackPositionUseCase(private val playbackHelper: PlaybackHelper) {
    operator fun invoke(): Int {
        return playbackHelper.getPlaybackPosition()
    }
}