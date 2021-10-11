package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class Dependencies(
    @JsonProperty("dependency")
    @JacksonXmlElementWrapper(useWrapping = false)
    val dependency: List<UpdateArtifact>? = emptyList()
)