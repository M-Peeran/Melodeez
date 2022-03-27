package com.peeranm.melodeez.common.di

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import androidx.room.Room
import com.peeranm.melodeez.common.db.MusicDatabase
import com.peeranm.melodeez.feature_music_playback.use_cases.*
import com.peeranm.melodeez.feature_music_playback.use_cases.media_player_state_use_cases.*
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.NowPlayingUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.SetTrackRemovedListenerUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.TrackRemovedUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases.*
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.GetMetadataUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.GetPlaybackStateUseCase
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.TrackInfoUseCases
import com.peeranm.melodeez.feature_music_playback.utils.*
import com.peeranm.melodeez.feature_playlist.data.PlaylistRepositoryImpl
import com.peeranm.melodeez.feature_playlist.use_cases.*
import com.peeranm.melodeez.feature_playlist.utils.PlaylistMapper
import com.peeranm.melodeez.feature_tracks.data.TrackRepositoryImpl
import com.peeranm.melodeez.feature_tracks.use_cases.*
import com.peeranm.melodeez.feature_tracks.utils.TrackMapper
import com.peeranm.melodeez.feature_tracks_by_album.data.AlbumRepositoryImpl
import com.peeranm.melodeez.feature_tracks_by_album.use_cases.*
import com.peeranm.melodeez.feature_tracks_by_album.utils.AlbumMapper
import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepositoryImpl
import com.peeranm.melodeez.feature_tracks_by_artist.use_cases.*
import com.peeranm.melodeez.feature_tracks_by_artist.utils.ArtistMapper
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
        return Room.databaseBuilder(
            context,
            MusicDatabase::class.java,
            "melodeez_music_database"
        ).fallbackToDestructiveMigration().build()
    }

    @Singleton
    @Provides
    fun providePlaybackManager(@ApplicationContext context: Context): PlaybackHelper {
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
        @ApplicationContext context: Context,
        pLayerStateUseCases: MediaPLayerStateUseCases
    ): NotificationHelper {

        return NotificationHelperImpl(
            context = context,
            isPlaying = pLayerStateUseCases.isPlaying
        )
    }

    @Singleton
    @Provides
    fun provideControllerCallbackLive(): ControllerCallbackHelper {
        return ControllerCallbackHelperImpl()
    }

    @Singleton
    @Provides
    fun provideTrackRemovedObserver(): TrackRemovedObserver {
        return TrackRemovedObserverImpl()
    }

    @Singleton
    @Provides
    fun providePlaybackUseCases(playbackHelper: PlaybackHelper): PlaybackUseCases {
        return PlaybackUseCases(
            playTrack = PlayTrackUseCase(playbackHelper),
            pausePlayback = PausePlaybackUseCase(playbackHelper),
            resumePlayback = ResumePlaybackUseCase(playbackHelper),
            stopPlayback = StopPlaybackUseCase(playbackHelper),
            seekToPosition = SeekToPositionUseCase(playbackHelper),
            releasePlayer = ReleasePlayerUseCase(playbackHelper)
        )
    }

    @Singleton
    @Provides
    fun provideMediaPlayerStateUseCases(playbackHelper: PlaybackHelper): MediaPLayerStateUseCases {
        return MediaPLayerStateUseCases(
            isPlaying = IsPlayingUseCase(playbackHelper),
            isLooping = IsLoopingUseCase(playbackHelper),
            getPlaybackPosition = GetPlaybackPositionUseCase(playbackHelper),
            setCompletionListener = SetCompletionListenerUseCase(playbackHelper)
        )
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
    fun provideRepeatState(): CurrentRepeatState {
        return CurrentRepeatState()
    }

    @Provides
    @Singleton
    fun provideTrackInfoUseCases(
        controllerCallbackHelper: ControllerCallbackHelper
    ): TrackInfoUseCases {
        return TrackInfoUseCases(
            getPlaybackState = GetPlaybackStateUseCase(controllerCallbackHelper),
            getMetadata = GetMetadataUseCase(controllerCallbackHelper)
        )
    }

    @Singleton
    @Provides
    fun provideNowPlayingUseCases(
        observer: TrackRemovedObserver
    ): NowPlayingUseCases {
        return NowPlayingUseCases(
            setTrackRemovedListener = SetTrackRemovedListenerUseCase(observer),
            trackRemoved = TrackRemovedUseCase(observer.getListener())
        )
    }

    @Singleton
    @Provides
    fun providePlaybackSourceManager(): PlaybackSourceHelper {
        return PlaybackSourceHelperImpl()
    }

    @Singleton
    @Provides
    fun providePlaylistUseCases(database: MusicDatabase): PlaylistUseCases {
        val playlistRepository = PlaylistRepositoryImpl(
            playlistDao = database.playlistDao,
            playlistMapper = PlaylistMapper(),
            trackMapper = TrackMapper()
        )
        val getPlaylist = GetPlaylistUseCase(playlistRepository)
        val getPlaylists = GetPlaylistsUseCase(playlistRepository)
        val getPlaylistsForUi = GetPlaylistsForUiUseCase(playlistRepository)
        val insertPlaylist = InsertPlaylistUseCase(playlistRepository)
        val updatePlaylist = UpdatePlaylistUseCase(playlistRepository)
        val deletePlaylist = DeletePlaylistUseCase(playlistRepository)
        val insertTrackToPlaylist = InsertTrackToPlaylistUseCase(playlistRepository)
        val getPlaylistWithTracks = GetPlaylistWithTracksUseCase(playlistRepository)
        val deleteTrackFromPlaylist = DeleteTrackFromPlaylistUseCase(
            repository = playlistRepository,
            getPlaylist = getPlaylist,
            updatePlaylist = updatePlaylist
        )
        return PlaylistUseCases(
            getPlaylists = getPlaylists,
            getPlaylist = getPlaylist,
            getPlaylistsForUi = getPlaylistsForUi,
            insertPlaylist = insertPlaylist,
            updatePlaylist = updatePlaylist,
            deletePlaylist = deletePlaylist,
            insertTrackToPlaylist = insertTrackToPlaylist,
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
        val trackRepository = TrackRepositoryImpl(
            trackDao = database.trackDao,
            trackMapper = TrackMapper()
        )
        val getTracksFromStorage = GetTracksFromStorageUseCase(context)
        val getTracksFromCache = GetTracksFromCacheUseCase(trackRepository)
        val getTracksFromCacheForUi = GetTracksFromCacheForUiUseCase(getTracksFromCache)
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
        val albumRepository = AlbumRepositoryImpl(
            albumDao = database.albumDao,
            albumMapper = AlbumMapper(),
            trackMapper = TrackMapper()
        )
        val getAlbumsFromStorage = GetAlbumsFromStorageUseCase(context)
        val getAlbumsFromCache = GetAlbumsFromCacheUseCase(albumRepository)
        val getAlbumsForUi = GetAlbumsForUiUseCase(getAlbumsFromCache)
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
            getAlbumsForUi = getAlbumsForUi,
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
        val artistRepository = ArtistRepositoryImpl(
            artistDao = database.artistDao,
            artistMapper = ArtistMapper(),
            trackMapper = TrackMapper()
        )
        val getArtistsFromStorage = GetArtistsFromStorageUseCase(context)
        val getArtistsFromCache = GetArtistsFromCacheUseCase(artistRepository)
        val getArtistsForUi = GetArtistsForUiUseCase(getArtistsFromCache)
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


}