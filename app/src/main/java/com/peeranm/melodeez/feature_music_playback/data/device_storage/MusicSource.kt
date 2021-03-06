package com.peeranm.melodeez.feature_music_playback.data.device_storage

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import com.peeranm.melodeez.core.UNKNOWN_ALBUM
import com.peeranm.melodeez.core.UNKNOWN_ARTIST
import com.peeranm.melodeez.core.UNKNOWN_TITLE
import com.peeranm.melodeez.feature_music_playback.model.Album
import com.peeranm.melodeez.feature_music_playback.model.Artist
import com.peeranm.melodeez.feature_music_playback.model.Track
import java.io.File
import java.io.FileOutputStream
import java.lang.Exception

class MusicSource(private val context: Context) {
    fun getTracksFromStorage(): List<Track> {
        val directory = File(context.filesDir, "albumarts")
        if (!directory.exists()) directory.mkdir()

        val tracks = mutableListOf<Track>()
        var uri: Uri
        var title: String
        var artist: String
        var album: String
        var duration: Long
        var id: Long
        var isAlbumArtAvailable: Boolean
        var embeddedArt: ByteArray?

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
                    try {
                        mmr.setDataSource(context, uri)
                        embeddedArt = mmr.embeddedPicture
                    } catch (e: Exception) { continue }
                    isAlbumArtAvailable = embeddedArt != null
                    if (isAlbumArtAvailable) {
                        val artFile = File(directory, "$album.png")
                        val outStream = FileOutputStream(artFile)
                        outStream.write(embeddedArt)
                        outStream.close()
                    }
                    mmr.release()
                } else isAlbumArtAvailable = true

                tracks.add(
                    Track(
                        uri = uri.toString(),
                        title = title,
                        artist = artist,
                        album = album,
                        duration = duration,
                        isAlbumArtAvailable = isAlbumArtAvailable
                    )
                )
            }
            it.close()
        }
        return tracks
    }

    fun getAlbumsFromStorage(): List<Album> {
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
        return albums
    }

    fun getArtistsFromStorage(): List<Artist> {
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