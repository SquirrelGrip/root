package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version

interface ArtifactDetailsFactory {

    fun create(groupId: String, artifactId: String, version: String): ArtifactDetails =
        ArtifactDetails(groupId, artifactId, Version(version))

    fun ArtifactDetails.getMavenMetaDataFile(id: String = ""): String =
        if (id.isNotBlank()) {
            "${this.getDirectory()}/maven-metadata-$id.xml"
        } else {
            "${this.getDirectory()}/maven-metadata.xml"
        }

    fun List<MavenMetaData>.toVersions(): List<Version> =
        this
            .flatMap { metaData ->
                val ignoredVersions = getIgnoredVersions(metaData.groupId, metaData.artifactId)
                val versions = metaData.versioning.versions
                if (ignoredVersions.isNotEmpty()) {
                    versions.filter {  version ->
                        !ignoredVersions.any {
                            it.toRegex().matches(version)
                        }
                    }
                } else {
                    versions
                }
            }
            .distinct()
            .map {
                Version(it)
            }

    fun ArtifactDetails.getDirectory(): String =
        "${groupId.replace(".", "/")}/$artifactId"

    fun getAvailableVersions(artifact: ArtifactDetails): List<Version>
    fun hasMetaData(artifact: ArtifactDetails): Boolean
    fun metaDataUp2Date(artifact: ArtifactDetails): Boolean
    fun getIgnoredVersions(groupId: String, artifactId: String): Collection<String>
}
