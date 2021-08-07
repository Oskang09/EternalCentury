package com.ec.model

data class EntityState(
    val mobId: String = "",
    val isMob: Boolean = false,
    val skills: List<EntityStateSkill> = listOf()
)

data class EntityStateSkill(
    val skill: String,
    val level: Int,
)