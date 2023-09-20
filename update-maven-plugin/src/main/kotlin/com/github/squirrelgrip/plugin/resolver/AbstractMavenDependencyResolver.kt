package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.IgnoredVersion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.apache.maven.artifact.Artifact
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugin.logging.Log
import org.apache.maven.project.MavenProject
import org.eclipse.aether.repository.LocalRepository
import java.util.Properties

abstract class AbstractMavenDependencyResolver(
    localRepository: LocalRepository,
    remoteRepositories: List<MavenArtifactRepository>,
    pluginRepositories: List<MavenArtifactRepository>,
    val log: Log,
    val ignoredVersions: List<IgnoredVersion>
) : DependencyResolver {
    companion object {
        val defaultArtifactHandler = DefaultArtifactHandler()
        val regex = "\\\$\\{(.+)\\}".toRegex()
    }

    val localArtifactDetailsFactory =
        LocalArtifactDetailsFactory(localRepository, ignoredVersions, log)

    val remoteArtifactDetailsFactory =
        RemoteArtifactDetailsFactory(localRepository, ignoredVersions, log, remoteRepositories)

    val pluginArtifactDetailsFactory =
        RemoteArtifactDetailsFactory(localRepository, ignoredVersions, log, pluginRepositories)

    fun Plugin.toArtifact(properties: Properties): Artifact =
        DefaultArtifact(groupId, artifactId, getCurrentVersion(version, properties), "", "", "", defaultArtifactHandler)

    fun Dependency.toArtifact(properties: Properties): Artifact =
        DefaultArtifact(groupId, artifactId, getCurrentVersion(version, properties), "", "", "", defaultArtifactHandler)

    private fun getCurrentVersion(version: String?, properties: Properties): String =
        (version ?: "0.0").let { raw ->
            regex.find(raw)?.let {
                properties.get(it.groupValues[1]).toString()
            } ?: raw
        }

    fun MavenProject.getProjectDependencies(
        processDependencies: Boolean,
        processTransitive: Boolean,
    ): List<Artifact> =
        if (processDependencies) {
            if (processTransitive) {
                dependencies
            } else {
                originalModel.dependencies
                    .map {
                        it.getEquivalentDependency(this.dependencies)
                    }
            }.map {
                it.toArtifact(properties)
            }
        } else {
            emptyList()
        }.also {
            log.debug("$it")
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
                    it.toArtifact(properties)
                }
            } else {
                (originalModel.dependencyManagement?.dependencies ?: emptyList()).map {
                    it.getEquivalentDependency(this.dependencies)
                }.map {
                    it.toArtifact(properties)
                }
            }
        } else {
            emptyList()
        }

    fun MavenProject.getProjectPlugins(
        processPluginDependencies: Boolean,
    ): List<Artifact> =
        if (processPluginDependencies) {
            buildPlugins.map { it.toArtifact(properties) }
        } else {
            emptyList()
        }

    fun MavenProject.getProjectManagedPlugins(
        processPluginDependenciesInPluginManagement: Boolean,
    ): List<Artifact> =
        if (processPluginDependenciesInPluginManagement) {
            (pluginManagement?.plugins ?: emptyList()).map {
                it.toArtifact(properties)
            }
        } else {
            emptyList()
        }

    fun getArtifactDetails(
        artifacts: List<Artifact>,
        managedArtifacts: List<Artifact>,
    ): List<Artifact> =
        artifacts + managedArtifacts

    fun List<Artifact>.toArtifactDetails(pluginArtifact: Boolean): List<ArtifactDetails> =
        runBlocking(Dispatchers.Default) {
            distinctBy {
                "${it.groupId}:${it.artifactId}"
            }.sortedBy {
                "${it.groupId}:${it.artifactId}"
            }.let {
                it.map {
                    async { it.getArtifactDetails(pluginArtifact) }
                }.awaitAll()
            }
        }

    private fun Artifact.getArtifactDetails(pluginArtifact: Boolean): ArtifactDetails =
        localArtifactDetailsFactory.create(groupId, artifactId, version).enrich(pluginArtifact)

    private fun ArtifactDetails.enrich(pluginArtifact: Boolean): ArtifactDetails {
        log.debug("Enrich: $groupId:$artifactId")
        if (localArtifactDetailsFactory.hasMetaData(this) && localArtifactDetailsFactory.metaDataUp2Date(this)) {
            return this.copy(versions = localArtifactDetailsFactory.getAvailableVersions(this))
        }
        return if (pluginArtifact) {
            this.copy(versions = pluginArtifactDetailsFactory.getAvailableVersions(this))
        } else {
            this.copy(versions = remoteArtifactDetailsFactory.getAvailableVersions(this))
        }
    }
}
