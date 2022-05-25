package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

interface ControllerCallbackHelper {
    val state: StateFlow<PlaybackStateCompat?>
    val metadata: StateFlow<MediaMetadataCompat?>
}