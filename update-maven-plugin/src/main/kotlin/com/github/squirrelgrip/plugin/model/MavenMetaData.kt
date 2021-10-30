package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty

class MavenMetaData(
    @JsonProperty("groupId")
    val groupId: String,
    @JsonProperty("artifactId")
    val artifactId: String,
    @JsonProperty("modelVersion")
    val modelVersion: String?,
    @JsonProperty("version")
    val version: String?,
    @JsonProperty("versioning")
    val versioning: Versioning
)