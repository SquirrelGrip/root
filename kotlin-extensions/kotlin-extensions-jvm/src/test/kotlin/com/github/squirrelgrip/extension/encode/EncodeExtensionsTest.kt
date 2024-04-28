package com.github.squirrelgrip.extension.encode

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EncodeExtensionsTest {
    @Test
    fun toHex() {
        assertThat(byteArrayOf(0x01, 0x02).toHex()).isEqualTo("0102")
    }

    @Test
    fun fromHex() {
        assertThat("0102".fromHex()).isEqualTo(byteArrayOf(0x01, 0x02))
    }
}
