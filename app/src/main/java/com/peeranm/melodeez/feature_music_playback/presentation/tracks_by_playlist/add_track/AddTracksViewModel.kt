package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.add_track

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases.TrackUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddTracksViewModel @Inject constructor(
    private val tracksUseCases: TrackUseCases,
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    private val selectedTrackIds = mutableListOf<Long>()

    val tracksState: StateFlow<List<Track>>
    get() = tracksUseCases.getTracksFromCacheForUi().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isAnyTrackSelected = MutableStateFlow(false)
    val isAnyTrackSelected: StateFlow<Boolean> = _isAnyTrackSelected

    fun toggleTrackSelection(trackId: Long, isSelected: Boolean) {
        val isExists = selectedTrackIds.contains(trackId)
        val trackShouldBeAdded = !isExists && isSelected
        val trackShouldBeRemoved = isExists && !isSelected
        if (trackShouldBeAdded) {
            selectedTrackIds.add(trackId)
            _isAnyTrackSelected.value = selectedTrackIds.isNotEmpty()
            return
        }
        if (trackShouldBeRemoved) {
            selectedTrackIds.remove(trackId)
            _isAnyTrackSelected.value = selectedTrackIds.isNotEmpty()
        }
    }

    fun addTracksToPlaylist(playlistId: Long) {
        viewModelScope.launch {
            playlistUseCases.insertTracksToPlaylist(selectedTrackIds, playlistId)
        }
    }

    fun clearSelection() {
        selectedTrackIds.removeAll(selectedTrackIds)
    }
}