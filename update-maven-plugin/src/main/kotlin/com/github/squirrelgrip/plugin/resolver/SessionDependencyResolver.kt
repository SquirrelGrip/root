package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.project.MavenProject

class SessionDependencyResolver(
    artifactMetadataSource: ArtifactMetadataSource,
    localRepository: ArtifactRepository,
    private val dependencyRepositories: List<ArtifactRepository>,
    private val pluginRepositories: List<ArtifactRepository>,
    private val session: MavenSession,
) : AbstractMavenDependencyResolver(
    artifactMetadataSource,
    localRepository
) {
    override fun getDependencyArtifacts(
        project: MavenProject,
        processDependencies: Boolean,
        processDependencyManagement: Boolean,
        processTransitive: Boolean
    ): List<ArtifactDetails> {
        val dependencies = session.projects.flatMap {
            it.getProjectDependencies(processDependencies, processTransitive)
        }
        val managedDependencies = session.projects.flatMap {
            it.getProjectManagedDependencies(processDependencyManagement, processTransitive)
        }
        return getArtifactDetails(dependencies, managedDependencies).toArtifactDetails(dependencyRepositories)
    }

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean
    ): List<ArtifactDetails> {
        val plugins = session.projects.flatMap {
            it.getProjectPlugins(processPluginDependencies)
        }
        val managedPlugins = session.projects.flatMap {
            it.getProjectManagedPlugins(processPluginDependenciesInPluginManagement)
        }
        return getArtifactDetails(plugins, managedPlugins).toArtifactDetails(pluginRepositories)
    }
}



