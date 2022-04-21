package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Summary(
    @JsonProperty("usingLastVersion")
    val usingLastVersion: Int = 0,
    @JsonProperty("nextVersionAvailable")
    val nextVersionAvailable: Int = 0,
    @JsonProperty("nextIncremetalAvailable")
    val nextIncremetalAvailable: Int = 0,
    @JsonProperty("nextMinorAvailable")
    val nextMinorAvailable: Int = 0,
    @JsonProperty("nextMajorAvailable")
    val nextMajorAvailable: Int = 0,
)
