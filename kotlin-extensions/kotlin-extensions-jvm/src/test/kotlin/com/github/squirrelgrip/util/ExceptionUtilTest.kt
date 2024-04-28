package com.github.squirrelgrip.util

import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

internal class ExceptionUtilTest {
    @Test
    fun catching() {
        assertTrue(catching { throw Exception() })
        assertFalse(catching {})
    }

    @Test
    fun notCatching() {
        assertFalse(notCatching { throw Exception() })
        assertTrue(notCatching {})
    }
}
