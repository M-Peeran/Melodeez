package com.peeranm.melodeez.feature_music_playback.use_cases.media_player_use_cases

data class MediaPlayerUseCases(
    val isPlaying: IsPlayingUseCase,
    val isLooping: IsLoopingUseCase,
    val getPlaybackPosition: GetPlaybackPositionUseCase,
    val setCompletionListener: SetCompletionListenerUseCase
)