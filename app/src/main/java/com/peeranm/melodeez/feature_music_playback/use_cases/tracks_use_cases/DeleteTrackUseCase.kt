package com.peeranm.melodeez.feature_music_playback.use_cases.tracks_use_cases

import com.peeranm.melodeez.feature_music_playback.data.repository.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTrackUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(trackId: Long) = withContext(Dispatchers.IO)  {
        repository.deleteTrack(trackId)
    }
}