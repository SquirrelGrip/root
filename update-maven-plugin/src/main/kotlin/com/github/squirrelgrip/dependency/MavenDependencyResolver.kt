package com.github.squirrelgrip.dependency

import com.github.squirrelgrip.dependency.model.ArtifactDetails
import com.github.squirrelgrip.dependency.model.DependencyType
import com.github.squirrelgrip.dependency.model.Version
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.project.MavenProject

class MavenDependencyResolver(
    private val artifactMetadataSource: ArtifactMetadataSource,
    private val localRepository: ArtifactRepository,
    private val remoteRepositories: List<ArtifactRepository>,
    private val pluginArtifactRepositories: List<ArtifactRepository>,
    private val session: MavenSession
) : DependencyResolver {
    override fun getDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        session.projects
            .flatMap {
                getProjectDependencyArtifacts(it) + getProjectDependencyManagementArtifacts(it)
            }
            .sortedBy {
                "${it.groupId}:${it.artifactId}"
            }

    override fun getPluginArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        session.projects
            .flatMap {
                getProjectPluginArtifacts(it) + getProjectPluginManagementArtifacts(it)
            }
            .sortedBy {
                "${it.groupId}:${it.artifactId}"
            }

    fun getProjectDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        project.dependencies
            ?.map {
                getArtifactDetails(
                    it.groupId,
                    it.artifactId,
                    it.version ?: "0",
                    false,
                    project,
                    DependencyType.DEPENDENCY
                )
            } ?: emptyList()


    fun getProjectDependencyManagementArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        project.dependencyManagement?.dependencies
            ?.map {
                getArtifactDetails(
                    it.groupId,
                    it.artifactId,
                    it.version ?: "0",
                    false,
                    project,
                    DependencyType.DEPENDENCY_MANAGEMENT
                )
            } ?: emptyList()

    fun getProjectPluginArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        project.buildPlugins
            ?.map {
                getArtifactDetails(it.groupId, it.artifactId, it.version ?: "0", true, project, DependencyType.PLUGIN)
            } ?: emptyList()

    fun getProjectPluginManagementArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        project.pluginManagement?.plugins
            ?.map {
                getArtifactDetails(
                    it.groupId,
                    it.artifactId,
                    it.version ?: "0",
                    true,
                    project,
                    DependencyType.PLUGIN_MANAGEMENT
                )
            } ?: emptyList()

    private fun getArtifactDetails(
        groupId: String,
        artifactId: String,
        version: String,
        usePluginRepositories: Boolean,
        project: MavenProject,
        dependencyType: DependencyType
    ): ArtifactDetails {
        val artifact = DefaultArtifact(groupId, artifactId, version, "", "", "", DefaultArtifactHandler())
        val remoteRepositories = if (usePluginRepositories) pluginArtifactRepositories else remoteRepositories
        val versions =
            artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories)
                .map {
                    Version(it.toString())
                }
        return ArtifactDetails(groupId, artifactId, Version(version), project.artifactId, dependencyType, versions)
    }

}

