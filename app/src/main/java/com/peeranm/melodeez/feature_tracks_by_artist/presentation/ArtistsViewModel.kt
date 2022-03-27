package com.peeranm.melodeez.feature_tracks_by_artist.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.common.utils.StateEvent
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import com.peeranm.melodeez.feature_tracks_by_artist.use_cases.ArtistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ArtistsViewModel @Inject constructor(
    private val artistUseCases: ArtistUseCases
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<List<Artist>>>()
    val dataState: LiveData<DataState<List<Artist>>> get() = _dataState

    fun setStateEvent(event: StateEvent) {
        when (event) {
            StateEvent.Get -> {
                viewModelScope.launch(Dispatchers.IO) {
                    artistUseCases.getArtistsForUi().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.Synchronize -> {
                viewModelScope.launch(Dispatchers.IO) {
                    artistUseCases.synchronizeArtists().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.None -> {}
        }
    }
}