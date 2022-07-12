package com.peeranm.melodeez.feature_music_playback.use_cases.playlist_use_cases

data class PlaylistUseCases(
    val getPlaylists: GetPlaylistsUseCase,
    val getPlaylist: GetPlaylistUseCase,
    val getPlaylistsForUi: GetPlaylistsForUiUseCase,
    val insertPlaylist: InsertPlaylistUseCase,
    val deletePlaylist: DeletePlaylistUseCase,
    val insertTrackToPlaylist: InsertTrackToPlaylistUseCase,
    val insertTracksToPlaylist: InsertTracksToPlaylistUseCase,
    val deleteTrackFromPlaylist: DeleteTrackFromPlaylistUseCase,
    val getPlaylistWithTracks: GetPlaylistWithTracksUseCase,
)