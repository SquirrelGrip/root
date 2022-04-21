package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.maven.project.MavenProject

data class DependencyUpdatesReport(
    @JsonProperty("summary")
    val summary: Summary,
    @JsonProperty("dependencyManagements")
    val dependencyManagements: DependencyManagements,
    @JsonProperty("dependencies")
    val dependencies: Dependencies,
) {
    fun getDependencies(project: MavenProject): Collection<ArtifactDetails> =
        ((dependencyManagements.dependencyManagement ?: emptyList()) + (dependencies.dependency ?: emptyList())).map {
            it.toArtifactDetails(project)
        }
}
