package com.peeranm.melodeez.feature_music_playback.use_cases.album_use_cases

data class AlbumUseCases(
    val getAlbumsFromStorage: GetAlbumsFromStorageUseCase,
    val getAlbumsFromCache: GetAlbumsFromCacheUseCase,
    val getAlbumsFromCacheForUi: GetAlbumsFromCacheForUiUseCase,
    val getAlbumWithTracks: GetAlbumWithTracksUseCase,
    val insertAlbum: InsertAlbumUseCase,
    val deleteAlbum: DeleteAlbumUseCase,
    val deleteAlbums: DeleteAlbumsUseCase,
    val synchronizeAlbums: SynchronizeAlbumsUseCase
)
