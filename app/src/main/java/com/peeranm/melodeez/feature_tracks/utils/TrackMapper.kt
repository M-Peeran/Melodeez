package com.peeranm.melodeez.feature_tracks.utils

import androidx.core.net.toUri
import com.peeranm.melodeez.feature_tracks.model.TrackEntity
import com.peeranm.melodeez.feature_tracks.model.Track
import com.peeranm.melodeez.common.utils.Mapper

class TrackMapper : Mapper<Track, TrackEntity> {
    override fun fromEntity(entity: TrackEntity): Track {
        return Track(
            title = entity.title,
            artist = entity.artist,
            album = entity.album,
            duration = entity.duration,
            uri = entity.uri.toUri(),
            isAlbumArtAvailable = entity.isAlbumArtAvailable
        )
    }

    override fun toEntity(model: Track): TrackEntity {
        return TrackEntity(
            title = model.title,
            artist = model.artist,
            album = model.album,
            duration = model.duration,
            uri = model.uri.toString(),
            isAlbumArtAvailable = model.isAlbumArtAvailable
        )
    }

    fun fromEntities(entities: List<TrackEntity>): List<Track> {
        return entities.map {
            this.fromEntity(it)
        }
    }

    fun toEntities(models: List<Track>): List<TrackEntity> {
        return models.map {
            this.toEntity(it)
        }
    }
}