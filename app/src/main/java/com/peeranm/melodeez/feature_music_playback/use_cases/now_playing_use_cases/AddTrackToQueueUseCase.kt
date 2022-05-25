package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackSourceHelper

class AddTrackToQueueUseCase(private val playbackSourceHelper: PlaybackSourceHelper) {
    operator fun invoke(track: Track): Boolean {
        val tracks = playbackSourceHelper.getCurrentSource()
        val isNotEmpty = (tracks.isNotEmpty())
        val trackNotExists = !tracks.contains(track)
        if (isNotEmpty && trackNotExists) {
            (tracks as MutableList).add(track)
            playbackSourceHelper.setCurrentSource(tracks)
        }
        return isNotEmpty
    }
}