package com.github.squirrelgrip.extension.json

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.json.JsonMapper
import com.github.squirrelgrip.format.DataFormat
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.format.toJsonSequence
import com.github.squirrelgrip.format.toJsonStream
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path
import java.util.stream.Stream

object Json : DataFormat<JsonMapper, JsonMapper.Builder>(
    object : ObjectMapperFactory<JsonMapper, JsonMapper.Builder> {
        override fun builder(): JsonMapper.Builder =
            JsonMapper.builder()
    }
)

/**
 * Converts Any to a JSON String representation
 */
fun Any.toJson(): String = Json.objectWriter().writeValueAsString(this)
fun Any.toJson(file: File) = Json.objectWriter().writeValue(file, this)
fun Any.toJson(path: Path) = Json.objectWriter().writeValue(path.toFile(), this)
fun Any.toJson(outputStream: OutputStream) = Json.objectWriter().writeValue(outputStream, this)
fun Any.toJson(writer: Writer) = Json.objectWriter().writeValue(writer, this)
fun Any.toJson(dataOutput: DataOutput) = Json.objectWriter().writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(): T = Json.objectReader<T>().readValue(this)
inline fun <reified T> InputStream.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> Reader.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> URL.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int): T = Json.objectReader<T>().readValue(this, offset, len, T::class.java)

inline fun <reified T> DataInput.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> JsonParser.toInstance(): T = com.github.squirrelgrip.extension.json.Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> File.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
inline fun <reified T> Path.toInstance(): T = Json.objectReader<T>().readValue(this.toFile())

inline fun <reified T> String.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> InputStream.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> Reader.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> URL.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> ByteArray.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> ByteArray.toInstanceList(offset: Int, len: Int): List<T> = Json.listObjectReader<T>().readValue(this, offset, len)

inline fun <reified T> DataInput.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> JsonParser.toInstanceList(): List<T> = com.github.squirrelgrip.extension.json.Json.listObjectReader<T>().readValue(this)
inline fun <reified T> File.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
inline fun <reified T> Path.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this.toFile())

inline fun <reified T> String.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> InputStream.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> Reader.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> URL.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> ByteArray.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> ByteArray.toJsonStream(offset: Int, length: Int): Stream<T> = this.toJsonParser(offset, length).toJsonStream<T>()

inline fun <reified T> File.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
inline fun <reified T> Path.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()

inline fun <reified T> String.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence()
inline fun <reified T> InputStream.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
inline fun <reified T> Reader.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
inline fun <reified T> URL.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
inline fun <reified T> ByteArray.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
inline fun <reified T> ByteArray.toJsonSequence(offset: Int, length: Int): Sequence<T> = this.toJsonParser(offset, length).toJsonSequence<T>()

inline fun <reified T> File.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()
inline fun <reified T> Path.toJsonSequence(): Sequence<T> = this.toJsonParser().toJsonSequence<T>()

fun String.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun InputStream.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun Reader.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun URL.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun ByteArray.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun ByteArray.toJsonParser(offset: Int, length: Int): JsonParser = Json.objectMapper.createParser(this, offset, length)
fun File.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
fun Path.toJsonParser(): JsonParser = Json.objectMapper.createParser(this.toFile())

fun String.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = Json.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = com.github.squirrelgrip.extension.json.Json.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = Json.objectMapper.readTree(this.toFile())

fun String.isJson(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isJson(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isJson(): Boolean = notCatching { this.toJsonNode() }
fun URL.isJson(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isJson(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isJson(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isJson(): Boolean = notCatching { this.toJsonNode() }
fun File.isJson(): Boolean = notCatching { this.toJsonNode() }
fun Path.isJson(): Boolean = notCatching { this.toFile().toJsonNode() }

fun Any.convertToMap(): Map<String, *> = Json.objectMapper.convertValue(this, object : TypeReference<Map<String, *>>() {}).toMap()


