package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.TrackRepository

class GetTracksFromCacheForUiUseCase(private val repository: TrackRepository) {
    operator fun invoke() = repository.getTracksAsFlow()
}