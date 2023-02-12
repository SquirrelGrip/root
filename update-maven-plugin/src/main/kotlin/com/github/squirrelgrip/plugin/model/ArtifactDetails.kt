package com.github.squirrelgrip.plugin.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ArtifactDetails(
    @JsonProperty("groupId")
    val groupId: String,
    @JsonProperty("artifactId")
    val artifactId: String,
    @JsonProperty("currentVersion")
    val currentVersion: Version,
    @JsonProperty("versions")
    val versions: Collection<Version> = emptyList()
) : Comparable<ArtifactDetails> {

    val sortedVersions: List<Version> by lazy {
        versions
            .filter {
                it > currentVersion
            }
            .sorted()
    }

    val nextVersion: Version
        get() = firstVersion {
            true
        }

    val latestIncremental: Version
        get() = lastVersion {
            it.major == currentVersion.major && it.minor == currentVersion.minor
        }

    val nextMinor: Version
        get() = firstVersion {
            it.major == currentVersion.major && it.minor != currentVersion.minor
        }

    val latestMinor: Version
        get() = lastVersion {
            it.major == currentVersion.major && it.minor != currentVersion.minor
        }

    val nextMajor: Version
        get() = firstVersion {
            it.major != currentVersion.major
        }

    val latest: Version
        get() = lastVersion {
            true
        }

    private fun firstVersion(predicate: (Version) -> Boolean): Version =
        sortedVersions.firstOrNull(predicate) ?: Version.NO_VERSION

    private fun lastVersion(predicate: (Version) -> Boolean): Version =
        sortedVersions.lastOrNull(predicate) ?: Version.NO_VERSION

    val values: List<String>
        get() = listOf(
            groupId,
            artifactId,
            currentVersion.toString(),
            nextVersion.toString(),
            latestIncremental.toString(),
            nextMinor.toString(),
            latestMinor.toString(),
            nextMajor.toString(),
            latest.toString()
        )

    override fun compareTo(other: ArtifactDetails): Int {
        val primaryComparison = artifactId.compareTo(other.artifactId)
        return if (primaryComparison == 0) {
            val secondaryComparison = groupId.compareTo(other.groupId)
            secondaryComparison
        } else {
            primaryComparison
        }
    }
}
