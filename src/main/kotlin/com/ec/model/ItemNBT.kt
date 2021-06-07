package com.ec.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ItemNBT(

    @JsonProperty("enchantments")
    val enchantments: MutableMap<String, Int> = mutableMapOf()

)