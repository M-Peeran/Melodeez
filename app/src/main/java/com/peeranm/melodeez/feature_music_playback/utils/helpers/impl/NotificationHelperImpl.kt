package com.peeranm.melodeez.feature_music_playback.utils.helpers.impl

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.media.MediaMetadata
import android.os.Build
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.feature_music_playback.presentation.MainActivity
import com.peeranm.melodeez.feature_music_playback.utils.NotificationActionListener
import com.peeranm.melodeez.feature_music_playback.utils.helpers.NotificationHelper
import java.io.File
import java.io.FileNotFoundException


class NotificationHelperImpl(private val context: Context) : NotificationHelper {

    private var listener: NotificationActionListener? = null

    init { createNotificationChannel() }

    override fun setListener(listener: NotificationActionListener) {
        this.listener = listener
    }

    private val buttonReceiver = object: BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent != null) {
                listener?.onReceive(intent.action)
            }
        }
    }

    override fun registerReceiver() {
        val intentFilter = IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
            addAction(ACTION_PLAY_NEXT)
            addAction(ACTION_PLAY_PREVIOUS)
            addAction(ACTION_STOP)
        }
        context.registerReceiver(buttonReceiver, intentFilter)
    }

    override fun unregisterReceiver() {
        context.unregisterReceiver(buttonReceiver)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                "Melodeez Notification",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.description = "Notification from Melodeez"
            NotificationManagerCompat.from(context).createNotificationChannel(notificationChannel)
        }
    }

    override fun getNotification(
        metadata: MediaMetadataCompat?,
        sessionToken: MediaSessionCompat.Token,
        isPlaying: Boolean
    ) = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID).apply {
        setContentTitle(metadata?.getText(MediaMetadata.METADATA_KEY_TITLE))
        setContentText(metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM_ARTIST))
        setSubText(metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM))
        val largeIcon = try {
            context.getBitmap(
                File(context.filesDir, "albumarts"),
                metadata?.getText(MediaMetadata.METADATA_KEY_ALBUM_ART_URI).toString()
            )
        } catch (e: FileNotFoundException) {
            AppCompatResources.getDrawable(
                context,
                R.drawable.ic_music
            )?.toBitmap()!!
        }
        setLargeIcon(largeIcon)
        setContentIntent(getActivityPendingIntent())
        setDeleteIntent(getPendingIntent(ACTION_STOP))
        setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        setSmallIcon(R.drawable.ic_music)
        color = ContextCompat.getColor(context, R.color.design_default_color_primary_dark)
        addAction(
            NotificationCompat.Action.Builder(
                R.drawable.ic_skip_previous,
                "Previous",
                getPendingIntent(ACTION_PLAY_PREVIOUS)
            ).build()
        )
        addAction(
            if (isPlaying) {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_pause_playback,
                    "Pause",
                    getPendingIntent(ACTION_PAUSE)
                ).build()
            } else {
                NotificationCompat.Action.Builder(
                    R.drawable.ic_play_arrow,
                    "Play",
                    getPendingIntent(ACTION_PLAY)
                ).build()
            }
        )
        addAction(
            NotificationCompat.Action.Builder(
                R.drawable.ic_skip_next,
                "Next",
                getPendingIntent(ACTION_PLAY_NEXT)
            ).build()
        )
        setStyle(
            androidx.media.app.NotificationCompat.MediaStyle()
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(0, 1, 2)
                .setShowCancelButton(true)
                .setCancelButtonIntent(getPendingIntent(ACTION_STOP))
        )
    }.build()

    private fun getPendingIntent(action: String) = PendingIntent.getBroadcast(
        context,
        PENDING_INTENT_REQ_CODE,
        Intent(action),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
    )

    private fun getActivityPendingIntent() = PendingIntent.getActivity(
        context,
        PENDING_INTENT_REQ_CODE,
        Intent(context, MainActivity::class.java),
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) PendingIntent.FLAG_IMMUTABLE
        else PendingIntent.FLAG_UPDATE_CURRENT
    )
}