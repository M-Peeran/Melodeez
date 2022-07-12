package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.model.Track

class GetTracksFromStorageUseCase(private val tracksSource: MusicSource) {
    operator fun invoke(): List<Track> {
        return tracksSource.getTracksFromStorage()
    }
}