package com.github.squirrelgrip.plugin.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionTest {

    @Test
    fun compare() {
        assertThat("0", "0.1")
        assertThat("0", "1")
        assertThat("1", "10")
        assertThat("2", "10")
        assertThat("10", "11")
        assertThat("4.0", "4.4")
        assertThat("2.31", "2.33")
        assertThat("2.31", "3.0.0")
        assertThat("2.31.0", "3.0.0")
        assertThat("5.8.0-RC1", "5.8.1")
        assertThat("30.0-jre", "31.0.1-jre")
        assertThat("30.1-jre", "31.0-jre")
        assertThat("30.1-jre", "31.0.1-jre")
        assertThat("30.1-jre", "30.1.1-jre")
        assertThat("30.1-jre", "30.1.0-jre")
        assertThat("1.6.20-M1", "1.6.20-M2")
        assertThat("1.6.20-M1", "1.6.20")
        assertThat("1.6.20-ALPHA1", "1.6.20-BETA1")
        assertThat("1.6.20-ALPHA2", "1.6.20-BETA1")
    }

    @Test
    fun sort() {
        val current = Version("2.30")
        val version = listOf(
            Version("2.31"),
            Version("2.35"),
            Version("2.26"),
            Version("2.32"),
            Version("2.33"),
            Version("3.0.0-M1"),
            Version("3.0.0-RC2"),
            Version("2.34"),
            Version("3.0.0"),
            Version("3.0.0-M6"),
        ).sorted().first {
            it > current
        }

        assertThat(version).isEqualTo(Version("2.31"))
    }

    @Test
    fun parts() {
        partsEquals("0", 0, 0, 0)
        partsEquals("0", 0, 0, 0)
        partsEquals("1", 1, 0, 0)
        partsEquals("2", 2, 0, 0)
        partsEquals("10", 10, 0, 0)
        partsEquals("4.0", 4, 0, 0)
        partsEquals("2.31", 2, 31, 0)
        partsEquals("2.31", 2, 31, 0)
        partsEquals("2.31.0", 2, 31, 0)
        partsEquals("2.31.1", 2, 31, 1)
        partsEquals("5.8.0-RC1", 5, 8, 0)
        partsEquals("5.8.1-RC1", 5, 8, 1)
        partsEquals("31.0.1-jre", 31, 0, 1)
        partsEquals("31.1-jre", 31, 1, 0)
    }

    fun partsEquals(version: String, major: Int, minor: Int, increment: Int) {
        assertThat(Version(version).major).isEqualTo(major)
        assertThat(Version(version).minor).isEqualTo(minor)
        assertThat(Version(version).increment).isEqualTo(increment)
    }

    fun assertThat(lower: String, upper: String) {
        assertThat(Version(lower)).isLessThanOrEqualTo(Version(upper))
        assertThat(Version(upper)).isGreaterThanOrEqualTo(Version(lower))
    }
}
