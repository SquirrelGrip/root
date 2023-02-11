package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.IgnoreVersions
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log

abstract class AbstractArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val log: Log,
    private val ignoredVersions: List<IgnoreVersions> = emptyList()
) : ArtifactDetailsFactory {
    override fun getIgnoredVersions(groupId: String, artifactId: String): Collection<String> =
        ignoredVersions.filter {
            it.groupIdRegEx.matches(groupId) && it.artifactIdRegEx.matches(artifactId)
        }.map {
            it.expression
        }
}