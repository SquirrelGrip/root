package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.extension.xml.toXml
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.model.Versioning
import org.apache.maven.artifact.repository.ArtifactRepository
import java.io.File
import javax.ws.rs.client.ClientBuilder

class RemoteArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val remoteRepositories: List<ArtifactRepository>,
) : ArtifactDetailsFactory {
    companion object {
        val client = ClientBuilder.newClient()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        remoteRepositories.associateWith {
            client.target(it.url)
        }.map { (repository, target) ->
            target.path(artifact.getMavenMetaDataFile()).request().get().use {
                val entity = it.readEntity(String::class.java)
                try {
                    entity.toInstance()
                } catch (e: Exception) {
                    MavenMetaData(artifact.groupId,
                        artifact.artifactId,
                        null,
                        artifact.currentVersion.value,
                        Versioning())
                }.apply {
                    try {
                        val file = File(localRepository.basedir, artifact.getMavenMetaDataFile(repository.id)).also {
                            it.parentFile.mkdirs()
                        }
                        updateTime().toXml(file)
                    } catch (e: Exception) {
                        // Ignore
                    }
                }
            }
        }.toVersions()

    override fun hasMetaData(artifact: ArtifactDetails): Boolean =
        true

}