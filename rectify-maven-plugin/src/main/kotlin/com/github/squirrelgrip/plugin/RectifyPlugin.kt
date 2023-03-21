package com.github.squirrelgrip.plugin

import org.apache.maven.execution.MavenSession
import org.apache.maven.plugin.AbstractMojo
import org.apache.maven.plugin.BuildPluginManager
import org.apache.maven.plugins.annotations.Component
import org.apache.maven.plugins.annotations.LifecyclePhase
import org.apache.maven.plugins.annotations.Mojo
import org.apache.maven.plugins.annotations.ResolutionScope
import org.apache.maven.project.MavenProject
import org.twdata.maven.mojoexecutor.MojoExecutor.*

@Mojo(
    name = "rectify",
    defaultPhase = LifecyclePhase.NONE,
    requiresDependencyResolution = ResolutionScope.RUNTIME,
    requiresProject = true,
    threadSafe = true,
    aggregator = true
)

class RectifyPlugin : AbstractMojo() {
    @Component
    private lateinit var mavenProject: MavenProject

    @Component
    private lateinit var mavenSession: MavenSession

    @Component
    private lateinit var pluginManager: BuildPluginManager

    override fun execute() {
        try {
            executeMojo(
                plugin(
                    groupId("org.apache.maven.plugins"),
                    artifactId("maven-dependency-plugin")
                ),
                goal("get"),
                configuration(
                    element(
                        name("artifact"),
                        "${mavenProject.groupId}:${mavenProject.artifactId}:${mavenProject.version}:${mavenProject.packaging}"
                    )
                ),
                executionEnvironment(
                    mavenProject,
                    mavenSession,
                    pluginManager
                )
            )
            println("FOUND")
        } catch (e: Exception) {
            println("NOT FOUND")
        }
    }
 }
