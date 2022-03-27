package com.peeranm.melodeez.feature_music_playback.presentation.now_playing

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackSourceHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val playbackSourceHelper: PlaybackSourceHelper
) : ViewModel() {

    fun getSource() = playbackSourceHelper.getCurrentSource()

    fun removeFromQueueAt(position: Int) {
        // Potential to be a use case
        val currentSource = playbackSourceHelper.getCurrentSource() as MutableList
        currentSource.removeAt(position)
        playbackSourceHelper.setCurrentSource(currentSource)
    }

}