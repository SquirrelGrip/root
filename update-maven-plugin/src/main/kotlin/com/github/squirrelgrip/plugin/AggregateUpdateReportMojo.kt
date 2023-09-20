package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.SessionDependencyResolver
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.util.Locale

@Mojo(
    name = "aggregate",
    defaultPhase = LifecyclePhase.SITE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true,
    aggregator = true
)
class AggregateUpdateReportMojo : AbstractUpdateReport() {
    override fun getMavenDependencyResolver(): AbstractMavenDependencyResolver =
        SessionDependencyResolver(
            repositorySystemSession.localRepository,
            remoteRepositories,
            pluginArtifactRepositories,
            session,
            log,
            ignoredVersions
        )

    override val reportHeading = "Aggregated Update Report"

    override fun getOutputName(): String =
        "update-aggregate-report"

    override fun getName(locale: Locale): String =
        "Aggregated Update Report"

    override fun getDescription(locale: Locale): String =
        "Builds an Aggregated Update Report for all modules in the project"
}
