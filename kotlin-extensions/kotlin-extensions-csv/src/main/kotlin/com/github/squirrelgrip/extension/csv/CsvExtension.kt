package com.github.squirrelgrip.extension.csv

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.csv.CsvMapper
import com.fasterxml.jackson.dataformat.csv.CsvSchema
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.format.SchemaDataFormat
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path

object Csv : SchemaDataFormat<CsvMapper, CsvMapper.Builder, CsvSchema>(
    object : ObjectMapperFactory<CsvMapper, CsvMapper.Builder> {
        override fun builder(): CsvMapper.Builder =
            CsvMapper.builder()
    }
) {
    override fun getSchema(clazz: Class<*>): CsvSchema =
        objectMapper.schemaFor(clazz).withHeader()
}

/**
 * Converts Any to a CSV representation
 */
fun Any.toCsv(schema: CsvSchema = Csv.getSchema(this.javaClass)): String = Csv.objectWriter(schema).writeValueAsString(this)
fun Any.toCsv(file: File, schema: CsvSchema = Csv.getSchema(this.javaClass)) = Csv.objectWriter(schema).writeValue(file, this)
fun Any.toCsv(path: Path, schema: CsvSchema = Csv.getSchema(this.javaClass)) = Csv.objectWriter(schema).writeValue(path.toFile(), this)
fun Any.toCsv(outputStream: OutputStream, schema: CsvSchema = Csv.getSchema(this.javaClass)) = Csv.objectWriter(schema).writeValue(outputStream, this)
fun Any.toCsv(writer: Writer, schema: CsvSchema = Csv.getSchema(this.javaClass)) = Csv.objectWriter(schema).writeValue(writer, this)
fun Any.toCsv(dataOutput: DataOutput, schema: CsvSchema = Csv.getSchema(this.javaClass)) = Csv.objectWriter(schema).writeValue(dataOutput, this)
inline fun <reified T> Iterable<T>.toCsv(schema: CsvSchema = Csv.getSchema(T::class.java)): String = Csv.objectWriter(schema).writeValueAsString(this)
inline fun <reified T> Iterable<T>.toCsv(file: File, schema: CsvSchema = Csv.getSchema(T::class.java)) = Csv.objectWriter(schema).writeValue(file, this)
inline fun <reified T> Iterable<T>.toCsv(path: Path, schema: CsvSchema = Csv.getSchema(T::class.java)) = Csv.objectWriter(schema).writeValue(path.toFile(), this)
inline fun <reified T> Iterable<T>.toCsv(outputStream: OutputStream, schema: CsvSchema = Csv.getSchema(T::class.java)) = Csv.objectWriter(schema).writeValue(outputStream, this)
inline fun <reified T> Iterable<T>.toCsv(writer: Writer, schema: CsvSchema = Csv.getSchema(T::class.java)) = Csv.objectWriter(schema).writeValue(writer, this)
inline fun <reified T> Iterable<T>.toCsv(dataOutput: DataOutput, schema: CsvSchema = Csv.getSchema(T::class.java)) = Csv.objectWriter(schema).writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> InputStream.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> Reader.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> URL.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int, schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, offset, len, T::class.java)
inline fun <reified T> DataInput.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> File.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this, T::class.java)
inline fun <reified T> Path.toInstance(schema: CsvSchema = Csv.getSchema(T::class.java)): T = Csv.objectReader<T>(schema).readValue(this.toFile(), T::class.java)

inline fun <reified T> String.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> InputStream.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> Reader.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> URL.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> ByteArray.toInstanceList(offset: Int, len: Int, schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this, offset, len)
inline fun <reified T> DataInput.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> File.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this)
inline fun <reified T> Path.toInstanceList(schema: CsvSchema = Csv.getSchema(T::class.java)): List<T> = Csv.listObjectReader<T>(schema).readValue(this.toFile())

fun String.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = Csv.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = Csv.objectMapper.readTree(this.toFile())

fun String.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun URL.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isCsv(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun File.isCsv(): Boolean = notCatching { this.toJsonNode() }
fun Path.isCsv(): Boolean = notCatching { this.toFile().toJsonNode() }

fun String.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun InputStream.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun Reader.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun URL.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun ByteArray.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun ByteArray.toJsonParser(offset: Int, length: Int): JsonParser = Csv.objectMapper.createParser(this, offset, length)
fun File.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this)
fun Path.toJsonParser(): JsonParser = Csv.objectMapper.createParser(this.toFile())