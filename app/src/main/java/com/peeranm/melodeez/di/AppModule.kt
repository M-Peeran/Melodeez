package com.peeranm.melodeez.di

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import com.peeranm.melodeez.feature_music_playback.data.device_storage.AlbumsSource
import com.peeranm.melodeez.feature_music_playback.data.device_storage.TracksSource
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
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.AddTrackToQueueUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.GetCurrentSourceUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.NowPlayingUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.RemoveTrackFromQueueUseCase
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
    fun providePlaylistUseCases(database: MusicDatabase): PlaylistUseCases {
        val playlistRepository = PlaylistRepositoryImpl(database)
        val getPlaylist = GetPlaylistUseCase(playlistRepository)
        val getPlaylists = GetPlaylistsUseCase(playlistRepository)
        val getPlaylistsForUi = GetPlaylistsForUiUseCase(playlistRepository)
        val insertPlaylist = CreatePlaylistUseCase(playlistRepository)
        val deletePlaylist = DeletePlaylistUseCase(playlistRepository)
        val insertTrackToPlaylist = InsertTrackToPlaylistUseCase(playlistRepository)
        val insertTracksToPlaylist = InsertTracksToPlaylistUseCase(playlistRepository)
        val getPlaylistWithTracks = GetPlaylistWithTracksUseCase(playlistRepository)
        val deleteTrackFromPlaylist = DeleteTrackFromPlaylistUseCase(playlistRepository,)
        return PlaylistUseCases(
            getPlaylists = getPlaylists,
            getPlaylist = getPlaylist,
            getPlaylistsForUi = getPlaylistsForUi,
            createPlaylist = insertPlaylist,
            deletePlaylist = deletePlaylist,
            insertTrackToPlaylist = insertTrackToPlaylist,
            insertTracksToPlaylist = insertTracksToPlaylist,
            insertTracksToLastCreatedPlaylist = InsertTracksToLastCreatedPlaylistUseCase(playlistRepository),
            deleteTrackFromPlaylist = deleteTrackFromPlaylist,
            getPlaylistWithTracks = getPlaylistWithTracks
        )
    }

    @Singleton
    @Provides
    fun provideTrackUseCases(
        @ApplicationContext context: Context,
        database: MusicDatabase
    ): TrackUseCases {
        val trackRepository = TrackRepositoryImpl(database.trackDao(),)
        val getTracksFromStorage = GetTracksFromStorageUseCase(context, TracksSource())
        val getTracksFromCache = GetTracksFromCacheUseCase(trackRepository)
        val getTracksFromCacheForUi = GetTracksFromCacheForUiUseCase(trackRepository)
        val insertTrack = InsertTrackUseCase(trackRepository)
        val deleteTrack = DeleteTrackUseCase(trackRepository)
        val deleteTracks = DeleteTracksUseCase(trackRepository)
        val synchronizeTracks = SynchronizeTracksUseCase(
            insertTrack = insertTrack,
            deleteTrack = deleteTrack,
            deleteTracks = deleteTracks,
            getTracksFromCache = getTracksFromCache,
            getTracksFromStorage = getTracksFromStorage
        )
        return TrackUseCases(
            getTracksFromStorage = getTracksFromStorage,
            getTracksFromCache = getTracksFromCache,
            getTracksFromCacheForUi = getTracksFromCacheForUi,
            insertTrack = insertTrack,
            deleteTrack = deleteTrack,
            deleteTracks = deleteTracks,
            synchronizeTracks = synchronizeTracks
        )
    }

    @Provides
    @Singleton
    fun provideAlbumUseCases(
        @ApplicationContext context: Context,
        database: MusicDatabase
    ): AlbumUseCases {
        val albumRepository = AlbumRepositoryImpl(database.albumDao(),)
        val getAlbumsFromStorage = GetAlbumsFromStorageUseCase(context, AlbumsSource())
        val getAlbumsFromCache = GetAlbumsFromCacheUseCase(albumRepository)
        val getAlbumsForUi = GetAlbumsFromCacheForUiUseCase(albumRepository)
        val getAlbumWithTracks = GetAlbumWithTracksUseCase(albumRepository)
        val insertAlbum = InsertAlbumUseCase(albumRepository)
        val deleteAlbum = DeleteAlbumUseCase(albumRepository)
        val deleteAlbums = DeleteAlbumsUseCase(albumRepository)
        val synchronizeAlbums = SynchronizeAlbumsUseCase(
            getAlbumsFromStorage = getAlbumsFromStorage,
            getAlbumsFromCache = getAlbumsFromCache,
            insertAlbum = insertAlbum,
            deleteAlbum = deleteAlbum,
            deleteAlbums = deleteAlbums
        )
        return AlbumUseCases(
            getAlbumsFromStorage = getAlbumsFromStorage,
            getAlbumsFromCache = getAlbumsFromCache,
            getAlbumsFromCacheForUi = getAlbumsForUi,
            getAlbumWithTracks = getAlbumWithTracks,
            insertAlbum = insertAlbum,
            deleteAlbum = deleteAlbum,
            deleteAlbums = deleteAlbums,
            synchronizeAlbums = synchronizeAlbums
        )
    }

    @Provides
    @Singleton
    fun provideArtistUseCases(
        @ApplicationContext context: Context,
        database: MusicDatabase
    ): ArtistUseCases {
        val artistRepository = ArtistRepositoryImpl(database.artistDao())
        val getArtistsFromStorage = GetArtistsFromStorageUseCase(context)
        val getArtistsFromCache = GetArtistsFromCacheUseCase(artistRepository)
        val getArtistsForUi = GetArtistsForUiUseCase(artistRepository)
        val getArtistWithTracks = GetArtistWithTracksUseCase(artistRepository)
        val insertArtist = InsertArtistUseCase(artistRepository)
        val deleteArtist = DeleteArtistUseCase(artistRepository)
        val deleteArtists = DeleteArtistsUseCase(artistRepository)
        val synchronizeArtists = SynchronizeArtistsUseCase(
            insertArtist = insertArtist,
            getArtistsFromCache = getArtistsFromCache,
            getArtistsFromStorage = getArtistsFromStorage,
            deleteArtist = deleteArtist,
            deleteArtists = deleteArtists
        )
        return ArtistUseCases(
            getArtistsFromStorage = getArtistsFromStorage,
            getArtistsFromCache = getArtistsFromCache,
            getArtistsForUi = getArtistsForUi,
            getArtistWithTracks = getArtistWithTracks,
            insertArtist = insertArtist,
            deleteArtist = deleteArtist,
            deleteArtists = deleteArtists,
            synchronizeArtists = synchronizeArtists
        )
    }

    @Singleton
    @Provides
    fun provideNowPlayingUseCases(playbackSourceHelper: PlaybackSourceHelper): NowPlayingUseCases {
        return NowPlayingUseCases(
            removeTrackFromQueue = RemoveTrackFromQueueUseCase(playbackSourceHelper),
            getCurrentSource = GetCurrentSourceUseCase(playbackSourceHelper),
            addTrackToQueue = AddTrackToQueueUseCase(playbackSourceHelper)
        )
    }

}