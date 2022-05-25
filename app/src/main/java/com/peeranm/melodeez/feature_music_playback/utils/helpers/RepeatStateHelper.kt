package com.peeranm.melodeez.feature_music_playback.utils.helpers

import com.peeranm.melodeez.core.utils.RepeatState

class RepeatStateHelper {

    private var repeatState: RepeatState? = null

    fun getState() = repeatState

    fun setState(state: RepeatState) {
        repeatState = state
    }
}