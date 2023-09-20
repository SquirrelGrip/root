package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.IgnoredVersion
import org.apache.maven.plugin.logging.Log
import org.eclipse.aether.repository.LocalRepository

abstract class AbstractArtifactDetailsFactory(
    val localRepository: LocalRepository,
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
            if (it.groupId != null) {
                it.groupId == groupId
            } else {
                if (it.groupIdRegEx != null) {
                    it.groupIdRegEx!!.toRegex().matches(groupId)
                } else {
                    throw IllegalArgumentException("Either groupId or groupIdRegEx must be specified.")
                }
            }
        }.filter {
            if (it.artifactId != null) {
                it.artifactId == artifactId
            } else {
                if (it.artifactIdRegEx != null) {
                    it.artifactIdRegEx!!.toRegex().matches(artifactId)
                } else {
                    throw IllegalArgumentException("Either artifactId or artifactIdRegEx must be specified.")
                }
            }
        }.map {
            it.version
        } + STANDARD_IGNORED_VERSION
}