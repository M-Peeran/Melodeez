package com.peeranm.melodeez.feature_music_playback.utils.helpers.impl

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.peeranm.melodeez.feature_music_playback.utils.helpers.PlaybackHelper
import java.lang.IllegalStateException

class PlaybackHelperImpl(
    private val context: Context,
    private val mediaPlayer: MediaPlayer
) : PlaybackHelper {

    override fun getPlaybackPosition(): Int {
        return mediaPlayer.currentPosition
    }

    override fun isLooping(): Boolean {
        return mediaPlayer.isLooping
    }

    override fun isPlaying(): Boolean {
        return mediaPlayer.isPlaying
    }

    override fun pausePlayback() {
        mediaPlayer.pause()
    }

    override fun resumePlayback() {
        mediaPlayer.start()
    }

    override fun playTrack(trackUri: Uri) {
        try {
            mediaPlayer.reset()
            mediaPlayer.setDataSource(context, trackUri)
            mediaPlayer.prepare()
            mediaPlayer.start()
        } catch (e: IllegalStateException) { e.printStackTrace() }
    }

    override fun seekToPosition(position: Int) {
        mediaPlayer.seekTo(position)
    }

    override fun stopPlayback() {
        mediaPlayer.stop()
    }

    override fun release() {
        mediaPlayer.release()
    }

    override fun setCompletionListener(listener: MediaPlayer.OnCompletionListener) {
        mediaPlayer.setOnCompletionListener(listener)
    }
}