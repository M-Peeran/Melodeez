package com.peeranm.melodeez.feature_music_playback.utils.helpers.impl

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.peeranm.melodeez.feature_music_playback.utils.helpers.ControllerCallbackHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class ControllerCallbackHelperImpl : MediaControllerCompat.Callback(),
    ControllerCallbackHelper {

    private val _state = MutableStateFlow<PlaybackStateCompat?>(null)
    override val state: StateFlow<PlaybackStateCompat?> = _state

    private val _metadata = MutableStateFlow<MediaMetadataCompat?>(null)
    override val metadata: StateFlow<MediaMetadataCompat?> = _metadata

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.run { _state.value = this }
    }
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.run { _metadata.value = this }
    }
}