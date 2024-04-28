package com.github.squirrelgrip.util

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.properties.ReadWriteProperty

class InitOncePropertyTest {

    var property: String by initOnce()

    @Test
    fun readValueFailure() {
        assertThrows(IllegalStateException::class.java) { property }
    }

    @Test
    fun writeValueTwice() {
        property = "Test1"
        assertThrows(IllegalStateException::class.java) { property = "Test2" }
    }

    @Test
    fun readWriteCorrect() {
        property = "Test"
        val data1 = property
        val data2 = property
        assertThat(data1).isSameAs(data2)
    }
}
