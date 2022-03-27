package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import android.content.Context
import android.provider.MediaStore
import com.peeranm.melodeez.common.utils.UNKNOWN_ARTIST
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetArtistsFromStorageUseCase(private val context: Context) {

    suspend operator fun invoke() : List<Artist> = withContext(Dispatchers.IO) {

        val projection = arrayOf(MediaStore.Audio.Media.ARTIST)
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = null
        val sortOrder = MediaStore.Audio.Media.ARTIST

        val artists = mutableListOf<Artist>()
        var artist: String

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use {

            while (it.moveToNext()) {
                artist = it.getString(
                    it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                ) ?: UNKNOWN_ARTIST
                artists.add(Artist(name = artist))
            }

            it.close()
        }
        artists
    }

}