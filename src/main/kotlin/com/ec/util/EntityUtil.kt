package com.ec.util

import com.ec.model.EntityState
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.bukkit.entity.Entity

object EntityUtil {

    private val mapper = jacksonObjectMapper()

    fun Entity.applyState(state: EntityState) {
        this.scoreboardTags.clear()
        this.scoreboardTags.add(mapper.writeValueAsString(state))
    }

    fun Entity.getState(): EntityState {
        val first = this.scoreboardTags.first()
        return mapper.readValue(first, EntityState::class.java)
    }

}