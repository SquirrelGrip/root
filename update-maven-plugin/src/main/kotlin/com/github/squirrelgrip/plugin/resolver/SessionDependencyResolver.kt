package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject

class SessionDependencyResolver(
    localRepository: ArtifactRepository,
    remoteRepositories: List<ArtifactRepository>,
    pluginRepositories: List<ArtifactRepository>,
    private val session: MavenSession,
    log: Log,
) : AbstractMavenDependencyResolver(
    localRepository,
    remoteRepositories,
    pluginRepositories,
    log
) {
    override fun getDependencyArtifacts(
        project: MavenProject,
        processDependencies: Boolean,
        processDependencyManagement: Boolean,
        processTransitive: Boolean,
    ): List<ArtifactDetails> {
        val dependencies = session.projects.flatMap {
            it.getProjectDependencies(processDependencies, processTransitive)
        }
        val managedDependencies = session.projects.flatMap {
            it.getProjectManagedDependencies(processDependencyManagement, processTransitive)
        }
        return getArtifactDetails(dependencies, managedDependencies).toArtifactDetails(false)
    }

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean,
    ): List<ArtifactDetails> {
        val plugins = session.projects.flatMap {
            it.getProjectPlugins(processPluginDependencies)
        }
        val managedPlugins = session.projects.flatMap {
            it.getProjectManagedPlugins(processPluginDependenciesInPluginManagement)
        }
        return getArtifactDetails(plugins, managedPlugins).toArtifactDetails(true)
    }
}
