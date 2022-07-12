package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class TrackInfo {

    private val _stateAsFlow = MutableStateFlow<PlaybackStateCompat?>(null)
    val stateAsFlow = _stateAsFlow.asStateFlow()

    private val _metadataAsFlow = MutableStateFlow<MediaMetadataCompat?>(null)
    val metadataAsFlow = _metadataAsFlow.asStateFlow()

    fun updateState(state: PlaybackStateCompat?) {
        _stateAsFlow.value = state
    }

    fun updateMetadata(metadata: MediaMetadataCompat) {
        _metadataAsFlow.value = metadata
    }
}