package com.github.squirrelgrip.extension.regex

fun String.replace(map: Map<String, String>): String =
    if (map.isNotEmpty()) {
        this.replace(map.keys.joinToString("|").toRegex()) {
            map[it.value]!!
        }
    } else {
        this
    }
