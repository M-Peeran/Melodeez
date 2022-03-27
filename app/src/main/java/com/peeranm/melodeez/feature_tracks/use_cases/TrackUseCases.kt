package com.peeranm.melodeez.feature_tracks.use_cases

data class TrackUseCases(
    val getTracksFromStorage: GetTracksFromStorageUseCase,
    val getTracksFromCache: GetTracksFromCacheUseCase,
    val getTracksFromCacheForUi: GetTracksFromCacheForUiUseCase,
    val insertTrack: InsertTrackUseCase,
    val deleteTrack: DeleteTrackUseCase,
    val deleteTracks: DeleteTracksUseCase,
    val synchronizeTracks: SynchronizeTracksUseCase
)