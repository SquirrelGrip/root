package com.github.squirrelgrip.extension.regex

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RegexExtensionTest {
    @Test
    fun replace() {
        assertThat("".replace(emptyMap())).isEqualTo("")
        assertThat("A".replace(emptyMap())).isEqualTo("A")
        assertThat("B".replace(emptyMap())).isEqualTo("B")
        assertThat("AB".replace(emptyMap())).isEqualTo("AB")
        assertThat("".replace(mapOf("A" to "1"))).isEqualTo("")
        assertThat("A".replace(mapOf("A" to "1"))).isEqualTo("1")
        assertThat("B".replace(mapOf("A" to "1"))).isEqualTo("B")
        assertThat("AB".replace(mapOf("A" to "1"))).isEqualTo("1B")
        assertThat("".replace(mapOf("B" to "2"))).isEqualTo("")
        assertThat("A".replace(mapOf("B" to "2"))).isEqualTo("A")
        assertThat("B".replace(mapOf("B" to "2"))).isEqualTo("2")
        assertThat("AB".replace(mapOf("B" to "2"))).isEqualTo("A2")
        assertThat("".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("")
        assertThat("A".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("1")
        assertThat("B".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("2")
        assertThat("AB".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("12")

        assertThat("".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("")
        assertThat("AA".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("11")
        assertThat("BB".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("22")
        assertThat("ABAB".replace(mapOf("A" to "1", "B" to "2"))).isEqualTo("1212")

        assertThat("CLOSE".replace(mapOf("CLOSE" to "1", "OPEN" to "2"))).isEqualTo("1")
        assertThat("OPEN".replace(mapOf("CLOSE" to "1", "OPEN" to "2"))).isEqualTo("2")
        assertThat("OPENCLOSE".replace(mapOf("CLOSE" to "1", "OPEN" to "2"))).isEqualTo("21")
        assertThat("CLOSEOPEN".replace(mapOf("CLOSE" to "1", "OPEN" to "2"))).isEqualTo("12")

        assertThat("PENDING_CLOSE".replace(sortedMapOf("PENDING_CLOSE" to "3", "CLOSE" to "1", "OPEN" to "2"))).isEqualTo("3")
        assertThat("PENDING_CLOSE".replace(sortedMapOf("PENDING_CLOSE" to "PENDING_CLOSE", "CLOSE" to "1", "OPEN" to "2"))).isEqualTo("PENDING_CLOSE")
        assertThat("PENDING_CLOSE".replace(sortedMapOf("CLOSE" to "1", "PENDING_CLOSE" to "PENDING_CLOSE", "OPEN" to "2"))).isEqualTo("PENDING_CLOSE")
        assertThat("PENDING_CLOSECLOSE".replace(sortedMapOf("CLOSE" to "1", "PENDING_CLOSE" to "PENDING_CLOSE", "OPEN" to "2"))).isEqualTo("PENDING_CLOSE1")
        assertThat("CLOSEPENDING_CLOSECLOSE".replace(sortedMapOf("CLOSE" to "1", "PENDING_CLOSE" to "PENDING_CLOSE", "OPEN" to "2"))).isEqualTo("1PENDING_CLOSE1")
    }
}