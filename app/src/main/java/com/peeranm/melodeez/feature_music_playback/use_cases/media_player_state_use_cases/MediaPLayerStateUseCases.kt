package com.peeranm.melodeez.feature_music_playback.use_cases.media_player_state_use_cases

data class MediaPLayerStateUseCases(
    val isPlaying: IsPlayingUseCase,
    val isLooping: IsLoopingUseCase,
    val getPlaybackPosition: GetPlaybackPositionUseCase,
    val setCompletionListener: SetCompletionListenerUseCase
)