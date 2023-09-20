package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.IgnoredVersion
import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.DependencyResolver
import com.github.squirrelgrip.plugin.resolver.VersionsDependencyResolver
import org.apache.maven.artifact.repository.MavenArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugins.annotations.Parameter
import org.eclipse.aether.RepositorySystemSession
import java.util.Locale

abstract class AbstractUpdateReport : AbstractDoxiaReport() {
    companion object {
        val headings = listOf(
            "GroupId",
            "ArtifactId",
            "Version",
            "Next Version",
            "Latest Incremental",
            "Next Minor",
            "Latest Minor",
            "Next Major",
            "Latest"
        )
    }

    @Parameter(defaultValue = "\${repositorySystemSession}", readonly = true)
    lateinit var repositorySystemSession: RepositorySystemSession

    @Parameter(defaultValue = "\${project.remoteArtifactRepositories}", readonly = true)
    lateinit var remoteRepositories: List<MavenArtifactRepository>

    @Parameter(defaultValue = "\${project.pluginArtifactRepositories}", readonly = true)
    lateinit var pluginArtifactRepositories: List<MavenArtifactRepository>

    @Parameter(defaultValue = "\${session}", readonly = true)
    lateinit var session: MavenSession

    @Parameter(property = "ignoredVersions")
    var ignoredVersions: List<IgnoredVersion> = emptyList()

    @Parameter(property = "processDependencies", defaultValue = "true")
    private var processDependencies = true

    @Parameter(property = "processDependencyManagement", defaultValue = "true")
    private var processDependencyManagement = true

    @Parameter(property = "processPluginDependencies", defaultValue = "true")
    private var processPluginDependencies = true

    @Parameter(property = "processPluginDependenciesInPluginManagement", defaultValue = "true")
    private var processPluginDependenciesInPluginManagement = true

    @Parameter(property = "processTransitive", defaultValue = "true")
    private var processTransitive = true

    @Parameter(property = "processParent", defaultValue = "false")
    private var processParent = false

    @Parameter(property = "useVersionsReport", defaultValue = "false")
    var useVersionsReport = false

    private val dependencyResolver: DependencyResolver by lazy {
        if (useVersionsReport)
            VersionsDependencyResolver(outputDirectory)
        else
            getMavenDependencyResolver()
    }

    override fun logParameters() {
        log.debug("processDependencies: $processDependencies")
        log.debug("processDependencyManagement: $processDependencyManagement")
        log.debug("processPluginDependencies: $processPluginDependencies")
        log.debug("processPluginDependenciesInPluginManagement: $processPluginDependenciesInPluginManagement")
        log.debug("processTransitive: $processTransitive")
        log.debug("ignoredVersions: $ignoredVersions")
    }

    abstract fun getMavenDependencyResolver(): AbstractMavenDependencyResolver

    override fun body(locale: Locale) {
        reportTable(
            "Dependencies",
            dependencyResolver.getDependencyArtifacts(
                project,
                processDependencies,
                processDependencyManagement,
                processTransitive
            )
        )
        reportTable(
            "Plugins",
            dependencyResolver.getPluginArtifacts(
                project,
                processPluginDependencies,
                processPluginDependenciesInPluginManagement
            )
        )
    }

    private fun reportTable(tableHeading: String, artifacts: Collection<ArtifactDetails>) {
        sink.sectionTitle2()
        sink.text(tableHeading)
        sink.sectionTitle2_()
        sink.table()
        sink.tableRows()
        sink.tableRow()
        headings.forEach {
            sink.tableHeaderCell()
            sink.text(it)
            sink.tableHeaderCell_()
        }
        sink.tableRow_()
        artifacts.forEach { artifact ->
            sink.tableRow()
            artifact.values.forEach {
                sink.tableCell()
                sink.text(it)
                sink.tableCell_()
            }
            sink.tableRow_()
        }
        sink.tableRows_()
        sink.table_()
    }
}
