package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.project.MavenProject

interface DependencyResolver {
    fun getDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails>
    fun getPluginArtifacts(project: MavenProject): Collection<ArtifactDetails>
}