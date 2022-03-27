package com.peeranm.melodeez.feature_music_playback.utils

import android.app.Notification
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat

interface NotificationHelper {

    fun getNotification(
        metadata: MediaMetadataCompat?,
        sessionToken: MediaSessionCompat.Token
    ): Notification

    fun registerReceiver()

    fun unregisterReceiver()

    fun setListener(listener: NotificationActionListener)
}