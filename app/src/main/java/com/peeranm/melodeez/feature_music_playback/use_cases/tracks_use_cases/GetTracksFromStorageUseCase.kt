package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import android.content.Context
import com.peeranm.melodeez.feature_music_playback.data.device_storage.TracksSource
import com.peeranm.melodeez.feature_music_playback.model.Track

class GetTracksFromStorageUseCase(
    private val context: Context,
    private val tracksSource: TracksSource
) {
    operator fun invoke(): List<Track> {
        return tracksSource.getTracksFromStorage(context)
    }
}