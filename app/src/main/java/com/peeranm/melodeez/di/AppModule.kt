package com.peeranm.melodeez.di

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.data.local.MusicDatabase
import com.peeranm.melodeez.feature_music_playback.data.repository.impl.PlaylistRepositoryImpl
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.*
import com.peeranm.melodeez.feature_music_playback.data.repository.impl.TrackRepositoryImpl
import com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases.*
import com.peeranm.melodeez.feature_music_playback.data.repository.impl.AlbumRepositoryImpl
import com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases.*
import com.peeranm.melodeez.feature_music_playback.data.repository.impl.ArtistRepositoryImpl
import com.peeranm.melodeez.feature_music_playback.utils.helpers.RepeatStateHelper
import com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases.*
import com.peeranm.melodeez.feature_music_playback.utils.helpers.*
import com.peeranm.melodeez.feature_music_playback.utils.helpers.impl.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext context: Context): MusicDatabase {
        return MusicDatabase.getInstance(context)
    }

    @Singleton
    @Provides
    fun providePlaybackHelper(@ApplicationContext context: Context): PlaybackHelper {
        return PlaybackHelperImpl(
            context = context,
            mediaPlayer = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .build()
                )
            }
        )
    }

    @Singleton
    @Provides
    fun providePlaybackNotificationManager(
        @ApplicationContext context: Context
    ): NotificationHelper {
        return NotificationHelperImpl(context)
    }

    @Provides
    @Singleton
    fun provideAudioFocusManager(
        @ApplicationContext context: Context
    ): AudioFocusHelper {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        return AudioFocusHelperImpl(audioManager)
    }

    @Singleton
    @Provides
    fun provideRepeatState(): RepeatStateHelper {
        return RepeatStateHelper()
    }

    @Singleton
    @Provides
    fun providePlaybackSourceManager(): PlaybackSourceHelper {
        return PlaybackSourceHelperImpl()
    }

    @Singleton
    @Provides
    fun provideMusicSource(@ApplicationContext context: Context): MusicSource {
        return MusicSource(context)
    }

    @Singleton
    @Provides
    fun providePlaylistUseCases(database: MusicDatabase): PlaylistUseCases {
        val playlistRepository = PlaylistRepositoryImpl(database)
        return PlaylistUseCases(
            getPlaylists = GetPlaylistsUseCase(playlistRepository),
            getPlaylist = GetPlaylistUseCase(playlistRepository),
            getPlaylistsForUi = GetPlaylistsForUiUseCase(playlistRepository),
            insertPlaylist = InsertPlaylistUseCase(playlistRepository),
            deletePlaylist = DeletePlaylistUseCase(playlistRepository),
            insertTrackToPlaylist = InsertTrackToPlaylistUseCase(playlistRepository),
            insertTracksToPlaylist = InsertTracksToPlaylistUseCase(playlistRepository),
            deleteTrackFromPlaylist = DeleteTrackFromPlaylistUseCase(playlistRepository),
            getPlaylistWithTracks = GetPlaylistWithTracksUseCase(playlistRepository)
        )
    }

    @Singleton
    @Provides
    fun provideTrackUseCases(
        musicSource: MusicSource,
        database: MusicDatabase
    ): TrackUseCases {
        val trackRepository = TrackRepositoryImpl(database.trackDao())
        return TrackUseCases(
            getTracksFromStorage = GetTracksFromStorageUseCase(musicSource),
            getTracksFromCache = GetTracksFromCacheUseCase(trackRepository),
            getTracksFromCacheForUi = GetTracksFromCacheForUiUseCase(trackRepository),
            insertTrack = InsertTrackUseCase(trackRepository),
            deleteTrack = DeleteTrackUseCase(trackRepository),
            deleteTracks = DeleteTracksUseCase(trackRepository),
            synchronizeTracks = SynchronizeTracksUseCase(musicSource, trackRepository)
        )
    }

    @Provides
    @Singleton
    fun provideAlbumUseCases(
        musicSource: MusicSource,
        database: MusicDatabase
    ): AlbumUseCases {
        val albumRepository = AlbumRepositoryImpl(database.albumDao())
        return AlbumUseCases(
            getAlbumsFromStorage = GetAlbumsFromStorageUseCase(musicSource),
            getAlbumsFromCache = GetAlbumsFromCacheUseCase(albumRepository),
            getAlbumsFromCacheForUi = GetAlbumsFromCacheForUiUseCase(albumRepository),
            getAlbumWithTracks = GetAlbumWithTracksUseCase(albumRepository),
            insertAlbum = InsertAlbumUseCase(albumRepository),
            deleteAlbum = DeleteAlbumUseCase(albumRepository),
            deleteAlbums = DeleteAlbumsUseCase(albumRepository),
            synchronizeAlbums = SynchronizeAlbumsUseCase(musicSource, albumRepository)
        )
    }

    @Provides
    @Singleton
    fun provideArtistUseCases(
        musicSource: MusicSource,
        database: MusicDatabase
    ): ArtistUseCases {
        val artistRepository = ArtistRepositoryImpl(database.artistDao())
        return ArtistUseCases(
            getArtistsFromStorage = GetArtistsFromStorageUseCase(musicSource),
            getArtistsFromCache = GetArtistsFromCacheUseCase(artistRepository),
            getArtistsForUi = GetArtistsForUiUseCase(artistRepository),
            getArtistWithTracks = GetArtistWithTracksUseCase(artistRepository),
            insertArtist = InsertArtistUseCase(artistRepository),
            deleteArtist = DeleteArtistUseCase(artistRepository),
            deleteArtists = DeleteArtistsUseCase(artistRepository),
            synchronizeArtists = SynchronizeArtistsUseCase(musicSource, artistRepository)
        )
    }

    @Singleton
    @Provides
    fun provideTrackInfo() = TrackInfo()

}