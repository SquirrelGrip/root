package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty

data class DependencyUpdatesReport(
    @JsonProperty("summary")
    val summary: Summary,
    @JsonProperty("dependencyManagements")
    val dependencyManagements: DependencyManagements,
    @JsonProperty("dependencies")
    val dependencies: Dependencies,
) {
    fun getDependencies(properties: Map<String, String>): Collection<ArtifactDetails> =
        ((dependencyManagements.dependencyManagement ?: emptyList()) + (dependencies.dependency ?: emptyList())).map {
            it.toArtifactDetails(properties)
        }
}

