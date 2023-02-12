package com.github.squirrelgrip.plugin.model

import org.apache.maven.plugins.annotations.Parameter

class IgnoredVersion() {
    @Parameter(property = "groupId", required = true)
    lateinit var groupId: String
    @Parameter(property = "artifactId", required = true)
    lateinit var artifactId: String
    @Parameter(property = "version", required = true)
    lateinit var version: String

    constructor(
        groupId: String,
        artifactId: String,
        version: String
    ) : this() {
        this.groupId = groupId
        this.artifactId = artifactId
        this.version = version
    }
}