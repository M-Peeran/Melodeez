package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import android.content.Context
import android.provider.MediaStore
import com.peeranm.melodeez.core.UNKNOWN_ARTIST
import com.peeranm.melodeez.feature_music_playback.model.Artist

class GetArtistsFromStorageUseCase(private val context: Context) {

    operator fun invoke() : List<Artist> {

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
        return artists
    }

}