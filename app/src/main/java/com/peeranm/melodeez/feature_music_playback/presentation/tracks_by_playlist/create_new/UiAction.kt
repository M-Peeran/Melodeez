package com.peeranm.melodeez.feature_music_playback.presentation.tracks_by_playlist.create_new

sealed class UiAction {
    object None : UiAction()
    class NavigateWithPlaylistId(val playlistId: Long) : UiAction()
}
