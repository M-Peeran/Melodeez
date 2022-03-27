package com.peeranm.melodeez.common.presentation

import android.content.ComponentName
import android.media.AudioManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.peeranm.melodeez.R
import com.peeranm.melodeez.feature_music_playback.utils.ControllerCallbackHelper
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var controllerCallbackHelper: ControllerCallbackHelper
    private lateinit var mediaBrowser: MediaBrowserCompat

    private val connectionCallback = object : MediaBrowserCompat.ConnectionCallback() {
        override fun onConnected() {
            Log.i("APP_LOGS", "Connected!")
            MediaControllerCompat.setMediaController(
                this@MainActivity,
                MediaControllerCompat(this@MainActivity, mediaBrowser.sessionToken)
            )
            val controller = MediaControllerCompat.getMediaController(this@MainActivity)
            controller.registerCallback(controllerCallbackHelper as MediaControllerCompat.Callback)
        }
        override fun onConnectionSuspended() {
            // The Service has crashed. Disable transport controls until it automatically reconnects
        }
        override fun onConnectionFailed() {
            // The Service has refused our connection
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectBrowser()

        lifecycle.addObserver(
            object : LifecycleEventObserver {
                override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
                    Log.i("APP_LOGS", "MAIN ACTIVITY - ${event.name}")
                }
            }
        )
    }

    private fun connectBrowser() {
        mediaBrowser = MediaBrowserCompat(
            this@MainActivity,
            ComponentName(this@MainActivity, PlaybackService::class.java),
            connectionCallback,
            null
        )
        mediaBrowser.connect()
    }

    override fun onResume() {
        super.onResume()
        volumeControlStream = AudioManager.STREAM_MUSIC
    }

    public override fun onDestroy() {
        super.onDestroy()
        MediaControllerCompat.getMediaController(this@MainActivity)
            .unregisterCallback(controllerCallbackHelper as MediaControllerCompat.Callback)
        mediaBrowser.disconnect()
    }
}