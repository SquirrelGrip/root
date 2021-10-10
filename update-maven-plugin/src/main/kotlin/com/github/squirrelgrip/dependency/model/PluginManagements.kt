package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class PluginManagements (
    @JsonProperty("pluginManagement")
    @JacksonXmlElementWrapper(useWrapping = false)
    val pluginManagement: List<Artifact>? = emptyList()
)