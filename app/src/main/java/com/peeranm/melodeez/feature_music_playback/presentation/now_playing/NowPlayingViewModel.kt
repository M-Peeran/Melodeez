package com.peeranm.melodeez.feature_music_playback.presentation.now_playing

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.NowPlayingUseCases
import com.peeranm.melodeez.feature_music_playback.data.device_storage.SourceAction
import com.peeranm.melodeez.feature_music_playback.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val nowPlayingUseCases: NowPlayingUseCases
) : ViewModel() {

    private val _sourceAction = MutableStateFlow(SourceAction.Nothing)
    val sourceAction: StateFlow<SourceAction> = _sourceAction

    private val _currentSource = MutableStateFlow<List<Track>>(emptyList())
    val currentSource: StateFlow<List<Track>> = _currentSource

    fun onEvent(event: Event) {
        when (event) {
            is Event.GetCurrentSource -> _currentSource.value = nowPlayingUseCases.getCurrentSource()
            is Event.RemoveTrackFromQueue -> _sourceAction.value = nowPlayingUseCases.removeTrackFromQueue(event.position)
        }
    }

}