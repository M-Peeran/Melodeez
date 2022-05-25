package com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases

import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import com.peeranm.melodeez.feature_music_playback.utils.helpers.ControllerCallbackHelper
import kotlinx.coroutines.flow.StateFlow

class GetPlaybackStateUseCase(private val controllerCallbackHelper: ControllerCallbackHelper) {
    operator fun invoke(): StateFlow<PlaybackStateCompat?> {
        return controllerCallbackHelper.state
    }
}