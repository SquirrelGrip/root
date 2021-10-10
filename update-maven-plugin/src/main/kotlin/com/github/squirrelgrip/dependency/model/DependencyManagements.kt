package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

data class DependencyManagements(
    @JsonProperty("dependencyManagement")
    @JacksonXmlElementWrapper(useWrapping = false)
    val dependencyManagement: List<Artifact>? = emptyList()
)