package com.github.squirrelgrip.extension.protobuf

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.protobuf.ProtobufMapper
import com.fasterxml.jackson.dataformat.protobuf.schema.ProtobufSchema
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.format.SchemaDataFormat
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path

object Protobuf : SchemaDataFormat<ProtobufMapper, ProtobufMapper.Builder, ProtobufSchema>(
    object : ObjectMapperFactory<ProtobufMapper, ProtobufMapper.Builder> {
        override fun builder(): ProtobufMapper.Builder =
            ProtobufMapper.builder()
    }
) {
    override fun getSchema(clazz: Class<*>): ProtobufSchema =
        objectMapper.generateSchemaFor(clazz)
}

/**
 * Converts Any to a Protobuf representation
 */
fun Any.toProtobuf(schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)): ByteArray = Protobuf.objectWriter(schema).writeValueAsBytes(this)
fun Any.toProtobuf(file: File, schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)) = Protobuf.objectWriter(schema).writeValue(file, this)
fun Any.toProtobuf(path: Path, schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)) = Protobuf.objectWriter(schema).writeValue(path.toFile(), this)
fun Any.toProtobuf(outputStream: OutputStream, schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)) = Protobuf.objectWriter(schema).writeValue(outputStream, this)
fun Any.toProtobuf(writer: Writer, schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)) = Protobuf.objectWriter(schema).writeValue(writer, this)
fun Any.toProtobuf(dataOutput: DataOutput, schema: ProtobufSchema = Protobuf.getSchema(this.javaClass)) = Protobuf.objectWriter(schema).writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> InputStream.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> Reader.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> URL.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int, schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this, offset, len)
inline fun <reified T> DataInput.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> File.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this)
inline fun <reified T> Path.toInstance(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): T = Protobuf.objectReader<T>(schema).readValue(this.toFile())

inline fun <reified T> String.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> InputStream.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> Reader.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> URL.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstanceList(offset: Int, len: Int, schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this, offset, len)
inline fun <reified T> DataInput.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> File.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> Path.toInstanceList(schema: ProtobufSchema = Protobuf.getSchema(T::class.java)): List<T> = Protobuf.listObjectReader<T>(schema).readValue(this.toFile())

fun String.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = Protobuf.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = Protobuf.objectMapper.readTree(this.toFile())

fun String.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun URL.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isProtobuf(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun File.isProtobuf(): Boolean = notCatching { this.toJsonNode() }
fun Path.isProtobuf(): Boolean = notCatching { this.toFile().toJsonNode() }
