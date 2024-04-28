package com.github.squirrelgrip.extension.encode

import java.util.*

private val HEX_ARRAY = "0123456789ABCDEF".toCharArray()

fun ByteArray.toBase64(): String =
    Base64.getEncoder().encodeToString(this)

fun String.fromBase64(): ByteArray =
    Base64.getDecoder().decode(this)

fun ByteArray.toHex(): String =
    this.map {
        val v: Int = it.toInt() and 0xFF
        val first = HEX_ARRAY[v ushr 4]
        val second = HEX_ARRAY[v and 0x0F]
        "$first$second"
    }.joinToString("") { it }

fun String.fromHex(): ByteArray {
    val output = StringBuilder()
    var i = 0
    while (i < this.length) {
        val str: String = this.substring(i, i + 2)
        output.append(str.toInt(16).toChar())
        i += 2
    }
    return output.toString().toByteArray()
}
