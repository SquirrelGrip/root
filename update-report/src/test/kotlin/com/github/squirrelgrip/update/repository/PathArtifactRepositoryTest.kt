package com.github.squirrelgrip.update.repository

import org.apache.maven.repository.internal.MavenRepositorySystemUtils
import org.eclipse.aether.RepositorySystem
import org.eclipse.aether.RepositorySystemSession
import org.eclipse.aether.artifact.DefaultArtifact
import org.eclipse.aether.collection.CollectRequest
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory
import org.eclipse.aether.graph.Dependency
import org.eclipse.aether.repository.LocalRepository
import org.eclipse.aether.repository.RemoteRepository
import org.eclipse.aether.resolution.DependencyRequest
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory
import org.eclipse.aether.spi.connector.transport.TransporterFactory
import org.eclipse.aether.transport.file.FileTransporterFactory
import org.eclipse.aether.transport.http.HttpTransporterFactory
import org.eclipse.aether.util.graph.visitor.PreorderNodeListGenerator
import org.junit.jupiter.api.Test

internal class PathArtifactRepositoryTest {

    companion object {
        fun newRepositorySystem(): RepositorySystem =
            MavenRepositorySystemUtils.newServiceLocator().apply {
                addService(RepositoryConnectorFactory::class.java, BasicRepositoryConnectorFactory::class.java)
                addService(TransporterFactory::class.java, FileTransporterFactory::class.java)
                addService(TransporterFactory::class.java, HttpTransporterFactory::class.java)
            }.let {
                it.getService(RepositorySystem::class.java)
            }

        fun newSession(system: RepositorySystem): RepositorySystemSession =
            MavenRepositorySystemUtils.newSession().apply {
                val localRepo = LocalRepository("target/local-repo")
                setLocalRepositoryManager(system.newLocalRepositoryManager(this, localRepo))
            }
    }

    @Test
    fun find() {
        val repoSystem = newRepositorySystem()

        val session = newSession(repoSystem)

        val dependency = Dependency(DefaultArtifact("com.github.squirrelgrip:update-maven-plugin:1.2.60"), "compile")
        val central = RemoteRepository.Builder("central", "default", "https://repo1.maven.org/maven2/").build()

        val collectRequest = CollectRequest()
        collectRequest.setRoot(dependency)
        collectRequest.addRepository(central)
        val node = repoSystem.collectDependencies(session, collectRequest).root

        val dependencyRequest = DependencyRequest()
        dependencyRequest.setRoot(node)

        repoSystem.resolveDependencies(session, dependencyRequest)

        val nodeListGenerator = PreorderNodeListGenerator()
        node.accept(nodeListGenerator)
        println("ARTIFACTS")
        nodeListGenerator.getArtifacts(true).sortedBy {
                it.toString()
            }.forEach {
                println(it)
            }
        println("DEPENDENCIES")
        nodeListGenerator.getDependencies(true).sortedBy {
                it.toString()
            }.forEach {
                println(it)
            }
    }
}