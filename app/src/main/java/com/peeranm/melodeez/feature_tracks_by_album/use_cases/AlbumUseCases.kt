package com.peeranm.melodeez.feature_tracks_by_album.use_cases

data class AlbumUseCases(
    val getAlbumsFromStorage: GetAlbumsFromStorageUseCase,
    val getAlbumsFromCache: GetAlbumsFromCacheUseCase,
    val getAlbumsForUi: GetAlbumsForUiUseCase,
    val getAlbumWithTracks: GetAlbumWithTracksUseCase,
    val insertAlbum: InsertAlbumUseCase,
    val deleteAlbum: DeleteAlbumUseCase,
    val deleteAlbums: DeleteAlbumsUseCase,
    val synchronizeAlbums: SynchronizeAlbumsUseCase
)
