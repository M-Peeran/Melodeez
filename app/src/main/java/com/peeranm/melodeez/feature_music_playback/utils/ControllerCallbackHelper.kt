package com.peeranm.melodeez.feature_music_playback.utils

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import javax.inject.Inject

interface ControllerCallbackHelper {
    val state: LiveData<PlaybackStateCompat>
    val metadata: LiveData<MediaMetadataCompat>
}