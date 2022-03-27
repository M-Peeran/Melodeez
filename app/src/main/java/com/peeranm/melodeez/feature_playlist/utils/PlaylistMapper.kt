package com.peeranm.melodeez.feature_playlist.utils

import com.peeranm.melodeez.feature_playlist.model.PlaylistEntity
import com.peeranm.melodeez.common.utils.Mapper
import com.peeranm.melodeez.feature_playlist.model.Playlist


class PlaylistMapper : Mapper<Playlist, PlaylistEntity> {
    override fun fromEntity(entity: PlaylistEntity): Playlist {
        return Playlist(
            name = entity.name,
            noOfTracks = entity.noOfTracks
        )
    }

    override fun toEntity(model: Playlist): PlaylistEntity {
        return PlaylistEntity(
            name = model.name,
            noOfTracks = model.noOfTracks
        )
    }

    fun fromEntities(entities: List<PlaylistEntity>): List<Playlist> {
        return entities.map {
            this.fromEntity(it)
        }
    }

    fun toEntities(models: List<Playlist>): List<PlaylistEntity> {
        return models.map {
            this.toEntity(it)
        }
    }
}