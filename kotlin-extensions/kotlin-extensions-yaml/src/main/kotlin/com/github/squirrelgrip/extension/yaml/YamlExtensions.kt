package com.github.squirrelgrip.extension.yaml

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.github.squirrelgrip.format.DataFormat
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path

object Yaml : DataFormat<YAMLMapper, YAMLMapper.Builder>(
    object : ObjectMapperFactory<YAMLMapper, YAMLMapper.Builder> {
        override fun builder(): YAMLMapper.Builder =
            YAMLMapper.builder()
    }
)

/**
 * Converts Any to a YAML String representation
 */
fun Any.toYaml(): String = Yaml.objectMapper.writeValueAsString(this)
fun Any.toYaml(file: File) = Yaml.objectMapper.writeValue(file, this)
fun Any.toYaml(path: Path) = Yaml.objectMapper.writeValue(path.toFile(), this)
fun Any.toYaml(outputStream: OutputStream) = Yaml.objectMapper.writeValue(outputStream, this)
fun Any.toYaml(writer: Writer) = Yaml.objectMapper.writeValue(writer, this)
fun Any.toYaml(dataOutput: DataOutput) = Yaml.objectMapper.writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> InputStream.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> Reader.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> URL.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int): T = Yaml.objectMapper.readValue(this, offset, len, T::class.java)
inline fun <reified T> DataInput.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> File.toInstance(): T = Yaml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> Path.toInstance(): T = Yaml.objectMapper.readValue(this.toFile(), T::class.java)

fun String.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun URL.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isYaml(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun File.isYaml(): Boolean = notCatching { this.toJsonNode() }
fun Path.isYaml(): Boolean = notCatching { this.toFile().toJsonNode() }

fun String.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = Yaml.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = Yaml.objectMapper.readTree(this.toFile())

fun String.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun InputStream.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun Reader.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun URL.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun ByteArray.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun ByteArray.toJsonParser(offset: Int, length: Int): JsonParser = Yaml.objectMapper.createParser(this, offset, length)
fun File.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this)
fun Path.toJsonParser(): JsonParser = Yaml.objectMapper.createParser(this.toFile())

