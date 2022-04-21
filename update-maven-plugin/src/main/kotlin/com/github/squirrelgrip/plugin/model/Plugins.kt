package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class Plugins(
    @JsonProperty("plugin")
    @JacksonXmlElementWrapper(useWrapping = false)
    val plugin: List<UpdateArtifact>? = emptyList(),
)
