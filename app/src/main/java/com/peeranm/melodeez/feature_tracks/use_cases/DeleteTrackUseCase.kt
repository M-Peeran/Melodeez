package com.peeranm.melodeez.feature_tracks.use_cases

import android.net.Uri
import com.peeranm.melodeez.feature_tracks.data.TrackRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DeleteTrackUseCase(private val repository: TrackRepository) {
    suspend operator fun invoke(trackUri: Uri) = withContext(Dispatchers.IO) {
        repository.deleteTrackByKey(trackUri.toString())
    }
}