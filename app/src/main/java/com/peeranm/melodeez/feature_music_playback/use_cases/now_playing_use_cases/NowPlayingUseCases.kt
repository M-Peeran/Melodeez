package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

data class NowPlayingUseCases(
    val setTrackRemovedListener: SetTrackRemovedListenerUseCase,
    val trackRemoved: TrackRemovedUseCase
)