package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import java.util.*

data class UpdateArtifact(
    @JsonProperty("groupId")
    val groupId: String,
    @JsonProperty("artifactId")
    val artifactId: String,
    @JsonProperty("scope")
    val scope: String? = "",
    @JsonProperty("classifier")
    val classifier: String? = "",
    @JsonProperty("type")
    val type: String? = "",
    @JsonProperty("currentVersion")
    val currentVersion: Version,
    @JsonProperty("nextVersion")
    val nextVersion: Version = Version.NO_VERSION,
    @JsonProperty("status")
    val status: String,
    @JacksonXmlElementWrapper(useWrapping = true, localName = "incrementals")
    @JsonProperty("incrementals")
    val incrementals: List<Version>? = emptyList(),
    @JacksonXmlElementWrapper(useWrapping = true, localName = "minors")
    @JsonProperty("minors")
    val minors: List<Version>? = emptyList(),
    @JacksonXmlElementWrapper(useWrapping = true, localName = "majors")
    @JsonProperty("majors")
    val majors: List<Version>? = emptyList(),
) {
    fun toArtifactDetails(properties: Map<String, String>): ArtifactDetails =
        ArtifactDetails(
            groupId,
            artifactId,
            currentVersion.resolve(properties),
            (incrementals ?: emptyList()) + (minors ?: emptyList()) + (majors ?: emptyList())
        )
}