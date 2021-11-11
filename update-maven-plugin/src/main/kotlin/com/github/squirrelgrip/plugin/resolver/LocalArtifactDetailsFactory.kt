package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepository
import java.io.File

class LocalArtifactDetailsFactory(
    val localRepository: ArtifactRepository
): ArtifactDetailsFactory {
    companion object {
        val regex = "maven-metadata-.*\\.xml".toRegex()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        getFiles(artifact).map {
            it.toInstance<MavenMetaData>()
        }.toVersions()


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

}