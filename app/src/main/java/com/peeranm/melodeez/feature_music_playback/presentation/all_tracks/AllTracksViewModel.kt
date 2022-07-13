package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases.TrackUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTracksViewModel @Inject constructor(
    private val trackUseCases: TrackUseCases
) : ViewModel() {

    val tracks: StateFlow<List<Track>>
    get() = trackUseCases.getTracksFromCacheForUi().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun synchronizeTracks() {
        viewModelScope.launch {
            trackUseCases.synchronizeTracks()
        }
    }
}