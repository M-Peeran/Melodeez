package com.peeranm.melodeez.feature_music_playback.presentation.now_playing

sealed class Event {
    class RemoveTrackFromQueue(val position: Int): Event()
    object GetCurrentSource : Event()
}