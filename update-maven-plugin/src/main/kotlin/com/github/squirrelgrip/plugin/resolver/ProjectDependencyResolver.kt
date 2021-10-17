package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.project.MavenProject

class ProjectDependencyResolver(
    artifactMetadataSource: ArtifactMetadataSource,
    localRepository: ArtifactRepository,
    private val dependencyRepositories: List<ArtifactRepository>,
    private val pluginRepositories: List<ArtifactRepository>,
    includeManagement: Boolean
) : AbstractMavenDependencyResolver(
    artifactMetadataSource,
    localRepository,
    includeManagement
) {

    override fun getDependencyArtifacts(project: MavenProject): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectDependencies(),
            project.getProjectManagedDependencies()
        ).toArtifactDetails(dependencyRepositories)

    override fun getPluginArtifacts(project: MavenProject): List<ArtifactDetails> =
        getArtifactDetails(
            project.getProjectPlugins(),
            project.getProjectManagedPlugins()
        ).toArtifactDetails(pluginRepositories)

}





