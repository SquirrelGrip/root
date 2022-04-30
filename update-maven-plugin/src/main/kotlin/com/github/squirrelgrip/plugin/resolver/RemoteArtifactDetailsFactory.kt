package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.extension.xml.toXml
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.MavenMetaData
import com.github.squirrelgrip.plugin.model.Version
import com.github.squirrelgrip.plugin.model.Versioning
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log
import java.io.File
import java.security.SecureRandom
import java.security.cert.X509Certificate
import javax.net.ssl.HostnameVerifier
import javax.net.ssl.SSLContext
import javax.net.ssl.SSLSession
import javax.net.ssl.X509TrustManager

class RemoteArtifactDetailsFactory(
    val localRepository: ArtifactRepository,
    val remoteRepositories: List<ArtifactRepository>,
    val log: Log,
) : ArtifactDetailsFactory {
    companion object {
        val sslContext = SSLContext.getInstance("TLSv1.2").also {
            it.init(null, arrayOf(InsecureTrustManager()), SecureRandom())
        }

        val client: OkHttpClient = OkHttpClient()

        // val client: Client =
        //     ClientBuilder.newBuilder().sslContext(sslContext).hostnameVerifier(InsecureHostnameVerifier()).build()
    }

    override fun getAvailableVersions(artifact: ArtifactDetails): List<Version> =
        remoteRepositories
            .associateWith {
                Request.Builder().url("${it.url}/${artifact.getMavenMetaDataFile()}").build()
            }.map { (repository, request) ->
                client.newCall(request).execute().use {
                    try {
                        it.body?.string()?.toInstance() ?: throw Exception()
                    } catch (e: Exception) {
                        MavenMetaData(
                            artifact.groupId,
                            artifact.artifactId,
                            null,
                            artifact.currentVersion.value,
                            Versioning()
                        )
                    }.apply {
                        try {
                            val file =
                                File(localRepository.basedir, artifact.getMavenMetaDataFile(repository.id)).also { file ->
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

class InsecureHostnameVerifier : HostnameVerifier {
    override fun verify(hostname: String, session: SSLSession): Boolean = true
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
