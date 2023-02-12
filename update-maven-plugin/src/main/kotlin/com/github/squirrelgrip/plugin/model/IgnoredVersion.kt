package com.github.squirrelgrip.plugin.model

import org.apache.maven.plugins.annotations.Parameter

class IgnoredVersion() {
    @Parameter(property = "groupId")
    var groupId: String? = null
    @Parameter(property = "groupIdRegEx")
    var groupIdRegEx: String? = null
    @Parameter(property = "artifactId")
    var artifactId: String? = null
    @Parameter(property = "artifactIdRegEx")
    var artifactIdRegEx: String? = null
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