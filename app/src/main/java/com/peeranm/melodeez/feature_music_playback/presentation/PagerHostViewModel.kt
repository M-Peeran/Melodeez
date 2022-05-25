package com.peeranm.melodeez.feature_music_playback.presentation

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.TrackInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PagerHostViewModel @Inject constructor(
    private val trackInfoUseCases: TrackInfoUseCases
) : ViewModel() {

    val state: StateFlow<PlaybackStateCompat?>
    get() = trackInfoUseCases.getPlaybackState()

    val metadata: StateFlow<MediaMetadataCompat?>
    get() = trackInfoUseCases.getMetadata()
}