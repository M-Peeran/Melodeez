package com.peeranm.melodeez.feature_playlist.presentation.details

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.common.utils.PlaylistEvents
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_playlist.use_cases.PlaylistUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailsViewModel @Inject constructor(
    private val playlistUseCases: PlaylistUseCases
) : ViewModel() {

    suspend fun getPlaylistWithTracks(key: String)
    = playlistUseCases.getPlaylistWithTracks(key)

    private val _playlistEvent = MutableLiveData<PlaylistEvents>(PlaylistEvents.Done)
    val playlistEvent get() = _playlistEvent

    fun setEvent(event: PlaylistEvents) {
        _playlistEvent.value = event
    }

    suspend fun deleteTrackFromPlaylist(playlistKey: String, trackKey: String) {
        val trackRef = PlaylistTrackCrossRef(playlistKey, trackKey)
        playlistUseCases.deleteTrackFromPlaylist(trackRef)
    }

    suspend fun deletePlaylist(key: String) = playlistUseCases.deletePlaylist(key)

}