package com.peeranm.melodeez.feature_tracks.use_cases

import com.peeranm.melodeez.feature_tracks.data.TrackRepository
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class InsertTrackUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(track: Track) = withContext(Dispatchers.IO) {
        repository.insert(track)
    }
}