package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.app.Notification
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.peeranm.melodeez.feature_music_playback.utils.NotificationActionListener

interface NotificationHelper {

    fun getNotification(
        metadata: MediaMetadataCompat?,
        sessionToken: MediaSessionCompat.Token
    ): Notification

    fun registerReceiver()

    fun unregisterReceiver()

    fun setListener(listener: NotificationActionListener)
}