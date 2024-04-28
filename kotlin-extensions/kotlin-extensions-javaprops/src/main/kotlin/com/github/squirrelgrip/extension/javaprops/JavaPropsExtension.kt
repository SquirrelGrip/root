package com.github.squirrelgrip.extension.javaprops

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsMapper
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsSchema
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.format.SchemaDataFormat
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path

object JavaProps : SchemaDataFormat<JavaPropsMapper, JavaPropsMapper.Builder, JavaPropsSchema>(
    object : ObjectMapperFactory<JavaPropsMapper, JavaPropsMapper.Builder> {
        override fun builder(): JavaPropsMapper.Builder =
            JavaPropsMapper.builder()
    }
) {
    override fun getSchema(clazz: Class<*>): JavaPropsSchema =
        JavaPropsSchema.emptySchema()
}

/**
 * Converts Any to a JavaProps representation
 */
fun Any.toJavaProps(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): String = JavaProps.objectWriter(schema).writeValueAsString(this)
fun Any.toJavaProps(file: File, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)) = JavaProps.objectWriter(schema).writeValue(file, this)
fun Any.toJavaProps(path: Path, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)) = JavaProps.objectWriter(schema).writeValue(path.toFile(), this)
fun Any.toJavaProps(outputStream: OutputStream, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)) = JavaProps.objectWriter(schema).writeValue(outputStream, this)
fun Any.toJavaProps(writer: Writer, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)) = JavaProps.objectWriter(schema).writeValue(writer, this)
fun Any.toJavaProps(dataOutput: DataOutput, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)) = JavaProps.objectWriter(schema).writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> InputStream.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> Reader.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> URL.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int, schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, offset, len, T::class.java)
inline fun <reified T> DataInput.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> File.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> Path.toInstance(schema: JavaPropsSchema = JavaProps.getSchema(this.javaClass)): T = JavaProps.objectReader<T>(schema).readValue(this.toFile(), T::class.java)

fun String.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = JavaProps.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = JavaProps.objectMapper.readTree(this.toFile())

fun String.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun URL.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isJavaProps(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun File.isJavaProps(): Boolean = notCatching { this.toJsonNode() }
fun Path.isJavaProps(): Boolean = notCatching { this.toFile().toJsonNode() }

fun String.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun InputStream.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun Reader.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun URL.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun ByteArray.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun ByteArray.toJsonParser(offset: Int, length: Int): JsonParser = JavaProps.objectMapper.createParser(this, offset, length)
fun File.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this)
fun Path.toJsonParser(): JsonParser = JavaProps.objectMapper.createParser(this.toFile())