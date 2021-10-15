package com.github.squirrelgrip.dependency

import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.squirrelgrip.dependency.model.ArtifactDetails
import com.github.squirrelgrip.dependency.model.Version
import com.github.squirrelgrip.dependency.serial.VersionDeserializer
import com.github.squirrelgrip.extension.xml.Xml
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugins.annotations.*
import org.apache.maven.reporting.AbstractMavenReport
import org.apache.maven.reporting.MavenReportException
import java.util.*

@Mojo(
    name = "report",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true,
    aggregator = true
)
class UpdateReportMojo : AbstractMavenReport() {
    companion object {
        val reportHeading = "Update Report"

        val headings = listOf(
            "GroupId",
            "ArtifactId",
            "Version",
            "Projects",
            "Type",
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
    var useVersionsReport = false

    val dependencyResolver: DependencyResolver by lazy {
        if (useVersionsReport)
            VersionsDependencyResolver(outputDirectory)
        else
            MavenDependencyResolver(
                artifactMetadataSource,
                localRepository,
                remoteRepositories,
                pluginArtifactRepositories,
                session
            )
    }

    override fun executeReport(locale: Locale) {
        Xml.xmlMapper.registerModule(SimpleModule().apply {
            addDeserializer(
                Version::class.java,
                VersionDeserializer()
            )
        })

        if (sink == null) {
            throw MavenReportException("Could not get the Doxia sink")
        }

        sink.head()
        sink.title()
        sink.text(reportHeading)
        sink.title_()
        sink.head_()
        sink.body()
        sink.section1()
        sink.sectionTitle1()
        sink.text(reportHeading)
        sink.sectionTitle1_()
        reportTable("Dependencies", dependencyResolver.getDependencyArtifacts(project))
        reportTable("Plugins", dependencyResolver.getPluginArtifacts(project))
        sink.section1_()
        sink.body_()
        sink.footer()
        sink.footer_()
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


    override fun getOutputName(): String {
        return "update-report"
    }

    override fun getName(locale: Locale): String {
        return "Update Report"
    }

    override fun getDescription(locale: Locale): String {
        return "Builds a update report"
    }
}
