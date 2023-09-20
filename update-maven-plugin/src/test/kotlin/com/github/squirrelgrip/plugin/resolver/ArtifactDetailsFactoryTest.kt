package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.Version
import org.apache.maven.artifact.repository.ArtifactRepositoryPolicy
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.artifact.repository.layout.DefaultRepositoryLayout
import org.apache.maven.monitor.logging.DefaultLog
import org.apache.maven.plugin.logging.Log
import org.assertj.core.api.Assertions.assertThat
import org.codehaus.plexus.logging.Logger.LEVEL_DISABLED
import org.codehaus.plexus.logging.console.ConsoleLogger
import org.eclipse.aether.repository.LocalRepository
import org.junit.jupiter.api.Test
import java.io.File

internal class ArtifactDetailsFactoryTest {
    val artifact = ArtifactDetails("com.github.squirrelgrip", "extensions", Version("1.2.6"))
    val localRepository: LocalRepository = LocalRepository(File("${System.getProperty("user.home")}/.m2/repository"))
    val log: Log = DefaultLog(ConsoleLogger(LEVEL_DISABLED, "TestLoggetr"))

    @Test
    fun getRemoteAvailableVersions() {
        val repository = MavenArtifactRepository("remote", "https://repo1.maven.org/maven2", DefaultRepositoryLayout(), ArtifactRepositoryPolicy(true, null, null), ArtifactRepositoryPolicy(true, null, null))
        val testSubject = RemoteArtifactDetailsFactory(
            localRepository,
            emptyList(),
            log,
            listOf(repository)
        )
        val availableVersions = testSubject.getAvailableVersions(artifact)
        assertThat(availableVersions).isNotEmpty
    }

    @Test
    fun getLocalAvailableVersions() {
        val testSubject = LocalArtifactDetailsFactory(
            localRepository,
            emptyList(),
            log,
        )
        val availableVersions = testSubject.getAvailableVersions(artifact)
        assertThat(availableVersions).isNotEmpty
    }
}
