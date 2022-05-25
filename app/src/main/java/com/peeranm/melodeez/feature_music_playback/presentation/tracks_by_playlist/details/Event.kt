package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details

sealed class Event {
    class GetPlaylistWithTracks(val playlistId: Long) : Event()
    class DeletePlaylist(val playlistId: Long) : Event()
}