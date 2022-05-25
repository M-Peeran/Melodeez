package com.peeranm.melodeez.feature_music_playback.presentation.player_ui

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.core.utils.RepeatState
import com.peeranm.melodeez.feature_music_playback.use_cases.media_player_use_cases.MediaPlayerUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases.PlaybackUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.TrackInfoUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val mediaPlayerUseCases: MediaPlayerUseCases,
    private val trackInfoUseCases: TrackInfoUseCases,
    private val playbackUseCases: PlaybackUseCases
) : ViewModel() {

    val state: StateFlow<PlaybackStateCompat?>
    get() = trackInfoUseCases.getPlaybackState()

    val metadata: StateFlow<MediaMetadataCompat?>
    get() = trackInfoUseCases.getMetadata()

    private val _repeatState = MutableStateFlow(playbackUseCases.getRepeatState())
    val repeatState: StateFlow<RepeatState?> = _repeatState

    fun onEvent(event: Event) {
        when (event) {
            is Event.ToggleRepeatState -> _repeatState.value = playbackUseCases.toggleRepeatState()
        }
    }

    fun isPlaying() = mediaPlayerUseCases.isPlaying()

    fun getPlaybackPosition() = mediaPlayerUseCases.getPlaybackPosition()

}