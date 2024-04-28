package com.github.squirrelgrip.extension.map

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class MapExtensionsTest {
    @Test
    fun `reverseMany() reverses the keys and values to map of value to list of keys`() {
        val map = mapOf(
            1 to "AAA",
            2 to "BBB",
            3 to "BBB"
        )
        assertThat(map.swap()).isEqualTo(
            mapOf(
                "AAA" to listOf(1),
                "BBB" to listOf(2, 3)
            )
        )
    }

    @Test
    fun `reverseCollection() reverses the keys and values to map of value to list of keys`() {
        val map = mapOf(
            1 to listOf("AAA"),
            2 to listOf("BBB"),
            3 to listOf("AAA", "BBB"),
            4 to listOf("CCC", "BBB")
        )
        assertThat(map.swapWithCollection()).isEqualTo(
            mapOf(
                "AAA" to listOf(1, 3),
                "BBB" to listOf(2, 3, 4),
                "CCC" to listOf(4)
            )
        )
    }

    @Test
    fun `reverse() reverses the keys and values`() {
        val map = mapOf(
            1 to "AAA",
            2 to "BBB"
        )
        assertThat(map.swap()).isEqualTo(
            mapOf(
                "AAA" to listOf(1),
                "BBB" to listOf(2)
            )
        )
    }

    @Test
    fun `flatten simple map`() {
        assertThat(mapOf<String, String>().flatten()).isEqualTo(mapOf<String, String>())
        assertThat(mapOf("1" to null).flatten()).isEqualTo(mapOf("1" to null))
        assertThat(mapOf("1" to "AAA").flatten()).isEqualTo(mapOf("1" to "AAA"))
        assertThat(mapOf("1" to listOf("AAA", "BBB")).flatten()).isEqualTo(mapOf("1/0" to "AAA", "1/1" to "BBB"))
        assertThat(mapOf("1" to listOf(mapOf("A" to "AAA", "B" to "BBB"), mapOf("C" to "CCC"))).flatten()).isEqualTo(
            mapOf("1/0/A" to "AAA", "1/0/B" to "BBB", "1/1/C" to "CCC")
        )
        assertThat(mapOf("1" to mapOf("A" to "AAA", "B" to "BBB")).flatten()).isEqualTo(
            mapOf(
                "1/A" to "AAA",
                "1/B" to "BBB"
            )
        )
    }
}
