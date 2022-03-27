package com.peeranm.melodeez.feature_tracks_by_artist.utils

import com.peeranm.melodeez.feature_tracks_by_artist.model.ArtistEntity
import com.peeranm.melodeez.common.utils.Mapper
import com.peeranm.melodeez.feature_tracks_by_artist.model.Artist

class ArtistMapper : Mapper<Artist, ArtistEntity> {
    override fun fromEntity(entity: ArtistEntity): Artist {
        return Artist(name = entity.name)
    }

    override fun toEntity(model: Artist): ArtistEntity {
        return ArtistEntity(name = model.name)
    }

    fun fromEntities(entities: List<ArtistEntity>): List<Artist> {
        return entities.map {
            this.fromEntity(it)
        }
    }

    fun toEntities(models: List<Artist>): List<ArtistEntity> {
        return models.map {
            this.toEntity(it)
        }
    }
}