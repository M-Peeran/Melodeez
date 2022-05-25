package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_album.details

import androidx.lifecycle.ViewModel
import com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases.AlbumUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AlbumDetailsViewModel @Inject constructor(
    private val albumUseCases: AlbumUseCases
) : ViewModel() {

    suspend fun getAlbumWithTracks(albumId: Long) = albumUseCases.getAlbumWithTracks(albumId)
}