package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

import android.net.Uri
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackHelper

class PlayTrackUseCase(private val playbackHelper: PlaybackHelper) {
    operator fun invoke(trackUri: Uri) {
        playbackHelper.playTrack(trackUri)
    }
}