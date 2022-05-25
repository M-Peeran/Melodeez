package com.peeranm.melodeez.feature_music_playback.use_cases.playback_use_cases

data class PlaybackUseCases(
    val playTrack: PlayTrackUseCase,
    val pausePlayback: PausePlaybackUseCase,
    val resumePlayback: ResumePlaybackUseCase,
    val stopPlayback: StopPlaybackUseCase,
    val seekToPosition: SeekToPositionUseCase,
    val releasePlayer: ReleasePlayerUseCase,
    val toggleRepeatState: ToggleRepeatStateUseCase,
    val getRepeatState: GetRepeatStateUseCase
)