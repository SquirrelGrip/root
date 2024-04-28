package com.github.squirrelgrip.extension.encryption

import com.github.squirrelgrip.extension.encode.fromBase64
import com.github.squirrelgrip.extension.encode.toBase64
import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec

//AES/CBC/NoPadding (128)
//AES/CBC/PKCS5Padding (128)
//AES/ECB/NoPadding (128)
//AES/ECB/PKCS5Padding (128)
//AES/GCM/NoPadding (128)
//DES/CBC/NoPadding (56)
//DES/CBC/PKCS5Padding (56)
//DES/ECB/NoPadding (56)
//DES/ECB/PKCS5Padding (56)
//DESede/CBC/NoPadding (168)
//DESede/CBC/PKCS5Padding (168)
//DESede/ECB/NoPadding (168)
//DESede/ECB/PKCS5Padding (168)
//RSA/ECB/PKCS1Padding (1024, 2048)
//RSA/ECB/OAEPWithSHA-1AndMGF1Padding (1024, 2048)
//RSA/ECB/OAEPWithSHA-256AndMGF1Padding (1024, 2048)
fun String.encrypt(key: String, transformations: String = "AES"): String =
    Cipher.getInstance(transformations).let {
        it.init(Cipher.ENCRYPT_MODE, SecretKeySpec(key.toByteArray(), transformations))
        it.doFinal(this.toByteArray()).toBase64()
    }

fun String.decrypt(key: String, transformations: String = "AES"): String =
    Cipher.getInstance(transformations).let {
        it.init(
            Cipher.DECRYPT_MODE,
            SecretKeySpec(key.toByteArray(), transformations)
        )
        String(it.doFinal(this.fromBase64()))
    }