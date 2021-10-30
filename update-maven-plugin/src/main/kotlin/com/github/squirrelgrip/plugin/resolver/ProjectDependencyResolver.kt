package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.project.MavenProject

class ProjectDependencyResolver(
    localRepository: ArtifactRepository,
    remoteRepositories: List<ArtifactRepository>,
    pluginRepositories: List<ArtifactRepository>
) : AbstractMavenDependencyResolver(
    localRepository,
    remoteRepositories,
    pluginRepositories
) {

    override fun getDependencyArtifacts(
        project: MavenProject,
        processDependencies: Boolean,
        processDependencyManagement: Boolean,
        processTransitive: Boolean
    ): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectDependencies(processDependencies, processTransitive),
            project.getProjectManagedDependencies(processDependencyManagement, processTransitive)
        ).toArtifactDetails()

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean
    ): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectPlugins(processPluginDependencies),
            project.getProjectManagedPlugins(processPluginDependenciesInPluginManagement)
        ).toArtifactDetails()

}





