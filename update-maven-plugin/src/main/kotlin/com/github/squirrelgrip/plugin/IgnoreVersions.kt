package com.github.squirrelgrip.plugin

data class IgnoreVersions(
    val groupId: String,
    val artifactId: String,
    val expression: String
) {
    val groupIdRegEx: Regex = groupId.toRegex()
    val artifactIdRegEx: Regex = artifactId.toRegex()
}