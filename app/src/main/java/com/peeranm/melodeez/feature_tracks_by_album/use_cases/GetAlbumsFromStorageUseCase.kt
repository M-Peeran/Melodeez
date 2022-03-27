package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import android.content.Context
import android.provider.MediaStore
import com.peeranm.melodeez.common.utils.UNKNOWN_ALBUM
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class GetAlbumsFromStorageUseCase(private val context: Context) {

    suspend operator fun invoke(): List<Album> = withContext(Dispatchers.IO) {

        val projection = arrayOf(MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.YEAR)
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = null
        val sortOrder = MediaStore.Audio.Media.ALBUM

        val artDirectory = File(context.filesDir, "albumarts")
        val albums = mutableListOf<Album>()
        var album: String
        var releaseYear: Int
        var isAlbumArtAvailable: Boolean

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use {

            while (it.moveToNext()) {

                album = it.getString(
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                ) ?: UNKNOWN_ALBUM

                releaseYear = it.getInt(it.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR))

                isAlbumArtAvailable = File(artDirectory, "$album.png").exists()

                albums.add(
                    Album(
                        name = album,
                        releaseYear = releaseYear,
                        isAlbumArtAvailable = isAlbumArtAvailable
                    )
                )
            }

            it.close()
        }
        albums
    }
}