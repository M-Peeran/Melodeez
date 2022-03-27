package com.peeranm.melodeez.feature_playlist.use_cases

data class PlaylistUseCases(
    val getPlaylists: GetPlaylistsUseCase,
    val getPlaylist: GetPlaylistUseCase,
    val getPlaylistsForUi: GetPlaylistsForUiUseCase,
    val insertPlaylist: InsertPlaylistUseCase,
    val updatePlaylist: UpdatePlaylistUseCase,
    val deletePlaylist: DeletePlaylistUseCase,
    val insertTrackToPlaylist: InsertTrackToPlaylistUseCase,
    val deleteTrackFromPlaylist: DeleteTrackFromPlaylistUseCase,
    val getPlaylistWithTracks: GetPlaylistWithTracksUseCase,
)