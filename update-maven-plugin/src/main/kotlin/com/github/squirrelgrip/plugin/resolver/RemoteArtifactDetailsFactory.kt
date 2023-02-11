package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.extension.xml.toXml
import com.github.squirrelgrip.plugin.IgnoreVersions
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.model.Versioning
import org.apache.hc.client5.http.classic.methods.HttpGet
import org.apache.hc.client5.http.impl.classic.AbstractHttpClientResponseHandler
import org.apache.hc.client5.http.impl.classic.HttpClients
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactoryBuilder
import org.apache.hc.core5.http.HttpEntity
import org.apache.hc.core5.http.io.HttpClientResponseHandler
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.SSLContext
import javax.net.ssl.X509TrustManager

class RemoteArtifactDetailsFactory(
    localRepository: ArtifactRepository,
    ignoredVersions: List<IgnoreVersions> = emptyList(),
    log: Log,
    val remoteRepositories: List<ArtifactRepository>
) : AbstractArtifactDetailsFactory(localRepository, log, ignoredVersions) {
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

    override fun create(groupId: String, artifactId: String, version: String): ArtifactDetails =
        ArtifactDetails(groupId, artifactId, Version(version), emptyList(), )


    override fun getAvailableVersions(
        artifact: ArtifactDetails
    ): List<Version> =
        remoteRepositories
            .filter {
                it.releases?.isEnabled ?: true
            }
            .associateWith {
                val url = getUrl(it.url, artifact.getMavenMetaDataFile())
                log.debug(url)
                HttpGet(url)
            }
            .map { (repository, request) ->
                client.execute(request, getHttpClientResponseHandler(artifact, repository))
            }.toVersions()

    private fun getHttpClientResponseHandler(
        artifact: ArtifactDetails,
        repository: ArtifactRepository
    ): HttpClientResponseHandler<MavenMetaData> =
        object : AbstractHttpClientResponseHandler<MavenMetaData>() {
            override fun handleEntity(entity: HttpEntity): MavenMetaData =
                try {
                    val content = entity.content
                    println(content)
                    content.toInstance()
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
                    log.debug("$this")
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

    fun getUrl(
        repositoryUrl: String,
        artifactPath: String
    ) =
        if (repositoryUrl.endsWith("/")) {
            "${repositoryUrl}${artifactPath}"
        } else {
            "${repositoryUrl}/${artifactPath}"
        }

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
