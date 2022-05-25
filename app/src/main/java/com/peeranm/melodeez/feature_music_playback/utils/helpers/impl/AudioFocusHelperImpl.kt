package com.peeranm.melodeez.feature_music_playback.utils.helpers.impl

import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.os.Build
import com.peeranm.melodeez.feature_music_playback.utils.helpers.AudioFocusHelper

class AudioFocusHelperImpl(private val audioManager: AudioManager) : AudioFocusHelper {

    private var focusListener: AudioManager.OnAudioFocusChangeListener? = null
    private var focusRequest: AudioFocusRequest? = null
    private var currentVol = -1
    private var isDucked = false

    override fun setListener(listener: AudioManager.OnAudioFocusChangeListener) {
        this.focusListener = listener
    }

    override fun isDucked() = isDucked

    override fun requestAudioFocus(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            focusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN).apply {
                setAudioAttributes(
                    AudioAttributes.Builder().apply {
                        setUsage(AudioAttributes.USAGE_MEDIA)
                        setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    }.build()
                )
                setFocusGain(AudioManager.AUDIOFOCUS_GAIN)
                setAcceptsDelayedFocusGain(true)
                setWillPauseWhenDucked(true)
                focusListener?.run { setOnAudioFocusChangeListener(this) }
            }.build()
            audioManager.requestAudioFocus(focusRequest!!)
        } else {
            audioManager.requestAudioFocus(
                focusListener,
                AudioManager.STREAM_MUSIC,
                AudioManager.AUDIOFOCUS_GAIN
            )
        }
    }

    override fun duckRestore() {
        audioManager.setStreamVolume(
            AudioManager.STREAM_MUSIC,
            currentVol,
            AudioManager.FLAG_PLAY_SOUND
        )
        isDucked = false
    }

    override fun duckNow() {
        currentVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_MUSIC,
            (currentVol/100)*30,
            AudioManager.FLAG_PLAY_SOUND
        )
        isDucked = true
    }

    override fun abandonAudioFocus() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            audioManager.abandonAudioFocusRequest(focusRequest!!)
        } else {
            audioManager.abandonAudioFocus(focusListener)
        }
    }
}