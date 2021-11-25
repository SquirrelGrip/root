package com.github.squirrelgrip.plugin

import org.apache.maven.artifact.repository.ArtifactRepository
import org.apache.maven.execution.MavenSession
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.Parameter
import org.apache.maven.plugins.annotations.ResolutionScope
import java.util.*

@Mojo(
    name = "check",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true,
)
class ProjectCheckerReportMojo : AbstractDoxiaReport() {
    override val reportHeading = "Project Checker Report"

    @Parameter(defaultValue = "\${project.remoteArtifactRepositories}", readonly = true)
    lateinit var remoteRepositories: List<ArtifactRepository>

    @Parameter(defaultValue = "\${project.pluginArtifactRepositories}", readonly = true)
    lateinit var pluginArtifactRepositories: List<ArtifactRepository>

    @Parameter(defaultValue = "\${session}", readonly = true)
    lateinit var session: MavenSession

    override fun logParameters() {}

    override fun body(locale: Locale) {
        /// This is the content
        //More information
        //CIManagement not defined
        //License not defined
        //Issue Management not defined
        //Mailing List not defined
        //Name not defined
        //Organisation not defined
        //Packaging not defined
        //
        //
        //Properties not used
        //Dependencies without DependencyManagement
        //Unused dependencies
        //Unused dependency managements
        //Unused plugin managements
        //Duplicate repositories
        duplicateRepositoriesSection()

    }

    private fun duplicateRepositoriesSection() {
        val duplicateRepositories = getDuplicateRepositories()
        sink.sectionTitle2()
        sink.text("Duplicate Repositories")
        sink.sectionTitle2_()
        if (duplicateRepositories.isNotEmpty()) {
            sink.list()
            duplicateRepositories.forEach {
                sink.listItem()
                sink.text("${it.id} ${it.url}")
                sink.listItem_()
            }
            sink.list_()
        } else {
            sink.text("No Duplicate Repositories")
        }
    }

    private fun getDuplicateRepositories(): List<ArtifactRepository> {
        val duplicateRepositories = remoteRepositories.groupBy {
            it.url
        }.filter {
            it.value.size > 1
        }.flatMap {
            it.value
        }
        return duplicateRepositories
    }

    override fun getOutputName(): String {
        return "project-checker-report"
    }

    override fun getName(locale: Locale): String {
        return "Project Checker Report"
    }

    override fun getDescription(locale: Locale): String {
        return "Checks the project for common problems"
    }
}
