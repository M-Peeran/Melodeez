package com.peeranm.melodeez.feature_music_playback.use_cases.media_player_use_cases

import android.media.MediaPlayer
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackHelper

class SetCompletionListenerUseCase(private val playbackHelper: PlaybackHelper) {
    operator fun invoke(listener: MediaPlayer.OnCompletionListener) {
        playbackHelper.setCompletionListener(listener)
    }
}