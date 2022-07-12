package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.data.device_storage.MusicSource
import com.peeranm.melodeez.feature_music_playback.data.repository.ArtistRepository
import com.peeranm.melodeez.feature_music_playback.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SynchronizeArtistsUseCase(
    private val musicSource: MusicSource,
    private val artistRepository: ArtistRepository
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {

        val newArtists = musicSource.getArtistsFromStorage()

        if (newArtists.isEmpty()) {
            artistRepository.deleteArtists()
            return@withContext
        }

        val cachedArtists = artistRepository.getArtists()
        if (cachedArtists.isEmpty()) {
            newArtists.forEach { artistRepository.insertArtist(it) }
            return@withContext
        }

        val cachedArtistMap = mutableMapOf<String, Artist>().apply {
            cachedArtists.forEach { artist -> put(artist.name, artist) }
        }

        val newArtistsMap = mutableMapOf<String, Artist>().apply {
            newArtists.forEach { artist -> put(artist.name, artist) }
        }

        for ((artistName, artist) in cachedArtistMap) {
            if (!newArtistsMap.containsKey(artistName)) {
                artistRepository.deleteArtist(artist)
            }
        }

        for ((artistName, artist) in newArtistsMap) {
            if (!cachedArtistMap.containsKey(artistName)) {
                artistRepository.insertArtist(artist)
            }
        }
    }
}