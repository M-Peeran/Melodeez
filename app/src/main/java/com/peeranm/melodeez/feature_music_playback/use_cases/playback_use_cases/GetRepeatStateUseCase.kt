package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

import com.peeranm.melodeez.core.utils.RepeatState
import com.peeranm.melodeez.feature_music_playback.utils.helpers.RepeatStateHelper

class GetRepeatStateUseCase(private val repeatStateHelper: RepeatStateHelper) {
    operator fun invoke(): RepeatState? {
        return repeatStateHelper.getState()
    }
}