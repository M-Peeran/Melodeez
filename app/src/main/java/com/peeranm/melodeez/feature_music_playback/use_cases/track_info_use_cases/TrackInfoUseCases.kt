package com.peeranm.melodeez.feature_music_playback.use_cases.track_info_use_cases

data class TrackInfoUseCases(
    val getPlaybackState: GetPlaybackStateUseCase,
    val getMetadata: GetMetadataUseCase
)