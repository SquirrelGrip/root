package com.github.squirrelgrip.dependency

import com.github.squirrelgrip.dependency.model.ArtifactDetails
import com.github.squirrelgrip.dependency.model.DependencyUpdatesReport
import com.github.squirrelgrip.dependency.model.PluginUpdatesReport
import com.github.squirrelgrip.extension.xml.toInstance
import org.apache.maven.project.MavenProject
import java.io.File

interface DependencyResolver {
    fun getDependencyArtifacts(project: MavenProject): Collection<ArtifactDetails>
    fun getPluginArtifacts(project: MavenProject): Collection<ArtifactDetails>
}