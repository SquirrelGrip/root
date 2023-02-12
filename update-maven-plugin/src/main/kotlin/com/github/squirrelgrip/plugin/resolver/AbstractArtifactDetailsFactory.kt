package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.IgnoredVersion
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log

abstract class AbstractArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val log: Log,
    private val ignoredVersions: List<IgnoredVersion> = emptyList()
) : ArtifactDetailsFactory {
    companion object {
        val STANDARD_IGNORED_VERSION: List<String> = listOf(
            ".*-SNAPSHOT",
            """\d{8}\.\d+"""
        )
    }

    override fun getIgnoredVersions(groupId: String, artifactId: String): Collection<String> =
        ignoredVersions.filter {
            it.groupId == groupId && it.artifactId == artifactId
        }.map {
            it.version
        } + STANDARD_IGNORED_VERSION
}