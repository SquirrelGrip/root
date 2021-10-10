package com.github.squirrelgrip.dependency.model

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import java.util.*

data class Artifact(
    @JsonProperty("groupId")
    val groupId: String,
    @JsonProperty("artifactId")
    val artifactId: String,
    @JsonProperty("scope")
    val scope: String,
    @JsonProperty("classifier")
    val classifier: String,
    @JsonProperty("type")
    val type: String,
    @JsonProperty("currentVersion")
    val currentVersion: Version,
    @JsonProperty("nextVersion")
    val nextVersion: Version = Version.NO_VERSION,
    @JsonProperty("status")
    val status: String,
    @JacksonXmlElementWrapper(useWrapping = true, localName = "incrementals")
    @JsonProperty("incrementals")
    val incrementals: List<Version>? = emptyList(),
    @JacksonXmlElementWrapper(useWrapping = true, localName = "minors")
    @JsonProperty("minors")
    val minors: List<Version>? = emptyList(),
    @JacksonXmlElementWrapper(useWrapping = true, localName = "majors")
    @JsonProperty("majors")
    val majors: List<Version>? = emptyList(),
) : Comparable<Artifact> {
    companion object {
        val regex = Regex(".*\\$\\{(.*)\\}.*")
    }

    fun values(properties: Properties): List<String> {
        val version = currentVersion.resolve(properties)
        return listOf(
            groupId,
            artifactId,
            version.toString(),
            getNewerNextVersion(version).toString(),
            getLatestIncremental(version).toString(),
            getLatestMinor(version).toString(),
            getEarliestMajor(version).toString(),
            getLatestMajor(version).toString()
        )
    }

    fun getNewerNextVersion(version: Version): Version {
        val allVersions = getAllVersions()
        return allVersions.sorted().firstOrNull {
            it.isValid() && it > version
        } ?: Version.NO_VERSION
    }

    fun getAllVersions(): List<Version> =
        getIncrementalVersions() + getMinorVersions() + getMajorVersions()

    fun getIncrementalVersions() =
        incrementals?.sorted() ?: emptyList()
    fun getMinorVersions() =
        minors?.sorted() ?: emptyList()
   fun getMajorVersions() =
       majors?.sorted() ?: emptyList()

    fun getEarliestIncremental(): Version =
        getIncrementalVersions().firstOrNull {
            it.isValid() && it > currentVersion
        } ?: Version.NO_VERSION

    fun getLatestIncremental(version: Version): Version =
        getAllVersions().lastOrNull {
            it.isValid() && it > version && it.major == version.major && it.minor == version.minor && it.increment > version.increment
        } ?: Version.NO_VERSION

    fun getLatestMinor(version: Version): Version =
        getAllVersions().lastOrNull {
            it.isValid() && it > version && it.major == version.major && it.minor > version.minor
        } ?: Version.NO_VERSION

    fun getEarliestMajor(version: Version): Version =
        getAllVersions().firstOrNull {
            it.isValid() && it > version && it.major > version.major
        } ?: Version.NO_VERSION

    fun getLatestMajor(version: Version): Version =
        getAllVersions().lastOrNull {
            it.isValid() && it > version
        } ?: Version.NO_VERSION

    override fun compareTo(other: Artifact): Int {
        val primaryComparison = artifactId.compareTo(other.artifactId)
        return if (primaryComparison == 0) {
            val secondaryComparison = groupId.compareTo(other.groupId)
            secondaryComparison
        } else {
            primaryComparison
        }
    }

}