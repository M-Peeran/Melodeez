package com.peeranm.melodeez.feature_tracks_by_album.presentation.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_tracks_by_album.use_cases.AlbumUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val albumUseCases: AlbumUseCases
) : ViewModel() {

    suspend fun getAlbumWithTracks(key: String) = albumUseCases.getAlbumWithTracks(key)
}