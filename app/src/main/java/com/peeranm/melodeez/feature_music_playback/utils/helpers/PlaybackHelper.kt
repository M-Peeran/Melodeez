package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.media.MediaPlayer
import android.net.Uri

interface PlaybackHelper {

    fun isPlaying(): Boolean
    fun isLooping(): Boolean

    fun pausePlayback()
    fun resumePlayback()
    fun playTrack(trackUri: Uri)

    fun getPlaybackPosition(): Int
    fun seekToPosition(position: Int)

    fun stopPlayback()
    fun release()

    fun setCompletionListener(listener: MediaPlayer.OnCompletionListener)
}