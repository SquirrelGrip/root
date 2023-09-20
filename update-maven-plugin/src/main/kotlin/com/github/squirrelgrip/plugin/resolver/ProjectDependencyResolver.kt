package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.IgnoredVersion
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.eclipse.aether.repository.LocalRepository

class ProjectDependencyResolver(
    localRepository: LocalRepository,
    remoteRepositories: List<MavenArtifactRepository>,
    pluginRepositories: List<MavenArtifactRepository>,
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
    ): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectDependencies(processDependencies, processTransitive),
            project.getProjectManagedDependencies(processDependencyManagement, processTransitive)
        ).toArtifactDetails(false)

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean,
    ): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectPlugins(processPluginDependencies),
            project.getProjectManagedPlugins(processPluginDependenciesInPluginManagement)
        ).toArtifactDetails(true)
}
