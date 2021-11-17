package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject


abstract class AbstractMavenDependencyResolver(
    localRepository: ArtifactRepository,
    remoteRepositories: List<ArtifactRepository>,
    pluginRepositories: List<ArtifactRepository>,
    val log: Log,
) : DependencyResolver {
    companion object {
        val defaultArtifactHandler = DefaultArtifactHandler()
    }

    val localArtifactDetailsFactory =
        LocalArtifactDetailsFactory(localRepository, log)

    val remoteArtifactDetailsFactory =
        RemoteArtifactDetailsFactory(localRepository, remoteRepositories, log)

    fun Plugin.toArtifact(): Artifact =
        DefaultArtifact(groupId, artifactId, version ?: "0.0", "", "", "", defaultArtifactHandler)

    fun Dependency.toArtifact(): Artifact =
        DefaultArtifact(groupId, artifactId, version ?: "0.0", "", "", "", defaultArtifactHandler)

    fun MavenProject.getProjectDependencies(
        processDependencies: Boolean,
        processTransitive: Boolean,
    ): List<Artifact> =
        if (processDependencies) {
            if (processTransitive) {
                dependencies
            } else {
                originalModel.dependencies.map {
                    it.getEquivalentDependency(this.dependencies)
                }
            }.map {
                it.toArtifact()
            }
        } else {
            emptyList()
        }

    private fun Dependency.getEquivalentDependency(dependencies: List<Dependency>): Dependency =
        dependencies.firstOrNull {
            it.groupId == groupId && it.artifactId == artifactId
        } ?: this

    fun MavenProject.getProjectManagedDependencies(
        processDependencyManagement: Boolean,
        processTransitive: Boolean,
    ): List<Artifact> =
        if (processDependencyManagement) {
            if (processTransitive) {
                (dependencyManagement?.dependencies ?: emptyList()).map {
                    it.toArtifact()
                }
            } else {
                (originalModel.dependencyManagement?.dependencies ?: emptyList()).map {
                    it.getEquivalentDependency(this.dependencies)
                }.map {
                    it.toArtifact()
                }
            }
        } else {
            emptyList()
        }

    fun MavenProject.getProjectPlugins(
        processPluginDependencies: Boolean,
    ): List<Artifact> =
        if (processPluginDependencies) {
            buildPlugins.map { it.toArtifact() }
        } else {
            emptyList()
        }

    fun MavenProject.getProjectManagedPlugins(
        processPluginDependenciesInPluginManagement: Boolean,
    ): List<Artifact> =
        if (processPluginDependenciesInPluginManagement) {
            (pluginManagement?.plugins ?: emptyList()).map {
                it.toArtifact()
            }
        } else {
            emptyList()
        }

    fun getArtifactDetails(
        artifacts: List<Artifact>,
        managedArtifacts: List<Artifact>,
    ): List<Artifact> =
        artifacts + managedArtifacts

    fun List<Artifact>.toArtifactDetails(): List<ArtifactDetails> =
        distinctBy {
            "${it.groupId}:${it.artifactId}"
        }.sortedBy {
            "${it.groupId}:${it.artifactId}"
        }.map {
            it.getArtifactDetails()
        }

    private fun Artifact.getArtifactDetails(): ArtifactDetails =
        localArtifactDetailsFactory.create(groupId, artifactId, version).enrich()

    private fun ArtifactDetails.enrich(): ArtifactDetails {
        log.debug("Enrich: $groupId:$artifactId")
        if (localArtifactDetailsFactory.hasMetaData(this) && localArtifactDetailsFactory.metaDataUp2Date(this)) {
            return this.copy(versions = localArtifactDetailsFactory.getAvailableVersions(this))
        }
        return this.copy(versions = remoteArtifactDetailsFactory.getAvailableVersions(this))
    }

}



