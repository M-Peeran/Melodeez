package com.peeranm.melodeez.feature_music_playback.utils

import com.peeranm.melodeez.common.utils.RepeatState

class CurrentRepeatState {
    private var repeatState: RepeatState = RepeatState.RepeatOff
    fun setState(state: RepeatState) { repeatState = state }
    fun getState() = repeatState
}