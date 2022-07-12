package com.peeranm.melodeez.feature_music_playback.utils

import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.widget.Toast
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import com.peeranm.melodeez.R
import com.peeranm.melodeez.core.*
import com.peeranm.melodeez.feature_music_playback.model.Track
import com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases.TrackUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases.AlbumUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases.ArtistUseCases
import com.peeranm.melodeez.feature_music_playback.utils.helpers.*
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaBrowserServiceCompat(),
    AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener,
    NotificationActionListener {

    @Inject lateinit var audioFocusHelper: AudioFocusHelper
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var playbackSourceHelper: PlaybackSourceHelper
    @Inject lateinit var repeatStateHelper: RepeatStateHelper
    @Inject lateinit var trackUseCases: TrackUseCases
    @Inject lateinit var albumUseCases: AlbumUseCases
    @Inject lateinit var artistUseCases: ArtistUseCases
    @Inject lateinit var playlistUseCases: PlaylistUseCases
    @Inject lateinit var playbackHelper: PlaybackHelper
    @Inject lateinit var trackInfo: TrackInfo

    private lateinit var mediaSession: MediaSessionCompat
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val sessionCallback = object: MediaSessionCompat.Callback() {

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)
            when (audioFocusHelper.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    serviceScope.launch {
                        val mediaKey = extras?.getLong(MEDIA_KEY) ?: MEDIA_KEY_NONE
                        val position = extras?.getInt(MEDIA_POSITION) ?: -1
                        val tracks = getTracks(mediaId, mediaKey)
                        val track = tracks[position]
                        val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                        val metadata = getMetadataBuilder(track).build()

                        mediaSession.setMetadata(metadata)
                        trackInfo.updateMetadata(metadata)
                        playbackHelper.playTrack(track.uri.toUri())
                        mediaSession.setPlaybackState(playbackState)
                        trackInfo.updateState(playbackState)

                        notificationHelper.registerReceiver()
                        playbackSourceHelper.setSourceKind(mediaId!!)
                        playbackSourceHelper.setTrackPosition(position)
                        playbackSourceHelper.setCurrentSource(tracks)
                    }.invokeOnCompletion {
                        startService(Intent(baseContext, PlaybackService::class.java))
                        startForeground(
                            NOTIFICATION_ID,
                            notificationHelper.getNotification(
                                mediaSession.controller.metadata,
                                mediaSession.sessionToken,
                                playbackHelper.isPlaying()
                            )
                        )
                    }
                }
            }
        }

        override fun onPlay() {
            playbackHelper.resumePlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
            mediaSession.setPlaybackState(playbackState)
            trackInfo.updateState(playbackState)
            startForeground(
                NOTIFICATION_ID,
                notificationHelper.getNotification(
                    mediaSession.controller.metadata,
                    mediaSession.sessionToken,
                    playbackHelper.isPlaying()
                )
            )
        }

        override fun onPause() {
            playbackHelper.pausePlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PAUSED).build()
            mediaSession.setPlaybackState(playbackState)
            trackInfo.updateState(playbackState)
            startForeground(
                NOTIFICATION_ID,
                notificationHelper.getNotification(
                    mediaSession.controller.metadata,
                    mediaSession.sessionToken,
                    playbackHelper.isPlaying()
                )
            )
        }

        override fun onStop() {
            playbackHelper.stopPlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_STOPPED).build()
            mediaSession.setPlaybackState(playbackState)
            trackInfo.updateState(playbackState)
            notificationHelper.unregisterReceiver()
            mediaSession.isActive = false
            serviceScope.cancel()
            stopSelf()
        }

        override fun onSkipToNext() {
            if(audioFocusHelper.requestAudioFocus() != AudioManager.AUDIOFOCUS_GAIN) return
            serviceScope.launch {
                val tracks = playbackSourceHelper.getCurrentSource()
                val position = playbackSourceHelper.getCurrentTrackPosition()

                if (position+1 < tracks.size) {
                    var playbackState = getStateBuilder(PlaybackStateCompat.STATE_SKIPPING_TO_NEXT).build()
                    mediaSession.setPlaybackState(playbackState)
                    trackInfo.updateState(playbackState)

                    val track = tracks[position+1]
                    val metadata = getMetadataBuilder(track).build()
                    mediaSession.setMetadata(metadata)
                    trackInfo.updateMetadata(metadata)

                    playbackHelper.playTrack(track.uri.toUri())
                    playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                    mediaSession.setPlaybackState(playbackState)
                    trackInfo.updateState(playbackState)

                    startService(Intent(baseContext, PlaybackService::class.java))
                    startForeground(
                        NOTIFICATION_ID,
                        notificationHelper.getNotification(
                            mediaSession.controller.metadata,
                            mediaSession.sessionToken,
                            playbackHelper.isPlaying()
                        )
                    )
                    playbackSourceHelper.setTrackPosition(position+1)
                }
            }
        }

        override fun onSkipToPrevious() {
            if(audioFocusHelper.requestAudioFocus() != AudioManager.AUDIOFOCUS_GAIN) return
            serviceScope.launch {
                val tracks = playbackSourceHelper.getCurrentSource()
                val position = playbackSourceHelper.getCurrentTrackPosition()

                if (position-1 >= 0) {
                    var playbackState = getStateBuilder(PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS).build()
                    mediaSession.setPlaybackState(playbackState)
                    trackInfo.updateState(playbackState)

                    val track = tracks[position-1]
                    val metadata = getMetadataBuilder(track).build()
                    mediaSession.setMetadata(metadata)
                    trackInfo.updateMetadata(metadata)

                    playbackHelper.playTrack(track.uri.toUri())
                    playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                    mediaSession.setPlaybackState(playbackState)
                    trackInfo.updateState(playbackState)

                    startService(Intent(baseContext, PlaybackService::class.java))
                    startForeground(
                        NOTIFICATION_ID,
                        notificationHelper.getNotification(
                            mediaSession.controller.metadata,
                            mediaSession.sessionToken,
                            playbackHelper.isPlaying()
                        )
                    )
                    playbackSourceHelper.setTrackPosition(position-1)
                }
            }
        }

        override fun onSeekTo(pos: Long) {
            playbackHelper.seekToPosition(pos.toInt())
            if (playbackHelper.isPlaying()) {
                val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                mediaSession.setPlaybackState(playbackState)
                trackInfo.updateState(playbackState)
            } else {
                val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PAUSED).build()
                mediaSession.setPlaybackState(playbackState)
                trackInfo.updateState(playbackState)
            }
        }
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> audioFocusHelper.duckRestore()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> mediaSession.controller.transportControls.pause()
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> audioFocusHelper.duckNow()
            AudioManager.AUDIOFOCUS_LOSS -> {
                mediaSession.controller.transportControls.stop()
                audioFocusHelper.abandonAudioFocus()
            }
        }
    }

    override fun onCompletion(mediaPlayer: MediaPlayer?) {
        when (repeatStateHelper.getState()) {
            is RepeatState.RepeatOne -> playbackHelper.resumePlayback() // Replays current track
            is RepeatState.RepeatOff -> playNextTrackOrStopPlayback()
            is RepeatState.RepeatAll -> replayFromStart()
        }
    }

    private fun playNextTrackOrStopPlayback() {
        val tracksSize = playbackSourceHelper.getCurrentSourceSize()
        val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()
        val isLastTrackPlayed = currentTrackPosition + 1 == tracksSize
        if (isLastTrackPlayed) {
            Toast.makeText(baseContext, "Queue ended", Toast.LENGTH_SHORT).show()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_NONE).build()
            mediaSession.setPlaybackState(playbackState)
            trackInfo.updateState(playbackState)
            return
        }
        mediaSession.controller.transportControls.skipToNext()
    }

    private fun replayFromStart() {
        val tracksSize = playbackSourceHelper.getCurrentSourceSize()
        val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()
        val isLastTrackPlayed = currentTrackPosition + 1 == tracksSize
        if (isLastTrackPlayed) {
            val bundle = bundleOf(MEDIA_POSITION to 0)
            mediaSession.controller.transportControls.playFromMediaId(KIND_NOW_PLAYING, bundle)
            return
        }
        mediaSession.controller.transportControls.skipToNext()
    }

    override fun onReceive(action: String?) {
        when (action) {
            ACTION_PLAY -> mediaSession.controller.transportControls.play()
            ACTION_PAUSE -> mediaSession.controller.transportControls.pause()
            ACTION_PLAY_NEXT -> mediaSession.controller.transportControls.skipToNext()
            ACTION_PLAY_PREVIOUS -> mediaSession.controller.transportControls.skipToPrevious()
            ACTION_STOP -> mediaSession.controller.transportControls.stop()
        }
    }

    private fun getMetadataBuilder(track: Track): MediaMetadataCompat.Builder {
        return MediaMetadataCompat.Builder().apply {
            if (track.isAlbumArtAvailable) {
                putString(MediaMetadata.METADATA_KEY_ALBUM_ART_URI, track.albumArtRef)
            }
            putString(MediaMetadata.METADATA_KEY_TITLE, track.title)
            putString(MediaMetadata.METADATA_KEY_ALBUM, track.album)
            putString(MediaMetadata.METADATA_KEY_ARTIST, track.artist)
            putLong(MediaMetadata.METADATA_KEY_DURATION, track.duration)
        }
    }

    private fun getStateBuilder(state: Int): PlaybackStateCompat.Builder {
        return PlaybackStateCompat.Builder().setActions(
            PlaybackStateCompat.ACTION_PLAY_PAUSE or
            PlaybackStateCompat.ACTION_PLAY or
            PlaybackStateCompat.ACTION_PAUSE or
            PlaybackStateCompat.ACTION_SKIP_TO_NEXT or
            PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS or
            PlaybackStateCompat.ACTION_SEEK_TO or
            PlaybackStateCompat.ACTION_STOP
        ).also {
            when (state) {
                PlaybackStateCompat.STATE_SKIPPING_TO_NEXT -> it.setState(state, 0, 1f)
                PlaybackStateCompat.STATE_SKIPPING_TO_PREVIOUS -> it.setState(state, 0, 1f)
                else -> it.setState(state, playbackHelper.getPlaybackPosition().toLong(), 1f)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        initialSetup()
    }

    private fun initialSetup() {
        mediaSession = MediaSessionCompat(baseContext, ACTIVITY_TAG).apply {
            setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
            )
            setPlaybackState(
                PlaybackStateCompat.Builder().setActions(
                    PlaybackStateCompat.ACTION_PLAY_PAUSE or
                            PlaybackStateCompat.ACTION_PLAY
                ).setState(
                    PlaybackStateCompat.STATE_STOPPED,
                    PlaybackStateCompat.PLAYBACK_POSITION_UNKNOWN,
                    1f
                ).build()
            )
            setCallback(sessionCallback)
            setSessionToken(sessionToken)
            isActive = true
        }
        audioFocusHelper.setListener(this)
        notificationHelper.setListener(this)
        playbackHelper.setCompletionListener(this)
    }

    private suspend fun getTracks(sourceKind: String?, mediaId: Long): List<Track> {
        return when(sourceKind) {
            KIND_TRACKS_COLLECTION -> trackUseCases.getTracksFromCache()
            KIND_ALBUM -> getTracksOfAlbum(albumId = mediaId)
            KIND_ARTIST -> getTracksOfArtist(artistId = mediaId)
            KIND_PLAYLIST -> getTracksOfPlaylist(playlistId = mediaId)
            KIND_NOW_PLAYING -> playbackSourceHelper.getCurrentSource()
            else -> emptyList()
        }
    }

    private suspend fun getTracksOfAlbum(albumId: Long): List<Track> {
        val ( _, tracks) = albumUseCases.getAlbumWithTracks(albumId)
        return tracks
    }

    private suspend fun getTracksOfPlaylist(playlistId: Long): List<Track> {
        val ( _, tracks) = playlistUseCases.getPlaylistWithTracks(playlistId)
        return tracks
    }

    private suspend fun getTracksOfArtist(artistId: Long): List<Track> {
        val ( _, tracks) =  artistUseCases.getArtistWithTracks(artistId)
        return tracks
    }

    override fun onDestroy() {
        super.onDestroy()
        cleanup()
    }

    private fun cleanup() {
        audioFocusHelper.abandonAudioFocus()
        notificationHelper.unregisterReceiver()
        playbackHelper.release()
        mediaSession.isActive = false
        mediaSession.release()
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        return BrowserRoot(getString(R.string.app_name), null)
    }
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
    }
}