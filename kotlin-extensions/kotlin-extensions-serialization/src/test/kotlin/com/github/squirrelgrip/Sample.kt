package com.github.squirrelgrip

import kotlinx.serialization.Serializable

@Serializable
data class Sample(
    val v: Int = 0,
    val s: String = "A Simple String",
    val m: Map<String, String> = mapOf("a" to "AAA"),
    val l: List<String> = listOf("1", "AAA")
)
