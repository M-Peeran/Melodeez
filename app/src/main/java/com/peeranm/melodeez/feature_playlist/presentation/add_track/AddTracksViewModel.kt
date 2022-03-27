package com.peeranm.melodeez.feature_playlist.presentation.add_track

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.use_cases.TrackUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTracksViewModel @Inject constructor(
    private val tracksUseCases: TrackUseCases,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val selectedTracks = mutableMapOf<Uri, Boolean>()

    private val _dataState = MutableLiveData<DataState<List<Track>>>()
    val dataState: LiveData<DataState<List<Track>>>
    get() = _dataState

    fun loadTracks() = viewModelScope.launch {
        tracksUseCases.getTracksFromCacheForUi().onEach { tracksState ->
            _dataState.value = tracksState
        }.launchIn(viewModelScope)
    }

    fun isAnyTrackSelected() = selectedTracks.isNotEmpty()

    suspend fun addAll(playlistKey: String) {
        val tracksKeys = selectedTracks.keys
        tracksKeys.forEach { trackUri ->
            val trackRef = PlaylistTrackCrossRef(playlistKey, trackUri.toString())
            playlistUseCases.insertTrackToPlaylist(trackRef)
        }
        // Potential use case
        val playlist = playlistUseCases.getPlaylist(playlistKey)
        playlist.noOfTracks += tracksKeys.size
        playlistUseCases.updatePlaylist(playlist)
    }

    fun addToSelectedTracks(trackUri: Uri, isSelected: Boolean) {
        selectedTracks[trackUri] = isSelected
    }

    fun removeAll() = selectedTracks.clear()

    fun removeFromSelectedTracks(trackUri: Uri) = selectedTracks.remove(trackUri) ?: false

    fun isAlreadyExists(trackUri: Uri) = selectedTracks[trackUri] ?: false
}