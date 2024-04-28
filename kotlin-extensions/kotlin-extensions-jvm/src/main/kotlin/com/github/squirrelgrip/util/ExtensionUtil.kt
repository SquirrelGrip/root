package com.github.squirrelgrip.util

fun Boolean.applyIfTrue(function: () -> Unit): Boolean {
    if (this) {
        function.invoke()
    }
    return this
}

fun Boolean.applyIfFalse(function: () -> Unit): Boolean {
    if (!this) {
        function.invoke()
    }
    return this
}