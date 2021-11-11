package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.time.toLocalDateTime
import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.extension.xml.toXml
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.model.Versioning
import org.apache.maven.artifact.repository.ArtifactRepository
import org.sonatype.aether.repository.LocalRepository
import java.io.File
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.ws.rs.client.ClientBuilder

class RemoteArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val remoteRepositories: List<ArtifactRepository>
) : ArtifactDetailsFactory {
    companion object {
        val client = ClientBuilder.newClient()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        remoteRepositories.associateWith {
            client.target(it.url)
        }.map {(repository, target) ->
            target.path(artifact.getMavenMetaDataFile()).request().get().use {
                it.readEntity(String::class.java).toInstance<MavenMetaData>().also {
                    it.updateTime().toXml(File(localRepository.basedir, artifact.getMavenMetaDataFile(repository.id)))
                }
            }
        }.toVersions()

    override fun hasMetaData(artifact: ArtifactDetails): Boolean =
        true

}