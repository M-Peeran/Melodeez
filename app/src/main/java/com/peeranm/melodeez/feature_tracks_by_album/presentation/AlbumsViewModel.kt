package com.peeranm.melodeez.feature_tracks_by_album.presentation

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.common.utils.StateEvent
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import com.peeranm.melodeez.feature_tracks_by_album.use_cases.AlbumUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AlbumsViewModel @Inject constructor(
    private val albumUseCases: AlbumUseCases
) : ViewModel() {

    private val _dataState = MutableLiveData<DataState<List<Album>>>()
    val dataState: LiveData<DataState<List<Album>>> get() = _dataState

    fun setStateEvent(event: StateEvent) {
        when (event) {
            StateEvent.Get -> {
                viewModelScope.launch {
                    albumUseCases.getAlbumsForUi().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.Synchronize -> {
                viewModelScope.launch {
                    albumUseCases.synchronizeAlbums().onEach { dataState ->
                        _dataState.value = dataState
                    }.launchIn(viewModelScope)
                }
            }
            StateEvent.None -> {

            }
        }
    }
}