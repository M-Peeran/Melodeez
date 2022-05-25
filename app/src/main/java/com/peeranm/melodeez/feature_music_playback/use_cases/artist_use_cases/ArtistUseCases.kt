package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

data class ArtistUseCases(
    val getArtistsFromStorage: GetArtistsFromStorageUseCase,
    val getArtistsFromCache: GetArtistsFromCacheUseCase,
    val getArtistsForUi: GetArtistsForUiUseCase,
    val getArtistWithTracks: GetArtistWithTracksUseCase,
    val insertArtist: InsertArtistUseCase,
    val deleteArtist: DeleteArtistUseCase,
    val deleteArtists: DeleteArtistsUseCase,
    val synchronizeArtists: SynchronizeArtistsUseCase
)