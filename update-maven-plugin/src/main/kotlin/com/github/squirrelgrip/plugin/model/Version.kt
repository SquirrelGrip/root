package com.github.squirrelgrip.plugin.model

import org.apache.maven.project.MavenProject

data class Version(
    val value: String,
) : Comparable<Version> {
    companion object {
        val NO_VERSION = Version("")
        val VALID_CHARS = (0..9).map { it.toString()[0] }
        val VERSION_REGEX = Regex("\\D+")
        val PROPERTY_REGEX = Regex(".*\\$\\{(.+?)\\}.*")
        val PRE_RELEASE_REGEX = """(.*)-\w+(\d+)""".toRegex()

        fun versionCompare(version1: String, version2: String): Int {
            val isV1PreRelease = version1.matches(PRE_RELEASE_REGEX)
            val isV2PreRelease = version2.matches(PRE_RELEASE_REGEX)

            val v1 = if (isV1PreRelease != isV2PreRelease && isV1PreRelease) {
                PRE_RELEASE_REGEX.find(version1)!!.groupValues.get(1)
            } else {
                version1
            }
            val v2 = if (isV1PreRelease != isV2PreRelease && isV2PreRelease) {
                PRE_RELEASE_REGEX.find(version2)!!.groupValues.get(1)
            } else {
                version2
            }
            // vnum stores each numeric part of version
            var vnum1 = 0
            var vnum2 = 0

            // loop until both String are processed
            var i = 0
            var j = 0
            while (i < v1.length || j < v2.length) {
                // Storing numeric part of version 1 in vnum1
                while (i < v1.length && v1[i] in VALID_CHARS) {
                    vnum1 = (vnum1 * 10 + (v1[i] - '0'))
                    i++
                }

                // Storing numeric part of version 2 in vnum2
                while (j < v2.length && v2[j] in VALID_CHARS) {
                    vnum2 = (vnum2 * 10 + (v2[j] - '0'))
                    j++
                }
                if (vnum1 > vnum2) return 1
                if (vnum2 > vnum1) return -1

                // if equal, reset variables and go for next numeric part
                vnum1 = 0
                vnum2 = 0
                i++
                j++
            }
            return if (isV1PreRelease != isV2PreRelease) {
                if (isV1PreRelease) {
                    -1
                } else {
                    1
                }
            } else {
                0
            }
        }
    }

    val major: Int by lazy { index(0) }
    val minor: Int by lazy { index(1) }
    val increment: Int by lazy { index(2) }

    private fun index(index: Int) = try {
        value.split(VERSION_REGEX)[index].toInt()
    } catch (e: Exception) {
        0
    }

    override fun toString(): String =
        value

    fun resolve(project: MavenProject): Version =
        Version(
            value.replace(PROPERTY_REGEX) {
                val key = it.groupValues[1]
                val replacement = project.properties[key.trim()]?.toString() ?: "\${$key}"
                value.replace(PROPERTY_REGEX, replacement)
            }
        )

    override fun compareTo(other: Version): Int =
        versionCompare(value, other.value)
}
