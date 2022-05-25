package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.TrackRepository
import com.peeranm.melodeez.feature_music_playback.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertTrackUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(track: Track) = withContext(Dispatchers.IO)  {
        repository.insert(track)
    }
}