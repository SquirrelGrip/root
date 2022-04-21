package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log
import java.io.File
import java.time.Instant

class LocalArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val log: Log,
    val updateInterval: Long = 60 * 60 * 24,
) : ArtifactDetailsFactory {
    companion object {
        val regex = "maven-metadata-.*\\.xml".toRegex()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        getMavenMetaData(artifact).toVersions()

    private fun getMavenMetaData(artifact: ArtifactDetails): List<MavenMetaData> =
        getFiles(artifact).map {
            it.toInstance()
        }

    fun getFiles(artifact: ArtifactDetails): Array<File> {
        val directory = File(localRepository.basedir, artifact.getDirectory())
        return if (directory.exists()) {
            directory.listFiles { _, name -> name.matches(regex) }
        } else {
            emptyArray()
        }
    }

    override fun hasMetaData(artifact: ArtifactDetails): Boolean =
        getFiles(artifact).isNotEmpty()

    override fun metaDataUp2Date(artifact: ArtifactDetails): Boolean {
        val lastUpdateInstant = getMavenMetaData(artifact).map {
            it.versioning.updatedDateTime
        }.maxOrNull() ?: Instant.MIN
        return lastUpdateInstant.plusSeconds(updateInterval).isAfter(Instant.now())
    }
}
