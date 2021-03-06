package com.peeranm.melodeez.feature_music_playback.presentation

import android.content.ComponentName
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaControllerCompat
import com.peeranm.melodeez.R
import com.peeranm.melodeez.feature_music_playback.utils.PlaybackService
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private var mediaBrowser: MediaBrowserCompat? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        connectBrowser()
    }

    private fun connectBrowser() {
        mediaBrowser = MediaBrowserCompat(
            this@MainActivity,
            ComponentName(this@MainActivity, PlaybackService::class.java),
            getConnectionCallback(),
            null
        )
        mediaBrowser?.connect()
    }

    private fun getConnectionCallback(): MediaBrowserCompat.ConnectionCallback {
        return object : MediaBrowserCompat.ConnectionCallback() {
            override fun onConnected() {
                mediaBrowser?.let {
                    MediaControllerCompat.setMediaController(
                        this@MainActivity,
                        MediaControllerCompat(this@MainActivity, it.sessionToken)
                    )
                }
            }
        }
    }

    public override fun onDestroy() {
        super.onDestroy()
        mediaBrowser?.disconnect()
        mediaBrowser = null
    }
}