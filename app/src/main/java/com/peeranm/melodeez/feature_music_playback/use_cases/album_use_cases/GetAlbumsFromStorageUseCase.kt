package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import android.content.Context
import android.provider.MediaStore
import com.peeranm.melodeez.core.utils.UNKNOWN_ALBUM
import com.peeranm.melodeez.feature_music_playback.data.device_storage.AlbumsSource
import com.peeranm.melodeez.feature_music_playback.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GetAlbumsFromStorageUseCase(private val context: Context, private val albumsSource: AlbumsSource) {
    operator fun invoke() = albumsSource.getAlbumsFromStorage(context)
}