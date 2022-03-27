package com.peeranm.melodeez.feature_music_playback.utils

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class ControllerCallbackHelperImpl : MediaControllerCompat.Callback(),
    ControllerCallbackHelper {

    private val _state = MutableLiveData<PlaybackStateCompat>()
    override val state: LiveData<PlaybackStateCompat>
    get() = _state

    private val _metadata = MutableLiveData<MediaMetadataCompat>()
    override val metadata: LiveData<MediaMetadataCompat>
    get() = _metadata

    override fun onPlaybackStateChanged(state: PlaybackStateCompat?) {
        state?.run { _state.value = this }
    }
    override fun onMetadataChanged(metadata: MediaMetadataCompat?) {
        metadata?.run { _metadata.value = this }
    }
}