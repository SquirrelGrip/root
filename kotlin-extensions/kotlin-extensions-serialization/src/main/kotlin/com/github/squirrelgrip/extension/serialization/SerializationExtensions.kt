package com.github.squirrelgrip.extension.serialization

import com.fasterxml.jackson.databind.json.JsonMapper
import com.github.squirrelgrip.util.notCatching
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.serializer

val json: Json =
    Json {
        isLenient = false
        prettyPrint = true
        encodeDefaults = true
    }

fun <T> toSerializationStrategy(serializer: KSerializer<T>): SerializationStrategy<T> = serializer

/**
 * Converts Any to a JSON String representation
 */
@OptIn(InternalSerializationApi::class)
fun Any.toJson(): String {
    return when (this) {
        is Collection<*> -> {
            if (this.isEmpty()) return "[]"

            val first =
                this.first()
                    ?: throw IllegalArgumentException("Cannot determine element type of empty collection")

            val elementType = first::class
            val elementSerializer =
                try {
                    elementType.serializer()
                } catch (e: SerializationException) {
                    throw IllegalArgumentException("Elements of ${elementType.simpleName} are not @Serializable", e)
                }

            @Suppress("UNCHECKED_CAST")
            val listSerializer = ListSerializer(elementSerializer as KSerializer<Any>)
            @Suppress("UNCHECKED_CAST")
            Json.encodeToString(listSerializer, this as List<Any>)
        }

        else -> {
            val serializer =
                try {
                    this::class.serializer()
                } catch (e: SerializationException) {
                    throw IllegalArgumentException("Class ${this::class.simpleName} is not @Serializable", e)
                }

            @Suppress("UNCHECKED_CAST")
            val strategy = serializer as SerializationStrategy<Any>
            Json.encodeToString(strategy, this)
        }
    }
}

// fun Any.toJson(file: File) = Json.objectWriter().writeValue(file, this)
//
// fun Any.toJson(path: Path) = Json.objectWriter().writeValue(path.toFile(), this)
//
// fun Any.toJson(sink: Sink) = Json.objectWriter().writeValue(outputStream, this)

// fun Any.toJson(writer: Writer) = Json.objectWriter().writeValue(writer, this)
//
// fun Any.toJson(dataOutput: DataOutput) = Json.objectWriter().writeValue(dataOutput, this)
//
inline fun <reified T> String.toInstance(): T = Json.decodeFromString<T>(this)

//
// inline fun <reified T> InputStream.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> Reader.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> URL.toInstance(): T =
//    this.openStream().use {
//        Json.objectReader<T>().readValue(it, T::class.java)
//    }
//
// inline fun <reified T> ByteArray.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> ByteArray.toInstance(
//    offset: Int,
//    len: Int
// ): T =
//    Json.objectReader<T>().readValue(
//        this,
//        offset,
//        len,
//        T::class.java
//    )
//
// inline fun <reified T> DataInput.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> JsonParser.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> File.toInstance(): T = Json.objectReader<T>().readValue(this, T::class.java)
//
// inline fun <reified T> Path.toInstance(): T = Json.objectReader<T>().readValue(this.toFile())
//
inline fun <reified T> String.toInstanceList(): List<T> = Json.decodeFromString<List<T>>(this)

//
// inline fun <reified T> InputStream.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> Reader.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> URL.toInstanceList(): List<T> =
//    this.openStream().use {
//        Json.listObjectReader<T>().readValue(it)
//    }
//
// inline fun <reified T> ByteArray.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> ByteArray.toInstanceList(
//    offset: Int,
//    len: Int
// ): List<T> =
//    Json.listObjectReader<T>().readValue(
//        this,
//        offset,
//        len
//    )
//
// inline fun <reified T> DataInput.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> JsonParser.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> File.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this)
//
// inline fun <reified T> Path.toInstanceList(): List<T> = Json.listObjectReader<T>().readValue(this.toFile())
//
// inline fun <reified T> String.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
// inline fun <reified T> InputStream.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
// inline fun <reified T> Reader.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
// inline fun <reified T> URL.toJsonStream(): Stream<T> =
//    this.openStream().use {
//        it.toJsonParser().toJsonStream<T>()
//    }
//
// inline fun <reified T> ByteArray.toJsonStream(): Stream<T> = this.toJsonParser().toJsonStream<T>()
//
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
// fun String.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
//
// fun InputStream.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
//
// fun Reader.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
//
// fun URL.toJsonParser(): JsonParser =
//    this.openStream().use {
//        Json.objectMapper.createParser(it)
//    }
//
// fun ByteArray.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
//
// fun ByteArray.toJsonParser(
//    offset: Int,
//    length: Int
// ): JsonParser = Json.objectMapper.createParser(this, offset, length)
//
// fun File.toJsonParser(): JsonParser = Json.objectMapper.createParser(this)
//
// fun Path.toJsonParser(): JsonParser = Json.objectMapper.createParser(this.toFile())
//
// fun Any.toJsonNode(): JsonNode = Json.objectMapper.valueToTree(this)
//
// fun String.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun InputStream.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun Reader.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun URL.toJsonNode(): JsonNode =
//    this.openStream().use {
//        Json.objectMapper.readTree(it)
//    }
//
// fun ByteArray.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun ByteArray.toJsonNode(
//    offset: Int,
//    length: Int
// ): JsonNode = Json.objectMapper.readTree(this, offset, length)
//
// fun JsonParser.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun File.toJsonNode(): JsonNode = Json.objectMapper.readTree(this)
//
// fun Path.toJsonNode(): JsonNode = Json.objectMapper.readTree(this.toFile())
//
fun String.isJson(): Boolean =
    notCatching {
        Json.decodeFromString<JsonElement>(this)
    }

// fun InputStream.isJson(): Boolean = notCatching { this.toJsonNode() }
//
// fun Reader.isJson(): Boolean = notCatching { this.toJsonNode() }
//
// fun URL.isJson(): Boolean =
//    notCatching {
//        this.openStream().use {
//            it.toJsonNode()
//        }
//    }
//
// fun ByteArray.isJson(): Boolean = notCatching { this.toJsonNode() }
//
// fun ByteArray.isJson(
//    offset: Int,
//    length: Int
// ): Boolean = notCatching { this.toJsonNode(offset, length) }
//
// fun JsonParser.isJson(): Boolean = notCatching { this.toJsonNode() }
//
// fun File.isJson(): Boolean = notCatching { this.toJsonNode() }
//
// fun Path.isJson(): Boolean = notCatching { this.toFile().toJsonNode() }
//
// fun Any.convertToMap(): Map<String, *> =
//    Json.objectMapper.convertValue(
//        this,
//        object : TypeReference<Map<String, *>>() {}
//    ).toMap()
