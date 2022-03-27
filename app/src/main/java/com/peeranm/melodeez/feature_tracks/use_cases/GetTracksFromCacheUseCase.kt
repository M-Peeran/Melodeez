package com.peeranm.melodeez.feature_tracks.use_cases

import com.peeranm.melodeez.feature_tracks.data.TrackRepository
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetTracksFromCacheUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(): List<Track> = withContext(Dispatchers.IO) {
        repository.getTracks()
    }
}