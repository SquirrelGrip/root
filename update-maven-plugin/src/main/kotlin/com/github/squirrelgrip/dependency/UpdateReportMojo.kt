package com.github.squirrelgrip.dependency

import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.squirrelgrip.dependency.model.*
import com.github.squirrelgrip.dependency.serial.VersionDeserializer
import com.github.squirrelgrip.extension.xml.Xml
import com.github.squirrelgrip.extension.xml.toInstance
import org.apache.maven.artifact.Artifact
import org.apache.maven.model.Dependency
import org.apache.maven.model.Repository
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
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
            "Latest Minor",
            "Next Major",
            "Latest"
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
        File(
            outputDirectory.parentFile,
            "dependency-updates-report.xml"
        ).toInstance<DependencyUpdatesReport>().getDependencies(getProperties())

    fun getPluginArtifacts(): Collection<ArtifactDetails> =
        File(
            outputDirectory.parentFile,
            "plugin-updates-report.xml"
        ).toInstance<PluginUpdatesReport>().getDependencies(getProperties())

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




