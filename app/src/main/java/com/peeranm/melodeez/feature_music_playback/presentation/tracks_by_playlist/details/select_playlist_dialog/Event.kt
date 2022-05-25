package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.select_playlist_dialog

import android.net.Uri

sealed class Event {
    object GetPlaylists : Event()
    class InsertTrackToPlaylist(val playlistId: Long, val trackId: Long) : Event()
}