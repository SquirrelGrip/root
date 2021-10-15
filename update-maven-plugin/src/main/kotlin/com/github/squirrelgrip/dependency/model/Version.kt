package com.github.squirrelgrip.dependency.model

import org.apache.maven.project.MavenProject

data class Version(
    val value: String
) : Comparable<Version> {
    companion object {
        val NO_VERSION = Version("")
        val VALID_CHARS = (0..9).map { it.toString()[0] }
        val VERSION_REGEX = Regex("\\D+")
        val PROPERTY_REGEX = Regex(".*\\$\\{(.+?)\\}.*")

        fun versionCompare(v1: String, v2: String): Int {
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
            return 0
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

    fun isValid(): Boolean =
        !value.uppercase().contains("LPHA") &&
        !value.contains("ndroid") &&
        !value.uppercase().contains("B") &&
        !value.uppercase().contains("ETA") &&
        !value.contains("enkin") &&
        !value.uppercase().contains("M") &&
        !value.contains("ative") &&
        !value.uppercase().contains("RC") &&
        !value.contains("r") &&
        !value.contains("SNAPSHOT")

    fun resolve(project: MavenProject): Version =
        Version(value.replace(PROPERTY_REGEX) {
            val key = it.groupValues[1]
            val replacement = project.properties[key.trim()]?.toString() ?: "\${$key}"
            value.replace(PROPERTY_REGEX, replacement)
        })

    override fun compareTo(other: Version): Int =
        versionCompare(value, other.value)

}
