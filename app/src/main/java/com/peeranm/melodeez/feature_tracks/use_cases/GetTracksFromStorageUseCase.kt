package com.peeranm.melodeez.feature_tracks.use_cases

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.peeranm.melodeez.common.utils.UNKNOWN_ALBUM
import com.peeranm.melodeez.common.utils.UNKNOWN_ARTIST
import com.peeranm.melodeez.common.utils.UNKNOWN_TITLE
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class GetTracksFromStorageUseCase(private val context: Context) {

    suspend operator fun invoke(): List<Track> = withContext(Dispatchers.IO) {

        val directory = File(context.filesDir, "albumarts")
        if (!directory.exists()) directory.mkdir()

        val tracks = mutableListOf<Track>()
        var uri: Uri
        var title: String
        var artist: String
        var album: String
        var duration: Long
        var isAlbumArtAvailable: Boolean
        var id: Long

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DISPLAY_NAME,
        )
        val contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val selectionArgs = null
        val sortOrder = MediaStore.Audio.Media.TITLE

        context.contentResolver.query(
            contentUri,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )?.use {

            while (it.moveToNext()) {
                title = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)) ?:
                        it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)) ?:
                        UNKNOWN_TITLE

                artist = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)) ?:
                        UNKNOWN_ARTIST

                album = it.getString(it.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)) ?:
                        UNKNOWN_ALBUM

                duration = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION))

                id = it.getLong(it.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                uri = ContentUris.withAppendedId(contentUri, id)

                if (!File(directory, "$album.png").exists()) {
                    val mmr = MediaMetadataRetriever()
                    var embeddedArt: ByteArray?
                    try {
                        mmr.setDataSource(context, uri)
                        embeddedArt = mmr.embeddedPicture
                    } catch (e: Exception) { continue }
                    isAlbumArtAvailable = if (embeddedArt != null) {
                        val artFile = File(directory, "$album.png")
                        val outStream = FileOutputStream(artFile)
                        outStream.write(embeddedArt)
                        outStream.close()
                        true
                    } else false
                    mmr.release()
                } else isAlbumArtAvailable = true

                tracks.add(Track(uri, title, artist, album, duration, isAlbumArtAvailable))
            }
            it.close()
        }
        tracks
    }
}