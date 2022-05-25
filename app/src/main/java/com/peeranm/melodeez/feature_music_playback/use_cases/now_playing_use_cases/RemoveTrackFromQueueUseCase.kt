package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.SourceAction
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper

class RemoveTrackFromQueueUseCase(
    private val playbackSourceHelper: PlaybackSourceHelper
) {
    operator fun invoke(position: Int): SourceAction {
        val currentSource = playbackSourceHelper.getCurrentSource() as MutableList
        currentSource.removeAt(position)
        playbackSourceHelper.setCurrentSource(currentSource)
        return when {
            position == playbackSourceHelper.getCurrentTrackPosition() -> {
                if (playbackSourceHelper.getCurrentSourceSize() > 0) {
                    SourceAction.PlayFromBeginning
                } else SourceAction.Stop
            }
            position < playbackSourceHelper.getCurrentTrackPosition() -> {
                val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()
                playbackSourceHelper.setTrackPosition(currentTrackPosition-1)
                SourceAction.Nothing
            }
            else -> SourceAction.Nothing
        }
    }
}