package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class Plugins (
    @JsonProperty("plugin")
    @JacksonXmlElementWrapper(useWrapping = false)
    val plugin: List<Artifact>? = emptyList()
)