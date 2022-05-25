package com.peeranm.melodeez.feature_music_playback.presentation.all_tracks.details

import com.peeranm.melodeez.feature_music_playback.model.Track

sealed class Event {
    class AddToQueue(val track: Track) : Event()
}