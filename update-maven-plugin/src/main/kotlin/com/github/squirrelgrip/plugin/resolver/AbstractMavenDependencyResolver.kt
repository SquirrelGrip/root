package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.project.MavenProject

abstract class AbstractMavenDependencyResolver(
    private val artifactMetadataSource: ArtifactMetadataSource,
    private val localRepository: ArtifactRepository,
    private val includeManagement: Boolean
): DependencyResolver {
    companion object {
        val defaultArtifactHandler = DefaultArtifactHandler()
    }

    fun Plugin.toArtifact(): Artifact =
        DefaultArtifact(groupId, artifactId, version ?: "0.0", "", "", "", defaultArtifactHandler)

    fun Dependency.toArtifact(): Artifact =
        DefaultArtifact(groupId, artifactId, version ?: "0.0", "", "", "", defaultArtifactHandler)

    fun MavenProject.getProjectDependencies() =
        dependencies.map { it.toArtifact() }

    fun MavenProject.getProjectManagedDependencies() =
        (dependencyManagement?.dependencies ?: emptyList()).map { it.toArtifact() }

    fun MavenProject.getProjectPlugins() =
        buildPlugins.map { it.toArtifact() }

    fun MavenProject.getProjectManagedPlugins() =
        (pluginManagement?.plugins ?: emptyList()).map { it.toArtifact() }

    fun getArtifactDetails(
        artifacts: List<Artifact>,
        managedArtifacts: List<Artifact>
    ): List<Artifact> =
        artifacts.toMutableList().also {
            if (includeManagement) {
                it.addAll(managedArtifacts)
            }
        }.toList()

    fun List<Artifact>.toArtifactDetails(
        artifactRepositories: List<ArtifactRepository>
    ): List<ArtifactDetails> =
        distinctBy {
            "${it.groupId}:${it.artifactId}"
        }.sortedBy {
            "${it.groupId}:${it.artifactId}"
        }.map {
            it.getArtifactDetails(artifactRepositories)
        }

    private fun Artifact.getArtifactDetails(
        remoteRepositories: List<ArtifactRepository>,
    ) =
        ArtifactDetails(
            groupId,
            artifactId,
            Version(version),
            artifactMetadataSource.retrieveAvailableVersions(
                this,
                localRepository,
                remoteRepositories
            ).map { Version(it.toString()) }
        )

}