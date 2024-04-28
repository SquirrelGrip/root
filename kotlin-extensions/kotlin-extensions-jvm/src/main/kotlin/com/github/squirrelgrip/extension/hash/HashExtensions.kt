package com.github.squirrelgrip.extension.hash

import java.security.MessageDigest

fun String.md2Hash(): ByteArray = this.toHash("MD2")
fun String.md5Hash(): ByteArray = this.toHash("MD5")
fun String.sha1Hash(): ByteArray = this.toHash("SHA")
fun String.sha224Hash(): ByteArray = this.toHash("SHA-224")
fun String.sha256Hash(): ByteArray = this.toHash("SHA-256")
fun String.sha384Hash(): ByteArray = this.toHash("SHA-384")
fun String.sha512Hash(): ByteArray = this.toHash("SHA-512")
fun ByteArray.md2Hash(): ByteArray = this.toHash("MD2")
fun ByteArray.md5Hash(): ByteArray = this.toHash("MD5")
fun ByteArray.sha1Hash(): ByteArray = this.toHash("SHA")
fun ByteArray.sha224Hash(): ByteArray = this.toHash("SHA-224")
fun ByteArray.sha256Hash(): ByteArray = this.toHash("SHA-256")
fun ByteArray.sha384Hash(): ByteArray = this.toHash("SHA-384")
fun ByteArray.sha512Hash(): ByteArray = this.toHash("SHA-512")

fun String.toHash(algorithm: String = "MD5"): ByteArray =
    MessageDigest.getInstance(algorithm).let {
        it.update(this.toByteArray())
        it.digest()
    }

fun ByteArray.toHash(algorithm: String = "MD5"): ByteArray =
    MessageDigest.getInstance(algorithm).let {
        it.update(this)
        it.digest()
    }
