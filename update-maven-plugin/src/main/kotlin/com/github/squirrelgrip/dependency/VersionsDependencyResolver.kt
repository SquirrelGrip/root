package com.github.squirrelgrip.dependency

import com.github.squirrelgrip.dependency.model.ArtifactDetails
import com.github.squirrelgrip.dependency.model.PluginUpdatesReport
import com.github.squirrelgrip.extension.xml.toInstance
import org.apache.maven.project.MavenProject
import java.io.File

class VersionsDependencyResolver(
    val outputDirectory: File
):DependencyResolver {
    override fun getDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        File(
            outputDirectory.parentFile,
            "plugin-updates-report.xml"
        ).toInstance<PluginUpdatesReport>().getDependencies(project.properties())

    override fun getPluginArtifacts(project: MavenProject): Collection<ArtifactDetails> =
        File(
            outputDirectory.parentFile,
            "plugin-updates-report.xml"
        ).toInstance<PluginUpdatesReport>().getDependencies(project.properties())

    private fun MavenProject.properties(): Map<String, String> =
        this.properties.map { it.key.toString() to it.value.toString() }.toMap()

}