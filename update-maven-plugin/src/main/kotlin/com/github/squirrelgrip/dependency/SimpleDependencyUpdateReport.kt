package com.github.squirrelgrip.dependency

import com.fasterxml.jackson.databind.module.SimpleModule
import com.github.squirrelgrip.dependency.model.Artifact
import com.github.squirrelgrip.dependency.model.DependencyUpdatesReport
import com.github.squirrelgrip.dependency.model.Version
import com.github.squirrelgrip.dependency.serial.VersionDeserializer
import com.github.squirrelgrip.extension.xml.Xml
import com.github.squirrelgrip.extension.xml.toInstance
import org.apache.maven.doxia.sink.Sink
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
class DependencyUpdateReport : AbstractMavenReport() {
    @Throws(MavenReportException::class)
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
        sink.header("Update Report")
        sink.paragraph()
        sink.table()

        sink.tableHeaderRow(
            "Group Id",
            "Artifact Id",
            "Current Version",
            "Next Version",
            "Latest Incremental",
            "Latest Minor",
            "Next Major",
            "Latest"
        )

        getArtifacts().toTableRow(sink)

        sink.table_()
        sink.paragraph_()
        sink.foot()
    }

    private fun getArtifacts(): List<Artifact> {
        return File(
            outputDirectory.parentFile,
            "dependency-updates-report.xml"
        ).toInstance<DependencyUpdatesReport>().getDependencies()
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

    private fun List<Artifact>.toTableRow(sink: Sink) {
        this.forEach {
            sink.tableRow(*it.values(project.properties).toTypedArray())
        }
    }


}



