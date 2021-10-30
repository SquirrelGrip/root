package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper

class Versioning(
    @JsonProperty("latest")
    val latest: String?,
    @JsonProperty("release")
    val release: String?,
    @JsonProperty("versions")
    @JacksonXmlElementWrapper(useWrapping = true)
    val versions: List<String>,
    @JsonProperty("lastUpdated")
    val lastUpdated: String?,
)
