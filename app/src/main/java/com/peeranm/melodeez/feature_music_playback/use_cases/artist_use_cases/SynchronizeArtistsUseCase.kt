package com.peeranm.melodeez.feature_music_playback.use_cases.artist_use_cases

import com.peeranm.melodeez.feature_music_playback.model.Artist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SynchronizeArtistsUseCase(
    private val insertArtist: InsertArtistUseCase,
    private val getArtistsFromCache: GetArtistsFromCacheUseCase,
    private val getArtistsFromStorage: GetArtistsFromStorageUseCase,
    private val deleteArtist: DeleteArtistUseCase,
    private val deleteArtists: DeleteArtistsUseCase
) {
    suspend operator fun invoke() = withContext(Dispatchers.IO) {
        val newArtists = getArtistsFromStorage()

        if (newArtists.isEmpty()) {
            deleteArtists()
            return@withContext
        }

        val cachedArtists = getArtistsFromCache()
        if (cachedArtists.isEmpty()) {
            newArtists.forEach { insertArtist(it) }
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
                deleteArtist(artist)
            }
        }

        for ((artistName, artist) in newArtistsMap) {
            if (!cachedArtistMap.containsKey(artistName)) {
                insertArtist(artist)
            }
        }
    }
}