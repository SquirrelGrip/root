package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.extension.xml.toXml
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.model.Versioning
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder
import org.apache.hc.core5.http.ssl.TLS
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class RemoteArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val remoteRepositories: List<ArtifactRepository>,
    val log: Log,
) : ArtifactDetailsFactory {
    companion object {
        private val sslContext = SSLContext.getInstance("TLSv1.2").also {
            it.init(null, arrayOf(InsecureTrustManager()), SecureRandom())
        }
        private val sslSocketFactory = SSLConnectionSocketFactoryBuilder.create()
            .setSslContext(sslContext)
            .build()
        private val connectionManager = PoolingHttpClientConnectionManagerBuilder.create()
            .setSSLSocketFactory(sslSocketFactory)
            .build()
        private val client = HttpClients.custom()
            .setConnectionManager(connectionManager)
            .build()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        remoteRepositories
            .filter {
                it.releases?.isEnabled ?: true
            }
            .associateWith {
                HttpGet("${it.url}/${artifact.getMavenMetaDataFile()}")
            }
            .map { (repository, request) ->
                client.execute(request).use {
                    try {
                        it.entity.content.toInstance()
                    } catch (e: Exception) {
                        e.printStackTrace()
                        MavenMetaData(
                            artifact.groupId,
                            artifact.artifactId,
                            null,
                            artifact.currentVersion.value,
                            Versioning()
                        )
                    }.apply {
                        println(this)
                        try {
                            val file =
                                File(
                                    localRepository.basedir,
                                    artifact.getMavenMetaDataFile(repository.id)
                                ).also { file ->
                                    file.parentFile.mkdirs()
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

    override fun metaDataUp2Date(artifact: ArtifactDetails): Boolean =
        false
}

class InsecureTrustManager : X509TrustManager {
    override fun checkClientTrusted(chain: Array<X509Certificate>, authType: String) {
        // Everyone is trusted!
    }

    override fun checkServerTrusted(chain: Array<X509Certificate>, authType: String) {
        // Everyone is trusted!
    }

    override fun getAcceptedIssuers(): Array<X509Certificate> = emptyArray()
}
