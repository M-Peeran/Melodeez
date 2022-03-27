package com.peeranm.melodeez.feature_playlist.data

import androidx.lifecycle.LiveData
import com.peeranm.melodeez.feature_playlist.model.Playlist
import com.peeranm.melodeez.feature_playlist.model.PlaylistTrackCrossRef
import com.peeranm.melodeez.feature_tracks.model.Track

interface PlaylistRepository {

    suspend fun insertPlaylist(playlist: Playlist)

    suspend fun updatePlaylist(playlist: Playlist)

    suspend fun deletePlaylistByKey(playlistKey: String): Int

    suspend fun deletePlaylists()

    suspend fun getPlaylist(key: String): Playlist

    suspend fun getPlaylists(): List<Playlist>

    suspend fun insertTrackToPlaylist(crossRef: PlaylistTrackCrossRef)

    suspend fun deleteTrackFromPlaylist(crossRef: PlaylistTrackCrossRef)

    suspend fun deleteTracksFromPlaylist(playlistKey: String)

    suspend fun getPlaylistWithTracks(key: String): Pair<Playlist, List<Track>>

    fun getAllPlaylistsAsLiveData(): LiveData<List<Playlist>>
}
