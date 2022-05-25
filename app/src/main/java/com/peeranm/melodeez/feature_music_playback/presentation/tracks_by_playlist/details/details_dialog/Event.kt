package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.details.details_dialog

import com.peeranm.melodeez.feature_music_playback.model.Track

sealed class Event {
    class DeleteTrackFromPlaylist(val playlistId: Long, val trackId: Long) : Event()
    class AddToQueue(val track: Track) : Event()
}