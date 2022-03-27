package com.peeranm.melodeez.feature_tracks_by_album.utils

import com.peeranm.melodeez.feature_tracks_by_album.model.AlbumEntity
import com.peeranm.melodeez.common.utils.Mapper
import com.peeranm.melodeez.feature_tracks_by_album.model.Album

class AlbumMapper : Mapper<Album, AlbumEntity> {
    override fun fromEntity(entity: AlbumEntity): Album {
        return Album(
            name = entity.name,
            isAlbumArtAvailable = entity.isAlbumArtAvailable,
            releaseYear = entity.releaseYear
        )
    }

    override fun toEntity(model: Album): AlbumEntity {
        return AlbumEntity(
            name = model.name,
            isAlbumArtAvailable = model.isAlbumArtAvailable,
            releaseYear = model.releaseYear
        )
    }

    fun fromEntities(entities: List<AlbumEntity>): List<Album> {
        return entities.map {
            this.fromEntity(it)
        }
    }

    fun toEntities(models: List<Album>): List<AlbumEntity> {
        return models.map {
            this.toEntity(it)
        }
    }
}