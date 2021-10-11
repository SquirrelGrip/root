package com.github.squirrelgrip.dependency

import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.squirrelgrip.dependency.model.*
import com.github.squirrelgrip.dependency.serial.VersionDeserializer
import com.github.squirrelgrip.extension.xml.Xml
import com.github.squirrelgrip.extension.xml.toInstance
import org.apache.maven.artifact.DefaultArtifact
import org.apache.maven.artifact.handler.DefaultArtifactHandler
import org.apache.maven.artifact.metadata.ArtifactMetadataSource
import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.model.Dependency
import org.apache.maven.model.Plugin
import org.apache.maven.plugins.annotations.*
import org.apache.maven.reporting.AbstractMavenReport
import org.apache.maven.reporting.MavenReportException
import java.io.File
import java.util.*

@Mojo(
    name = "report",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true
)
class UpdateReportMojo : AbstractMavenReport() {
    companion object {
        val headings = listOf(
            "Group Id",
            "Artifact Id",
            "Current Version",
            "Next Version",
            "Latest Incremental",
            "Next Minor",
            "Latest Minor",
            "Next Major",
            "Latest"
        )
    }

    val useVersionsReport = false

    @Component
    lateinit var artifactMetadataSource: ArtifactMetadataSource

    @Parameter(defaultValue = "\${localRepository}", readonly = true)
    lateinit var localRepository: ArtifactRepository

    @Parameter(defaultValue = "\${project.remoteArtifactRepositories}", readonly = true)
    lateinit var remoteRepositories: List<ArtifactRepository>

    @Parameter(defaultValue = "\${project.pluginArtifactRepositories}", readonly = true)
    lateinit var pluginArtifactRepositories: List<ArtifactRepository>

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
        val reportHeading = "Update Report"

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
        reportTable("Dependencies", getDependencyArtifacts())
        reportTable("Plugins", getPluginArtifacts())
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

    fun getDependencyArtifacts(): Collection<ArtifactDetails> =
        if (useVersionsReport) {
            File(
                outputDirectory.parentFile,
                "dependency-updates-report.xml"
            ).toInstance<DependencyUpdatesReport>().getDependencies(getProperties())
        } else {
            ((project.dependencies) + (project.dependencyManagement?.dependencies ?: emptyList())).map {
                it.dependency()
            }
        }

    fun getPluginArtifacts(): Collection<ArtifactDetails> =
        if (useVersionsReport) {
            File(
                outputDirectory.parentFile,
                "plugin-updates-report.xml"
            ).toInstance<PluginUpdatesReport>().getDependencies(getProperties())
        } else {
            ((project.buildPlugins) + (project.pluginManagement?.plugins ?: emptyList())).map {
                it.dependency()
            }
        }

    private fun Dependency.dependency() = getArtifactDetails(groupId, artifactId, version, false)
    private fun Plugin.dependency() = getArtifactDetails(groupId, artifactId, version, true)

    fun getArtifactDetails(
        groupId: String,
        artifactId: String,
        version: String,
        usePluginRepositories: Boolean
    ): ArtifactDetails {
        val artifact = DefaultArtifact(groupId, artifactId, version, "", "", "", DefaultArtifactHandler())
        val remoteRepositories = if (usePluginRepositories) pluginArtifactRepositories else remoteRepositories
        val versions =
            artifactMetadataSource.retrieveAvailableVersions(artifact, localRepository, remoteRepositories).map {
                Version(it.toString())
            }
        return ArtifactDetails(groupId, artifactId, Version(version), versions)
    }

    private fun getProperties(): Map<String, String> =
        project.properties.map { it.key.toString() to it.value.toString() }.toMap()


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




