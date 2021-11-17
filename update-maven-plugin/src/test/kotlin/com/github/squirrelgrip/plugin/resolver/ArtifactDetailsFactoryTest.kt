package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout
import org.apache.maven.monitor.logging.DefaultLog
import org.apache.maven.plugin.logging.Log
import org.assertj.core.api.Assertions.assertThat
import org.codehaus.plexus.logging.Logger.LEVEL_DISABLED
import org.codehaus.plexus.logging.console.ConsoleLogger
import org.junit.jupiter.api.Test

internal class ArtifactDetailsFactoryTest {
    val artifact = ArtifactDetails("com.github.squirrelgrip", "extensions", Version("1.2.6"))
    val localRepository: ArtifactRepository = MavenArtifactRepository("local", "file:///Users/adrian/.m2/repository", DefaultRepositoryLayout(), ArtifactRepositoryPolicy(true, null, null), ArtifactRepositoryPolicy(true, null, null))
    val log: Log = DefaultLog(ConsoleLogger(LEVEL_DISABLED, "TestLoggetr"))

    @Test
    fun getRemoteAvailableVersions() {
        val repository: ArtifactRepository = MavenArtifactRepository("remote", "https://repo1.maven.org/maven2", DefaultRepositoryLayout(), ArtifactRepositoryPolicy(true, null, null), ArtifactRepositoryPolicy(true, null, null))
        val testSubject = RemoteArtifactDetailsFactory(localRepository, listOf(repository), log)
        val availableVersions = testSubject.getAvailableVersions(artifact)
        assertThat(availableVersions).isNotEmpty
    }

    @Test
    fun getLocalAvailableVersions() {
        val testSubject = LocalArtifactDetailsFactory(localRepository, log)
        val availableVersions = testSubject.getAvailableVersions(artifact)
        assertThat(availableVersions).isNotEmpty
    }
}