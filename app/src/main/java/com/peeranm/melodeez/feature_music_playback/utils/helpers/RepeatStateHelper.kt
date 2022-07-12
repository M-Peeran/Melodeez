package com.peeranm.melodeez.feature_music_playback.utils.helpers

import com.peeranm.melodeez.core.RepeatState


class RepeatStateHelper {

    private var repeatState: RepeatState = RepeatState.RepeatOff

    fun getState() = repeatState

    fun toggleState(): RepeatState {
        return when(getState()) {
            is RepeatState.RepeatOff -> {
                repeatState = RepeatState.RepeatAll
                getState()
            }
            is RepeatState.RepeatAll -> {
                repeatState = RepeatState.RepeatOne
                getState()
            }
            is RepeatState.RepeatOne -> {
                repeatState = RepeatState.RepeatOff
                getState()
            }
        }
    }
}