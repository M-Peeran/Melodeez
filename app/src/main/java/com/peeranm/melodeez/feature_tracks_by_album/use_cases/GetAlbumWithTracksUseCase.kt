package com.peeranm.melodeez.feature_tracks_by_album.use_cases

import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.feature_tracks_by_album.data.AlbumRepository
import com.peeranm.melodeez.feature_tracks_by_album.model.Album
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetAlbumWithTracksUseCase(private val repository: AlbumRepository) {
    suspend operator fun invoke(albumKey: String): Pair<Album, List<Track>>
    = withContext(Dispatchers.IO) { repository.getAlbumWithTracks(albumKey) }
}