package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.plugin.logging.Log
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.BDDMockito.given
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension

@ExtendWith(MockitoExtension::class)
internal class RemoteArtifactDetailsFactoryTest {
    @Mock
    lateinit var localRepository: ArtifactRepository
    @Mock
    lateinit var remoteRepository: ArtifactRepository
    @Mock
    lateinit var log: Log

    @Test
    fun getAvailableVersions() {
        given(localRepository.basedir).willReturn("${System.getProperty("user.home")}/.m2/repository")
        given(remoteRepository.url).willReturn("https://repo1.maven.org/maven2")
        val artifact = ArtifactDetails("com.google.guava", "guava", Version("30.1-jre"))

        val testSubject = RemoteArtifactDetailsFactory(localRepository, listOf(remoteRepository), log)

        val availableVersions = testSubject.getAvailableVersions(artifact)

        assertThat(availableVersions).contains(Version("31.0.1-jre"))
        val enrichedArtifact = artifact.copy(versions = availableVersions)

        assertThat(enrichedArtifact.currentVersion.value).isEqualTo("30.1-jre")
        assertThat(enrichedArtifact.nextVersion.value).isEqualTo("30.1.1-jre")
        assertThat(enrichedArtifact.nextMajor.value).isEqualTo("31.0-jre")
        assertThat(enrichedArtifact.latest.value).isEqualTo("31.1-jre")
    }
}