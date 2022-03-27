package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import com.peeranm.melodeez.common.utils.DataState
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GetAlbumsForUiUseCase(private val getAlbumsFromCache: GetAlbumsFromCacheUseCase) {
    operator fun invoke(): Flow<DataState<List<Album>>> = flow {
        emit(DataState.Loading)
        val albums = getAlbumsFromCache()
        if (albums.isEmpty()) {
            emit(DataState.Failure("No Albums found on the device storage"))
        } else {
            emit(DataState.Success(albums))
        }
    }
}