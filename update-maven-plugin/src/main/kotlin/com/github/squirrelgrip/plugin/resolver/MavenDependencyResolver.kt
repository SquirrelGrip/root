package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.json.toJson
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.DependencyType
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.model.Dependency
import org.apache.maven.project.MavenProject
import java.io.FileWriter

class MavenDependencyResolver(
    private val artifactMetadataSource: ArtifactMetadataSource,
    private val localRepository: ArtifactRepository,
    private val remoteRepositories: List<ArtifactRepository>,
    private val pluginArtifactRepositories: List<ArtifactRepository>,
    private val session: MavenSession,
    private val includeDependencyManagement: Boolean
) : DependencyResolver {
    override fun getDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        session.projects
            .flatMap {
                it.getDependencies(includeDependencyManagement)
            }
            .distinctBy {
                "${it.groupId}:${it.artifactId}"
            }
            .sortedBy {
                "${it.groupId}:${it.artifactId}"
            }
            .map {
                getArtifactDetails(
                    it.groupId,
                    it.artifactId,
                    it.version ?: "0.0",
                    false,
                )
            }

    override fun getPluginArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        session.projects
            .flatMap {
                (it.buildPlugins ?: emptyList()) + (it.pluginManagement?.plugins ?: emptyList())
            }
            .distinctBy {
                "${it.groupId}:${it.artifactId}"
            }
            .sortedBy {
                "${it.groupId}:${it.artifactId}"
            }
            .map {
                getArtifactDetails(
                    it.groupId,
                    it.artifactId,
                    it.version ?: "0.0",
                    true,
                )
            }

    private fun getArtifactDetails(
        groupId: String,
        artifactId: String,
        version: String,
        usePluginRepositories: Boolean,
    ): ArtifactDetails {
        val artifact = DefaultArtifact(groupId, artifactId, version, "", "", "", DefaultArtifactHandler())
        val remoteRepositories = if (usePluginRepositories) pluginArtifactRepositories else remoteRepositories
        val versions =
            artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories)
                .map {
                    Version(it.toString())
                }
        return ArtifactDetails(groupId, artifactId, Version(version), versions)
    }

    fun MavenProject.getDependencies(includeDependencyManagement: Boolean): List<Dependency> {
        val dependencies = mutableListOf<Dependency>()
        dependencies.addAll(this.dependencies)
        if (includeDependencyManagement) {
            dependencies.addAll(this.dependencyManagement.dependencies)
        }
        return dependencies.toList()
    }
}



