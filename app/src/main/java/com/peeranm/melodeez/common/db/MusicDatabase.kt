package com.peeranm.melodeez.common.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.peeranm.melodeez.feature_tracks_by_album.data.AlbumDao
import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistDao
import com.peeranm.melodeez.feature_playlist.data.PlaylistDao
import com.peeranm.melodeez.feature_playlist.model.PlaylistEntity
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_tracks.data.TrackDao
import com.peeranm.melodeez.feature_tracks.model.TrackEntity
import com.peeranm.melodeez.feature_tracks_by_album.model.AlbumEntity
import com.peeranm.melodeez.feature_tracks_by_artist.model.ArtistEntity

@Database(
    entities = [
        TrackEntity::class,
        AlbumEntity::class,
        ArtistEntity::class,
        PlaylistEntity::class,
        PlaylistTrackCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract val trackDao: TrackDao
    abstract val albumDao: AlbumDao
    abstract val artistDao: ArtistDao
    abstract val playlistDao: PlaylistDao
}