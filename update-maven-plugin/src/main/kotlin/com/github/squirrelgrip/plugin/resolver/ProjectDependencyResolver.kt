package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.project.MavenProject

class ProjectDependencyResolver(
    artifactMetadataSource: ArtifactMetadataSource,
    localRepository: ArtifactRepository,
    private val dependencyRepositories: List<ArtifactRepository>,
    private val pluginRepositories: List<ArtifactRepository>
) : AbstractMavenDependencyResolver(
    artifactMetadataSource,
    localRepository,
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
        ).toArtifactDetails(dependencyRepositories)

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean
    ): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectPlugins(processPluginDependencies),
            project.getProjectManagedPlugins(processPluginDependenciesInPluginManagement)
        ).toArtifactDetails(pluginRepositories)

}





