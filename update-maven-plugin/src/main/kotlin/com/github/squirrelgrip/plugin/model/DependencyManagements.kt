package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class DependencyManagements(
    @JsonProperty("dependencyManagement")
    @JacksonXmlElementWrapper(useWrapping = false)
    val dependencyManagement: List<UpdateArtifact>? = emptyList(),
)
