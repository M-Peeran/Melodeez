package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import android.content.Context
import com.peeranm.melodeez.feature_music_playback.data.device_storage.AlbumsSource

class GetAlbumsFromStorageUseCase(private val context: Context, private val albumsSource: AlbumsSource) {
    operator fun invoke() = albumsSource.getAlbumsFromStorage(context)
}