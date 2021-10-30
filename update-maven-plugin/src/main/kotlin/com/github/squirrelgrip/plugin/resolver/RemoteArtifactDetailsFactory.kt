package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepository
import javax.ws.rs.client.ClientBuilder

class RemoteArtifactDetailsFactory(
    val remoteRepositories: List<ArtifactRepository>
) : ArtifactDetailsFactory {
    companion object {
        val client = ClientBuilder.newClient()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        remoteRepositories.map {
            client.target(it.url)
        }.map {
            it.path(artifact.getMavenMetaDataFile()).request().get().use {
                it.readEntity(String::class.java)
            }
        }.map {
            it.toInstance<MavenMetaData>()
        }.toVersions()

}