package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource

class GetAlbumsFromStorageUseCase(private val musicSource: MusicSource) {
    operator fun invoke() = musicSource.getAlbumsFromStorage()
}