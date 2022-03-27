package com.peeranm.melodeez.common.utils

interface Mapper<Model, Entity> {
    fun fromEntity(entity: Entity): Model
    fun toEntity(model: Model): Entity
}