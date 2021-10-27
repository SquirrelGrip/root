package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.ProjectDependencyResolver
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.util.*

@Mojo(
    name = "report",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true
)
class ProjectUpdateReportMojo : AbstractUpdateReport() {
    override val reportHeading = "Project Update Report"

    override fun getMavenDependencyResolver(): AbstractMavenDependencyResolver =
        ProjectDependencyResolver(artifactMetadataSource, localRepository, remoteRepositories, pluginArtifactRepositories)

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
