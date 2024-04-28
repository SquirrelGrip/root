package com.github.squirrelgrip.extension.xml

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.github.squirrelgrip.format.DataFormat
import com.github.squirrelgrip.format.ObjectMapperFactory
import com.github.squirrelgrip.util.notCatching
import java.io.*
import java.net.URL
import java.nio.file.Path

object Xml : DataFormat<XmlMapper, XmlMapper.Builder>(
    object : ObjectMapperFactory<XmlMapper, XmlMapper.Builder> {
        override fun builder(): XmlMapper.Builder =
            XmlMapper.builder()
    }
)

/**
 * Converts Any to a XML String representation
 */
fun Any.toXml(): String = Xml.objectMapper.writeValueAsString(this)
fun Any.toXml(file: File) = Xml.objectMapper.writeValue(file, this)
fun Any.toXml(path: Path) = Xml.objectMapper.writeValue(path.toFile(), this)
fun Any.toXml(outputStream: OutputStream) = Xml.objectMapper.writeValue(outputStream, this)
fun Any.toXml(writer: Writer) = Xml.objectMapper.writeValue(writer, this)
fun Any.toXml(dataOutput: DataOutput) = Xml.objectMapper.writeValue(dataOutput, this)

inline fun <reified T> String.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> InputStream.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> Reader.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> URL.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> ByteArray.toInstance(offset: Int, len: Int): T = Xml.objectMapper.readValue(this, offset, len, T::class.java)
inline fun <reified T> DataInput.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> File.toInstance(): T = Xml.objectMapper.readValue(this, T::class.java)
inline fun <reified T> Path.toInstance(): T = Xml.objectMapper.readValue(this.toFile(), T::class.java)

fun String.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun InputStream.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun Reader.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun URL.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun ByteArray.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun ByteArray.toJsonNode(offset: Int, length: Int): JsonNode = Xml.objectMapper.readTree(this, offset, length)
fun JsonParser.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun File.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this)
fun Path.toJsonNode(): JsonNode = Xml.objectMapper.readTree(this.toFile())

fun String.isXml(): Boolean = notCatching { this.toJsonNode() }
fun InputStream.isXml(): Boolean = notCatching { this.toJsonNode() }
fun Reader.isXml(): Boolean = notCatching { this.toJsonNode() }
fun URL.isXml(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isXml(): Boolean = notCatching { this.toJsonNode() }
fun ByteArray.isXml(offset: Int, length: Int): Boolean = notCatching { this.toJsonNode(offset, length) }
fun JsonParser.isXml(): Boolean = notCatching { this.toJsonNode() }
fun File.isXml(): Boolean = notCatching { this.toJsonNode() }
fun Path.isXml(): Boolean = notCatching { this.toFile().toJsonNode() }

fun String.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun InputStream.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun Reader.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun URL.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun ByteArray.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun ByteArray.toJsonParser(offset: Int, length: Int): JsonParser = Xml.objectMapper.createParser(this, offset, length)
fun File.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this)
fun Path.toJsonParser(): JsonParser = Xml.objectMapper.createParser(this.toFile())