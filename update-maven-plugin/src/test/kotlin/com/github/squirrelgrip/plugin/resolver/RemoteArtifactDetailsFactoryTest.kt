package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.IgnoredVersion
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.CHECKSUM_POLICY_IGNORE
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy.UPDATE_POLICY_DAILY
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.plugin.logging.Log
import org.assertj.core.api.Assertions.assertThat
import org.eclipse.aether.repository.LocalRepository
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import java.io.File

@ExtendWith(MockitoExtension::class)
internal class RemoteArtifactDetailsFactoryTest {
    @Mock
    lateinit var localRepository: LocalRepository

    @Mock
    lateinit var remoteRepository: MavenArtifactRepository

    @Mock
    lateinit var log: Log

    @Test
    fun getAvailableVersions() {
        given(localRepository.basedir).willReturn(File("${System.getProperty("user.home")}/.m2/repository"))
        given(remoteRepository.url).willReturn("https://repo1.maven.org/maven2")
        given(
            remoteRepository.releases
        ).willReturn(ArtifactRepositoryPolicy(true, UPDATE_POLICY_DAILY, CHECKSUM_POLICY_IGNORE))
        val artifact = ArtifactDetails("com.google.guava", "guava", Version("30.1-jre"))

        val testSubject =
            RemoteArtifactDetailsFactory(
                localRepository,
                listOf(IgnoredVersion("com.google.guava", "guava", ".*-android")),
                log,
                listOf(remoteRepository)
            )

        val availableVersions = testSubject.getAvailableVersions(artifact)

        assertThat(availableVersions).contains(Version("31.0.1-jre"))
        val enrichedArtifact = artifact.copy(versions = availableVersions)

        assertThat(enrichedArtifact.currentVersion.value).isEqualTo("30.1-jre")
        assertThat(enrichedArtifact.nextVersion.value).isEqualTo("30.1.1-jre")
        assertThat(enrichedArtifact.nextMajor.value).isEqualTo("31.0-jre")
        assertThat(enrichedArtifact.latest.value).isEqualTo("32.1.3-jre")
    }

    @Test
    fun getUrl() {
        val testSubject =
            RemoteArtifactDetailsFactory(
                localRepository,
                listOf(IgnoredVersion("com.google.guava", "guava", ".*-android")),
                log,
                listOf(remoteRepository)
            )
        assertThat(testSubject.getUrl("http://0.0.0.0/", "org/something/maven-metadata.xml")).isEqualTo(
            "http://0.0.0.0/org/something/maven-metadata.xml"
        )
        assertThat(testSubject.getUrl("http://0.0.0.0", "org/something/maven-metadata.xml")).isEqualTo(
            "http://0.0.0.0/org/something/maven-metadata.xml"
        )
    }
}
