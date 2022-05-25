package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.NowPlayingUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class TrackDetailsViewModel @Inject constructor(
    private val nowPlayingUseCases: NowPlayingUseCases
) : ViewModel() {

    private val _isSuccessful = MutableStateFlow<Boolean?>(null)
    val isSuccessful: StateFlow<Boolean?> = _isSuccessful

    fun onEvent(event: Event) {
        when (event) {
            is Event.AddToQueue -> {
                val isSuccess = nowPlayingUseCases.addTrackToQueue(event.track)
                _isSuccessful.value = isSuccess
            }
        }
    }
}