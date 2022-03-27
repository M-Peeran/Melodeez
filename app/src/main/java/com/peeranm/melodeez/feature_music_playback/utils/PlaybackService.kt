package com.peeranm.melodeez.feature_music_playback.utils

import android.app.Notification
import android.content.Intent
import android.media.AudioManager
import android.media.MediaMetadata
import android.media.MediaPlayer
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.text.TextUtils
import android.util.Log
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.media.MediaBrowserServiceCompat
import com.peeranm.melodeez.R
import com.peeranm.melodeez.common.utils.*
import com.peeranm.melodeez.feature_music_playback.use_cases.media_player_state_use_cases.MediaPLayerStateUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases.NowPlayingUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases.PlaybackUseCases
import com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases.TrackInfoUseCases
import com.peeranm.melodeez.feature_playlist.use_cases.PlaylistUseCases
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks.use_cases.TrackUseCases
import com.peeranm.melodeez.feature_tracks_by_album.use_cases.AlbumUseCases
import com.peeranm.melodeez.feature_tracks_by_artist.use_cases.ArtistUseCases
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class PlaybackService : MediaBrowserServiceCompat(),
    AudioManager.OnAudioFocusChangeListener,
    MediaPlayer.OnCompletionListener,
    NotificationActionListener,
    TrackRemovedListener {

    @Inject lateinit var audioFocusHelper: AudioFocusHelper
    @Inject lateinit var notificationHelper: NotificationHelper
    @Inject lateinit var playbackSourceHelper: PlaybackSourceHelper
    @Inject lateinit var repeatState: CurrentRepeatState
    @Inject lateinit var trackUseCases: TrackUseCases
    @Inject lateinit var albumUseCases: AlbumUseCases
    @Inject lateinit var artistUseCases: ArtistUseCases
    @Inject lateinit var playlistUseCases: PlaylistUseCases
    @Inject lateinit var mediaPlayerStateUseCases: MediaPLayerStateUseCases
    @Inject lateinit var playbackUseCases: PlaybackUseCases
    @Inject lateinit var nowPlayingUseCases: NowPlayingUseCases

    private lateinit var mediaSession: MediaSessionCompat
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val sessionCallback = object: MediaSessionCompat.Callback() {

        override fun onPlayFromMediaId(mediaId: String?, extras: Bundle?) {
            super.onPlayFromMediaId(mediaId, extras)

            when (audioFocusHelper.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    serviceScope.launch {

                        val mediaKey = extras?.getString(MEDIA_KEY) ?: MEDIA_KEY_NONE
                        val position = extras?.getInt(MEDIA_POSITION) ?: -1
                        val tracks = getSource(mediaId, mediaKey)
                        val track = tracks[position]
                        val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                        val metadata = getMetadataBuilder(track).build()

                        mediaSession.setMetadata(metadata)
                        playbackUseCases.playTrack(track.uri)
                        mediaSession.setPlaybackState(playbackState)

                        startService(Intent(baseContext, PlaybackService::class.java))
                        startForeground(NOTIFICATION_ID, getNotification())

                        notificationHelper.registerReceiver()
                        playbackSourceHelper.setSourceKind(mediaId!!)
                        playbackSourceHelper.setTrackPosition(position)
                        playbackSourceHelper.setCurrentSource(tracks)
                    }
                    if (!mediaSession.isActive) {
                        mediaSession.isActive = true
                    }
                }
            }
        }

        override fun onPlay() {
            playbackUseCases.resumePlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
            mediaSession.setPlaybackState(playbackState)
            startForeground(NOTIFICATION_ID, getNotification())
        }

        override fun onPause() {
            playbackUseCases.pausePlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PAUSED).build()
            mediaSession.setPlaybackState(playbackState)
            startForeground(NOTIFICATION_ID, getNotification())
        }

        override fun onStop() {
            playbackUseCases.stopPlayback()
            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_STOPPED).build()
            mediaSession.setPlaybackState(playbackState)
            notificationHelper.unregisterReceiver()
            mediaSession.isActive = false
            stopSelf()
        }

        override fun onSkipToNext() {

            when (audioFocusHelper.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    serviceScope.launch {

                        val tracks = getCurrentSource()
                        val position = playbackSourceHelper.getCurrentTrackPosition()

                        if (position+1 < tracks.size) {
                            val track = tracks[position+1]
                            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                            val metadata = getMetadataBuilder(track).build()

                            mediaSession.setMetadata(metadata)
                            playbackUseCases.playTrack(track.uri)
                            mediaSession.setPlaybackState(playbackState)

                            startService(Intent(baseContext, PlaybackService::class.java))
                            startForeground(NOTIFICATION_ID, getNotification())

                            playbackSourceHelper.setTrackPosition(position+1)
                        }
                    }
                }
            }
        }

        override fun onSkipToPrevious() {
            when (audioFocusHelper.requestAudioFocus()) {
                AudioManager.AUDIOFOCUS_GAIN -> {
                    serviceScope.launch {

                        val tracks = getCurrentSource()
                        val position = playbackSourceHelper.getCurrentTrackPosition()

                        if (position-1 >= 0) {
                            val track = tracks[position-1]
                            val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                            val metadata = getMetadataBuilder(track).build()

                            mediaSession.setMetadata(metadata)
                            playbackUseCases.playTrack(track.uri)
                            mediaSession.setPlaybackState(playbackState)

                            startService(Intent(baseContext, PlaybackService::class.java))
                            startForeground(NOTIFICATION_ID, getNotification())

                            playbackSourceHelper.setTrackPosition(position-1)
                        }
                    }
                }
            }
        }

        override fun onSeekTo(pos: Long) {

            playbackUseCases.seekToPosition(pos.toInt())

            if (mediaPlayerStateUseCases.isPlaying()) {
                val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PLAYING).build()
                mediaSession.setPlaybackState(playbackState)
            } else {
                val playbackState = getStateBuilder(PlaybackStateCompat.STATE_PAUSED).build()
                mediaSession.setPlaybackState(playbackState)
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
        when (repeatState.getState()) {

            is RepeatState.RepeatOne -> playbackUseCases.resumePlayback()

            is RepeatState.RepeatOff -> {
                val tracksSize = playbackSourceHelper.getCurrentSourceSize()
                val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()

                if (currentTrackPosition+1 == tracksSize) {
                    Toast.makeText(baseContext, "Queue ended", Toast.LENGTH_SHORT).show()
                    val playbackState = getStateBuilder(PlaybackStateCompat.STATE_NONE).build()
                    mediaSession.setPlaybackState(playbackState)
                } else {
                    mediaSession.controller.transportControls.skipToNext()
                }
            }

            is RepeatState.RepeatAll -> {
                val tracksSize = playbackSourceHelper.getCurrentSourceSize()
                val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()

                if (currentTrackPosition+1 == tracksSize) {
                    val bundle = bundleOf(MEDIA_POSITION to 0)
                    mediaSession.controller.transportControls.playFromMediaId(KIND_NOW_PLAYING, bundle)
                } else {
                    mediaSession.controller.transportControls.skipToNext()
                }
            }
        }
    }

    override fun onReceive(action: String?) {
        Log.i("APP_LOGS", "onReceive called")
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
                putString(
                    MediaMetadata.METADATA_KEY_ALBUM_ART_URI,
                    track.albumArtRef
                )
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
        ).setState(state, mediaPlayerStateUseCases.getPlaybackPosition().toLong(), 1f)
    }

    override fun onCreate() {
        super.onCreate()
        mediaSession = MediaSessionCompat(baseContext, "MainActivity").apply {
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
            Log.i("APP_LOGS", "SERVICE CREATED")
        }
        audioFocusHelper.setListener(this)
        notificationHelper.setListener(this)
        nowPlayingUseCases.setTrackRemovedListener(this)
        mediaPlayerStateUseCases.setCompletionListener(this)
    }

    override fun notifyOnRemove(removedPos: Int) {
        when {
            removedPos == -1 -> {}
            removedPos == playbackSourceHelper.getCurrentTrackPosition() -> {
                if (playbackSourceHelper.getCurrentSourceSize() > 0) {
                    val keyBundle = Bundle()
                    keyBundle.putInt(MEDIA_POSITION, 0)
                    mediaSession.controller.transportControls.playFromMediaId(
                        KIND_NOW_PLAYING,
                        keyBundle
                    )
                } else mediaSession.controller.transportControls.stop()
            }
            removedPos < playbackSourceHelper.getCurrentTrackPosition() -> {
                val currentTrackPosition = playbackSourceHelper.getCurrentTrackPosition()
                playbackSourceHelper.setTrackPosition(currentTrackPosition-1)
            }
        }
    }

    private fun getNotification(): Notification {
        return notificationHelper.getNotification(
            metadata = mediaSession.controller.metadata,
            sessionToken = mediaSession.sessionToken
        )
    }

    private suspend fun getSource(sourceKind: String?, mediaKey: String): List<Track> {
        return when(sourceKind) {
            KIND_TRACKS_COLLECTION -> getTracks()
            KIND_ALBUM -> getTracksOfAlbum(albumKey = mediaKey)
            KIND_ARTIST -> getTracksOfArtist(artistKey = mediaKey)
            KIND_PLAYLIST -> getTracksOfPlaylist(playlistKey = mediaKey)
            KIND_NOW_PLAYING -> getCurrentSource()
            else -> emptyList()
        }
    }

    private fun getCurrentSource() = playbackSourceHelper.getCurrentSource()
    
    private suspend fun getTracks() = trackUseCases.getTracksFromCache()

    private suspend fun getTracksOfAlbum(albumKey: String): List<Track> {
        val ( _, tracks) = albumUseCases.getAlbumWithTracks(albumKey)
        return tracks
    }

    private suspend fun getTracksOfPlaylist(playlistKey: String): List<Track> {
        val ( _, tracks) = playlistUseCases.getPlaylistWithTracks(playlistKey)
        return tracks
    }

    private suspend fun getTracksOfArtist(artistKey: String): List<Track> {
        val ( _, tracks) =  artistUseCases.getArtistWithTracks(artistKey)
        return tracks
    }

    override fun onDestroy() {
        super.onDestroy()
        audioFocusHelper.abandonAudioFocus()
        notificationHelper.unregisterReceiver()
        playbackUseCases.releasePlayer()
        mediaSession.isActive = false
        mediaSession.release()
        Log.i("APP_LOGS", "SERVICE DESTROYED")
    }

    override fun onGetRoot(clientPackageName: String, clientUid: Int, rootHints: Bundle?): BrowserRoot {
        // (Optional) Control the level of access for the specified package name.
        // You'll need to write your own logic to do this.
        return if (TextUtils.equals(clientPackageName, packageName)) {
            BrowserRoot(getString(R.string.app_name), null)
        } else BrowserRoot(MY_EMPTY_MEDIA_ROOT_ID, null)
    }
    override fun onLoadChildren(parentId: String, result: Result<MutableList<MediaBrowserCompat.MediaItem>>) {
        result.sendResult(null)
//        //  Browsing not allowed
//        if (MY_EMPTY_MEDIA_ROOT_ID == parentId) {
//            result.sendResult(null)
//            return
//        }
//
//        // Assume for example that the music catalog is already loaded/cached.
//
//        val mediaItems = emptyList<MediaBrowserCompat.MediaItem>() as MutableList
//
//        // Check if this is the root menu:
//        if (MY_MEDIA_ROOT_ID == parentId) {
//            // Build the MediaItem objects for the top level,
//            // and put them in the mediaItems list...
//        } else {
//            // Examine the passed parentMediaId to see which submenu we're at,
//            // and put the children of that menu in the mediaItems list...
//        }
//        result.sendResult(mediaItems)
    }
}