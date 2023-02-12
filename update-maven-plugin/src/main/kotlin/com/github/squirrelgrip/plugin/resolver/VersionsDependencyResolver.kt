package com.github.squirrelgrip.plugin.resolver

import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.plugin.model.ArtifactDetails
import com.github.squirrelgrip.plugin.model.DependencyUpdatesReport
import com.github.squirrelgrip.plugin.model.PluginUpdatesReport
import org.apache.maven.project.MavenProject
import java.io.File

class VersionsDependencyResolver(
    val outputDirectory: File,
) : DependencyResolver {
    override fun getDependencyArtifacts(
        project: MavenProject,
        processDependencies: Boolean,
        processDependencyManagement: Boolean,
        processTransitive: Boolean,
    ): Collection<ArtifactDetails> =
        File(
            outputDirectory.parentFile,
            "dependency-updates-report.xml"
        ).toInstance<DependencyUpdatesReport>().getDependencies(project)

    override fun getPluginArtifacts(
        project: MavenProject,
        processPluginDependencies: Boolean,
        processPluginDependenciesInPluginManagement: Boolean,
    ): Collection<ArtifactDetails> =
        File(
            outputDirectory.parentFile,
            "plugin-updates-report.xml"
        ).toInstance<PluginUpdatesReport>().getDependencies(project)
}
