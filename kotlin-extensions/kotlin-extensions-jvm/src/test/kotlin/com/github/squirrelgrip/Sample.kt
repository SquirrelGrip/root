package com.github.squirrelgrip

import com.fasterxml.jackson.annotation.JsonProperty

data class Sample(
    @JsonProperty("v")
    val v: Int = 0,
    @JsonProperty("s")
    val s: String = "A Simple String",
    @JsonProperty("m")
    val m: Map<String, String> = mapOf("a" to "AAA"),
    @JsonProperty("l")
    val l: List<String> = listOf("1", "AAA")
)
