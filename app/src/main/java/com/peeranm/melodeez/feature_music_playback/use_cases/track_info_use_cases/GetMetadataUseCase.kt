package com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases

import android.support.v4.media.MediaMetadataCompat
import androidx.lifecycle.LiveData
import com.peeranm.melodeez.feature_music_playback.utils.ControllerCallbackHelper

class GetMetadataUseCase(
    private val controllerCallbackHelper: ControllerCallbackHelper
) {
    operator fun invoke(): LiveData<MediaMetadataCompat> {
        return controllerCallbackHelper.metadata
    }
}