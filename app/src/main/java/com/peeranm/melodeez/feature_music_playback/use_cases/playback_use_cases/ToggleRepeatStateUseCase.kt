package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

import com.peeranm.melodeez.core.utils.RepeatState
import com.peeranm.melodeez.feature_music_playback.utils.helpers.RepeatStateHelper

class ToggleRepeatStateUseCase(
    private val repeatStateHelper: RepeatStateHelper
) {
    operator fun invoke(): RepeatState? {
        return when(repeatStateHelper.getState()) {
            is RepeatState.RepeatOff -> {
                repeatStateHelper.setState(RepeatState.RepeatAll)
                repeatStateHelper.getState()
            }
            is RepeatState.RepeatAll -> {
                repeatStateHelper.setState(RepeatState.RepeatOne)
                repeatStateHelper.getState()
            }
            is RepeatState.RepeatOne -> {
                repeatStateHelper.setState(RepeatState.RepeatOff)
                repeatStateHelper.getState()
            }
            else -> {
                repeatStateHelper.setState(RepeatState.RepeatAll)
                repeatStateHelper.getState()
            }
        }
    }
}