package com.peeranm.melodeez.feature_music_playback.utils.helpers

import android.media.AudioManager

interface AudioFocusHelper {

    fun requestAudioFocus(): Int

    fun duckNow()

    fun duckRestore()

    fun abandonAudioFocus()

    fun isDucked(): Boolean

    fun setListener(listener: AudioManager.OnAudioFocusChangeListener)
}