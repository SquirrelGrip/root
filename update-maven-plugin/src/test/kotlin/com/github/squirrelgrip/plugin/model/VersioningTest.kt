package com.github.squirrelgrip.plugin.model

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class VersioningTest {
    @Test
    fun updateTime() {
        val versioning = Versioning().updateTime()
        val lastUpdated = versioning.lastUpdated
        assertThat(lastUpdated).hasSize(14)
    }
}