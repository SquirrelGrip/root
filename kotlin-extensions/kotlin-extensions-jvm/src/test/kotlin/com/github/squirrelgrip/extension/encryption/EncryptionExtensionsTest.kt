package com.github.squirrelgrip.extension.encryption

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class EncryptionExtensionsTest {
    @Test
    fun encryptAES() {
        val key = "Bar12345Bar12345"
        val input = "Hello World"
        assertThat(input.encrypt(key).decrypt(key)).isEqualTo(input)
    }
}