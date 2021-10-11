package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty

data class PluginUpdatesReport (
    @JsonProperty("summary")
    var summary: Summary,
    @JsonProperty("pluginManagements")
    var pluginManagements: PluginManagements?,
    @JsonProperty("plugins")
    var plugins: Plugins?,
) {
    fun getDependencies(properties: Map<String, String>): Collection<ArtifactDetails> =
        ((pluginManagements?.pluginManagement ?: emptyList()) + (plugins?.plugin ?: emptyList())).map {
            it.toArtifactDetails(properties)
        }
}
