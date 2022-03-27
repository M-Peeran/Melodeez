package com.peeranm.melodeez.feature_tracks.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.common.utils.StateEvent
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.use_cases.TrackUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllTracksViewModel @Inject constructor(
    private val trackUseCases: TrackUseCases
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<List<Track>>>()
    val dataState: LiveData<DataState<List<Track>>> get() = _dataState

    fun setStateEvent(stateEvent: StateEvent) {
        when (stateEvent) {
            StateEvent.Synchronize -> {
                viewModelScope.launch {
                    trackUseCases.synchronizeTracks().onEach { tracksState ->
                        _dataState.value = tracksState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.Get -> {
                viewModelScope.launch {
                    trackUseCases.getTracksFromCacheForUi().onEach { tracksState ->
                        _dataState.value = tracksState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.None -> {}
        }
    }
}