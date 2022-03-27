package com.peeranm.melodeez.feature_tracks_by_artist.use_cases

import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks_by_artist.data.ArtistRepository
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetArtistsForUiUseCase(private val getArtistsFromCache: GetArtistsFromCacheUseCase) {
    operator fun invoke(): Flow<DataState<List<Artist>>> = flow {
        emit(DataState.Loading)
        val artists = getArtistsFromCache()
        if (artists.isEmpty()) {
            emit(DataState.Failure("No Artists found on the device storage"))
        } else {
            emit(DataState.Success(artists))
        }
    }
}