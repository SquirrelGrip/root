package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.ProjectDependencyResolver
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.util.Locale

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
        ProjectDependencyResolver(
            repositorySystemSession.localRepository,
            remoteRepositories,
            pluginArtifactRepositories,
            log,
            ignoredVersions
        )

    override fun getOutputName(): String =
        "update-report"

    override fun getName(locale: Locale): String =
        "Update Report"

    override fun getDescription(locale: Locale): String =
        "Builds a update report"
}
