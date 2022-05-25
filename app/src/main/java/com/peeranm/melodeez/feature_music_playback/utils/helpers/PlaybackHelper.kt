package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.media.MediaPlayer
import android.net.Uri

interface PlaybackHelper {

    fun getPlaybackPosition(): Int

    fun isLooping(): Boolean

    fun isPlaying(): Boolean

    fun pausePlayback()

    fun resumePlayback()

    fun playTrack(trackUri: Uri)

    fun seekToPosition(position: Int)

    fun stopPlayback()

    fun release()

    fun setCompletionListener(listener: MediaPlayer.OnCompletionListener)
}