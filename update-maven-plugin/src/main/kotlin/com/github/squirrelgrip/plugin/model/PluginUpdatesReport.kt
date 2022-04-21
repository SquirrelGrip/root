package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty
import org.apache.maven.project.MavenProject

data class PluginUpdatesReport(
    @JsonProperty("summary")
    var summary: Summary,
    @JsonProperty("pluginManagements")
    var pluginManagements: PluginManagements?,
    @JsonProperty("plugins")
    var plugins: Plugins?,
) {
    fun getDependencies(project: MavenProject): Collection<ArtifactDetails> =
        ((pluginManagements?.pluginManagement ?: emptyList()) + (plugins?.plugin ?: emptyList())).map {
            it.toArtifactDetails(project)
        }
}
