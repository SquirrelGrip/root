package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.IgnoredVersion
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.eclipse.aether.repository.LocalRepository

class SessionDependencyResolver(
    localRepository: LocalRepository,
    remoteRepositories: List<MavenArtifactRepository>,
    pluginRepositories: List<MavenArtifactRepository>,
    private val session: MavenSession,
    log: Log,
    ignoredVersions: List<IgnoredVersion>
) : AbstractMavenDependencyResolver(
    localRepository,
    remoteRepositories,
    pluginRepositories,
    log,
    ignoredVersions
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
