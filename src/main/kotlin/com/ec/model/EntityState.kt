package com.ec.model

data class EntityState(
    val mobId: String = "",
    val isMob: Boolean = false,
    val skills: List<EntityStateSkill> = listOf()
)

data class EntityStateSkill(
    val skill: String,
    val level: Int,
) {
    override fun equals(other: Any?): Boolean {

        if (other is EntityStateSkill) {
            return other.skill == skill && other.level == level
        }

        if (other is String) {
            return other == "$skill@$level"
        }

        return other.hashCode() == hashCode()
    }
}