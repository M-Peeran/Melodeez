package com.peeranm.melodeez.feature_music_playback.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.peeranm.melodeez.core.utils.DATABASE_NAME
import com.peeranm.melodeez.feature_music_playback.data.local.daos.AlbumDao
import com.peeranm.melodeez.feature_music_playback.data.local.daos.ArtistDao
import com.peeranm.melodeez.feature_music_playback.data.local.daos.PlaylistDao
import com.peeranm.melodeez.feature_music_playback.data.local.daos.TrackDao
import com.peeranm.melodeez.feature_music_playback.model.*

@Database(
    entities = [
        Track::class,
        Album::class,
        Artist::class,
        Playlist::class,
        PlaylistTrackCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class MusicDatabase : RoomDatabase() {
    abstract fun trackDao(): TrackDao
    abstract fun albumDao(): AlbumDao
    abstract fun artistDao(): ArtistDao
    abstract fun playlistDao(): PlaylistDao

    companion object {

        @Volatile
        private var INSTANCE: MusicDatabase? = null

        private val lock = Any()

        fun getInstance(context: Context): MusicDatabase {
            var instance = INSTANCE
            if (instance == null) {
                instance = synchronized(lock) {
                    Room.databaseBuilder(context, MusicDatabase::class.java, DATABASE_NAME)
                        .fallbackToDestructiveMigration()
                        .build()
                }
                INSTANCE = instance
            }
            return instance
        }
    }

}