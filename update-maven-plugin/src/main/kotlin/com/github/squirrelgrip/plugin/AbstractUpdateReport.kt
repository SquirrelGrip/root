package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.DependencyResolver
import com.github.squirrelgrip.plugin.resolver.SessionDependencyResolver
import com.github.squirrelgrip.plugin.resolver.VersionsDependencyResolver
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugins.annotations.*
import java.util.*

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

    @Component
    lateinit var artifactMetadataSource: ArtifactMetadataSource

    @Parameter(defaultValue = "\${localRepository}", readonly = true)
    lateinit var localRepository: ArtifactRepository

    @Parameter(defaultValue = "\${project.remoteArtifactRepositories}", readonly = true)
    lateinit var remoteRepositories: List<ArtifactRepository>

    @Parameter(defaultValue = "\${project.pluginArtifactRepositories}", readonly = true)
    lateinit var pluginArtifactRepositories: List<ArtifactRepository>

    @Parameter(defaultValue = "\${session}", readonly = true)
    lateinit var session: MavenSession

    @Parameter(property = "update.useVersionsReport", name = "useVersionsReport", defaultValue = "false")
    var useVersionsReport = "false"

    @Parameter(property = "update.includeManagement", name = "includeManagement", defaultValue = "true")
    var includeManagement = "true"

    private val dependencyResolver: DependencyResolver by lazy {
        if (useVersionsReport.toBoolean())
            VersionsDependencyResolver(outputDirectory)
        else
            getMavenDependencyResolver()
    }

    abstract fun getMavenDependencyResolver(): AbstractMavenDependencyResolver

    override fun body(locale: Locale) {
        reportTable("Dependencies", dependencyResolver.getDependencyArtifacts(project))
        reportTable("Plugins", dependencyResolver.getPluginArtifacts(project))
    }

    private fun reportTable(tableHeading: String, artifacts: Collection<ArtifactDetails>) {
        sink.paragraph()
        sink.sectionTitle2()
        sink.text(tableHeading)
        sink.sectionTitle2_()
        sink.table()
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
        sink.table_()
        sink.paragraph_()
    }

}
