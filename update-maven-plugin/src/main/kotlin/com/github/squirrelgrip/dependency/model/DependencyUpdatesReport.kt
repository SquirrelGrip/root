package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.github.squirrelgrip.dependency.model.DependencyManagements

data class DependencyUpdatesReport(
    @JsonProperty("summary")
    val summary: Summary,
    @JsonProperty("dependencyManagements")
    val dependencyManagements: DependencyManagements,
    @JsonProperty("dependencies")
    val dependencies: Dependencies,
) {
    fun getDependencies() : List<Artifact> {
        val a = dependencyManagements.dependencyManagement ?: emptyList()
        val b = dependencies.dependency ?: emptyList()
        return (a + b).sorted()
    }
}