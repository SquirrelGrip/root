package com.github.squirrelgrip.extension.json

import com.github.squirrelgrip.extension.file.toReader
import com.github.squirrelgrip.extension.file.toWriter
import com.github.squirrelgrip.extension.gson.GsonIterator
import com.github.squirrelgrip.extension.io.toReader
import com.github.squirrelgrip.extension.io.toWriter
import com.github.squirrelgrip.util.notCatching
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import java.io.Reader
import java.io.Writer
import java.net.URL
import java.nio.file.Path
import java.time.Instant
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport

object GsonEngine {
    private val gson: Gson by lazy {
        com.google.gson
            .GsonBuilder()
            .registerTypeAdapter(
                Instant::class.java,
                object : TypeAdapter<Instant>() {
                    override fun write(
                        out: com.google.gson.stream.JsonWriter,
                        value: Instant?
                    ) {
                        if (value == null) {
                            out.nullValue()
                        } else {
                            out.value(value.toString())
                        }
                    }

                    override fun read(`in`: JsonReader): Instant? =
                        when (`in`.peek()) {
                            com.google.gson.stream.JsonToken.NULL -> {
                                `in`.nextNull()
                                null
                            }
                            else -> Instant.parse(`in`.nextString())
                        }
                }
            ).create()
    }

    fun get(): Gson = gson
}

/**
 * Converts Any to a JSON String representation
 */
fun Any.toJson(): String = GsonEngine.get().toJson(this, this::class.java)

fun Any.toJson(writer: Writer) = GsonEngine.get().toJson(this, this::class.java, GsonEngine.get().newJsonWriter(writer))

fun Any.toJson(file: File) = this.toJson(file.toWriter())

fun Any.toJson(path: Path) = this.toJson(path.toWriter())

fun Any.toJson(outputStream: OutputStream) = this.toJson(outputStream.toWriter())

fun JsonElement.toJson() = GsonEngine.get().toJson(this)

inline fun <reified T> String.toInstance(): T = GsonEngine.get().fromJson<T>(this, T::class.java)

inline fun <reified T> Reader.toInstance(): T = GsonEngine.get().fromJson<T>(this, T::class.java)

inline fun <reified T> ByteArray.toInstance(): T = ByteArrayInputStream(this).toReader().toInstance<T>()

inline fun <reified T> InputStream.toInstance(): T = this.toReader().toInstance<T>()

inline fun <reified T> URL.toInstance(): T =
    this.openStream().toReader().use {
        it.toInstance<T>()
    }

inline fun <reified T> ByteArray.toInstance(
    offset: Int,
    len: Int
): T = this.copyOfRange(offset, offset + len).toInstance<T>()

inline fun <reified T> File.toInstance(): T = this.toReader().toInstance<T>()

inline fun <reified T> Path.toInstance(): T = this.toReader().toInstance<T>()

inline fun <reified T> String.toInstanceList(): List<T> = GsonEngine.get().fromJson<List<T>>(this, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> Reader.toInstanceList(): List<T> = GsonEngine.get().fromJson<List<T>>(this, object : TypeToken<List<T>>() {}.type)

inline fun <reified T> InputStream.toInstanceList(): List<T> = this.toReader().toInstanceList<T>()

inline fun <reified T> URL.toInstanceList(): List<T> =
    this.openStream().toReader().use { reader ->
        reader.toInstanceList()
    }

inline fun <reified T> ByteArray.toInstanceList(): List<T> = ByteArrayInputStream(this).toReader().toInstanceList<T>()

inline fun <reified T> ByteArray.toInstanceList(
    offset: Int,
    len: Int
): List<T> = this.copyOfRange(offset, offset + len).toInstanceList<T>()

// inline fun <reified T> JsonParser.toInstanceList(): List<T> = GsonEngine.listObjectReader<T>().readValue(this)

inline fun <reified T> File.toInstanceList(): List<T> = this.toReader().toInstanceList<T>()

inline fun <reified T> Path.toInstanceList(): List<T> = this.toReader().toInstanceList<T>()

inline fun <reified T> String.toJsonStream(): Stream<T> = StreamSupport.stream(Spliterators.spliteratorUnknownSize(GsonIterator(this, T::class.java), 0), false)

inline fun <reified T> Reader.toJsonStream(): Stream<T> = StreamSupport.stream(Spliterators.spliteratorUnknownSize(GsonIterator(this, T::class.java), 0), false)

inline fun <reified T> InputStream.toJsonStream(): Stream<T> = this.toReader().toJsonStream<T>()

inline fun <reified T> URL.toJsonStream(): Stream<T> = this.openStream().toJsonStream<T>()

inline fun <reified T> ByteArray.toJsonStream(): Stream<T> = ByteArrayInputStream(this).toJsonStream<T>()

// inline fun <reified T> ByteArray.toJsonStream(
//    offset: Int,
//    length: Int
// ): Stream<T> =
//    this.toJsonParser(
//        offset,
//        length
//    ).toJsonStream<T>()
//
// inline fun <reified T> File.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
// inline fun <reified T> Path.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
// inline fun <reified T> String.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence()
//
// inline fun <reified T> InputStream.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
//
// inline fun <reified T> Reader.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
//
// inline fun <reified T> URL.toJsonSequence(): Sequence<T> =
//    this.openStream().use {
//        it.toJsonParser().toJsonSequence<T>()
//    }
//
// inline fun <reified T> ByteArray.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
//
// inline fun <reified T> ByteArray.toJsonSequence(
//    offset: Int,
//    length: Int
// ): Sequence<T> =
//    this.toJsonParser(
//        offset,
//        length
//    ).toJsonSequence<T>()
//
// inline fun <reified T> File.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
//
// inline fun <reified T> Path.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
//
// fun String.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this)
//
// fun InputStream.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this)
//
// fun Reader.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this)
//
// fun URL.toJsonParser(): JsonParser =
//    this.openStream().use {
//        GsonEngine.objectMapper.createParser(it)
//    }
//
// fun ByteArray.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this)
//
// fun ByteArray.toJsonParser(
//    offset: Int,
//    length: Int
// ): JsonParser = GsonEngine.objectMapper.createParser(this, offset, length)
//
// fun File.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this)
//
// fun Path.toJsonParser(): JsonParser = GsonEngine.objectMapper.createParser(this.toFile())
//
fun Any.toJsonElement(): JsonElement = GsonEngine.get().toJsonTree(this, this::class.java)

fun String.isJson(): Boolean = notCatching { this.toInstance<Any>() }

fun InputStream.isJson(): Boolean = notCatching { this.toInstance<Any>() }

fun Reader.isJson(): Boolean = notCatching { this.toInstance<Any>() }

fun URL.isJson(): Boolean =
    notCatching {
        this.toInstance<Any>()
    }

fun ByteArray.isJson(): Boolean = notCatching { this.toInstance<Any>() }

fun ByteArray.isJson(
    offset: Int,
    length: Int
): Boolean = notCatching { this.toInstance<Any>(offset, length) }

// fun JsonParser.isJson(): Boolean = notCatching { this.toJsonNode() }
//
fun File.isJson(): Boolean = notCatching { this.toInstance<Any>() }

fun Path.isJson(): Boolean = notCatching { this.toInstance<Any>() }
