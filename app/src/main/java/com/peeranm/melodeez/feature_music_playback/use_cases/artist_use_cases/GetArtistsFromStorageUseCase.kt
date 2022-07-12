package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.model.Artist

class GetArtistsFromStorageUseCase(private val musicSource: MusicSource) {
    operator fun invoke() : List<Artist>
    = musicSource.getArtistsFromStorage()
}