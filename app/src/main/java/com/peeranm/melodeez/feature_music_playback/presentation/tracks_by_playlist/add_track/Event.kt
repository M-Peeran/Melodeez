package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.add_track


sealed class Event {
    object Clear : Event()
    class AddSelectedTracksToPlaylist(val playlistId: Long) : Event()
    class ToggleTrackSelection(val trackId: Long, val isSelected: Boolean) : Event()
}