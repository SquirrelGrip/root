package com.github.squirrelgrip.util

inline fun catching(fn: () -> Unit): Boolean =
    try {
        fn()
        false
    } catch (e: Throwable) {
        true
    }

inline fun notCatching(fn: () -> Unit): Boolean =
    try {
        fn()
        true
    } catch (e: Throwable) {
        false
    }
