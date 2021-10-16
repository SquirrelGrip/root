package com.github.squirrelgrip.plugin

import com.github.squirrelgrip.plugin.resolver.AbstractMavenDependencyResolver
import com.github.squirrelgrip.plugin.resolver.SessionDependencyResolver
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import java.util.*

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
        SessionDependencyResolver(artifactMetadataSource, localRepository, remoteRepositories, pluginArtifactRepositories, session, includeManagement.toBoolean())

    override val reportHeading = "Aggregated Update Report"

    override fun getOutputName(): String {
        return "update-aggregate-report"
    }

    override fun getName(locale: Locale): String {
        return "Aggregated Update Report"
    }

    override fun getDescription(locale: Locale): String {
        return "Builds a Aggregated Update Report for All Projects"
    }
}
