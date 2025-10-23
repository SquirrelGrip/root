package com.github.squirrelgrip

import com.fasterxml.jackson.annotation.JsonProperty

data class Sample(
    @param:JsonProperty("v")
    val v: Int = 0,
    @param:JsonProperty("s")
    val s: String = "A Simple String",
    @param:JsonProperty("m")
    val m: Map<String, String> = mapOf("a" to "AAA"),
    @param:JsonProperty("l")
    val l: List<String> = listOf("1", "AAA")
)
