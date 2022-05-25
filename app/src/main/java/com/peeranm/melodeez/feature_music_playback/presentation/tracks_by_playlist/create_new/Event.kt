package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new

sealed class Event {
    class CreatePlaylistWithName(val name: String) : Event()
}