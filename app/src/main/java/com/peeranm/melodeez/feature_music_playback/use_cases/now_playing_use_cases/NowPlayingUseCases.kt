package com.peeranm.melodeez.feature_music_playback.use_cases.now_playing_use_cases

class NowPlayingUseCases(
    val removeTrackFromQueue: RemoveTrackFromQueueUseCase,
    val getCurrentSource: GetCurrentSourceUseCase,
    val addTrackToQueue: AddTrackToQueueUseCase,
)