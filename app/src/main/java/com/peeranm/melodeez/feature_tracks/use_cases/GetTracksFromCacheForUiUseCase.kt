package com.peeranm.melodeez.feature_tracks.use_cases

import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetTracksFromCacheForUiUseCase(private val getTracksFromCache: GetTracksFromCacheUseCase) {
    suspend operator fun invoke(): Flow<DataState<List<Track>>> = flow {
        emit(DataState.Loading)
        val tracks = getTracksFromCache()
        if (tracks.isEmpty()) {
            emit(DataState.Failure("No tracks found in the device storage"))
        } else {
            emit(DataState.Success(tracks))
        }
    }
}