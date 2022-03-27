package com.peeranm.melodeez.feature_music_playback.presentation

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.common.utils.RepeatState
import com.peeranm.melodeez.feature_music_playback.use_cases.media_player_state_use_cases.MediaPLayerStateUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.TrackInfoUseCases
import com.peeranm.melodeez.feature_music_playback.utils.CurrentRepeatState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaPlayerStateUseCases: MediaPLayerStateUseCases,
    private val trackInfoUseCases: TrackInfoUseCases,
    private val repeatState: CurrentRepeatState
) : ViewModel() {

    init { Log.i("APP_LOGS", "PLAYER FRAGMENT - VM Created") }

    val state: LiveData<PlaybackStateCompat>
    get() = trackInfoUseCases.getPlaybackState()

    val metadata: LiveData<MediaMetadataCompat>
    get() = trackInfoUseCases.getMetadata()

    fun getRepeatState() = repeatState.getState()

    fun setRepeatState(newState: RepeatState) {
        repeatState.setState(newState)
    }

    fun isPlaying() = mediaPlayerStateUseCases.isPlaying()

    fun getPlaybackPosition() = mediaPlayerStateUseCases.getPlaybackPosition()

    override fun onCleared() {
        super.onCleared()
        Log.i("APP_LOGS", "PLAYER FRAGMENT - VM Destroyed")
    }
}