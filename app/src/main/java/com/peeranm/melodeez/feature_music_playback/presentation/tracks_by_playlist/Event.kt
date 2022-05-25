package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist

sealed class Event {
    object GetPlaylists : Event()
    class DeletePlaylist(val playlistId: Long) : Event()
}