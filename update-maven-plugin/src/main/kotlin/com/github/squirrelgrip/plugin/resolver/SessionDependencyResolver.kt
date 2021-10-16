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
    includeManagement: Boolean
) : AbstractMavenDependencyResolver(
    artifactMetadataSource,
    localRepository,
    includeManagement
) {
    override fun getDependencyArtifacts(project: MavenProject): List<ArtifactDetails> {
        val dependencies = session.projects.flatMap {
            it.getProjectDependencies()
        }
        val managedDependencies = session.projects.flatMap {
            it.getProjectManagedDependencies()
        }
        return getArtifactDetails(dependencies, managedDependencies).toArtifactDetails(dependencyRepositories)
    }

    override fun getPluginArtifacts(project: MavenProject): List<ArtifactDetails> {
        val plugins = session.projects.flatMap {
            it.getProjectPlugins()
        }
        val managedPlugins = session.projects.flatMap {
            it.getProjectManagedPlugins()
        }
        return getArtifactDetails(plugins, managedPlugins).toArtifactDetails(pluginRepositories)
    }
}



