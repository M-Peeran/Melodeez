package com.peeranm.melodeez.feature_tracks.presentation.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackSourceHelper
import com.peeranm.melodeez.feature_tracks.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class TrackDetailsViewModel @Inject constructor(
    private val playbackSourceHelper: PlaybackSourceHelper
) : ViewModel() {

    fun addToQueue(track: Track): Boolean {
        // Potential to be a use case
        val tracks = playbackSourceHelper.getCurrentSource() as MutableList
        val isNotEmpty = (tracks.isNotEmpty())
        val trackNotExists = !tracks.contains(track)
        if (isNotEmpty && trackNotExists) {
            tracks.add(track)
            playbackSourceHelper.setCurrentSource(tracks)
        }
        return isNotEmpty
    }
}