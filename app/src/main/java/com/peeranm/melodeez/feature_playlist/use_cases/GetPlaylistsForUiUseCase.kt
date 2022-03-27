package com.peeranm.melodeez.feature_playlist.use_cases

import androidx.lifecycle.LiveData
import com.peeranm.melodeez.feature_playlist.data.PlaylistRepository
import com.peeranm.melodeez.feature_playlist.model.Playlist

class GetPlaylistsForUiUseCase(private val repository: PlaylistRepository) {
    operator fun invoke(): LiveData<List<Playlist>> {
        return repository.getAllPlaylistsAsLiveData()
    }
}