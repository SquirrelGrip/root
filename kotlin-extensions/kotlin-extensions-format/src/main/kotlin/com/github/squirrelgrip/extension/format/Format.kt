package com.github.squirrelgrip.format

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.core.FormatSchema
import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.cfg.MapperBuilder
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.KotlinFeature
import com.fasterxml.jackson.module.kotlin.kotlinModule
import java.util.*
import java.util.stream.Stream
import java.util.stream.StreamSupport
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

@JsonIgnoreProperties("stackTrace")
internal class ThrowableMixIn @JsonCreator constructor(
    @JsonProperty("message") message: String?
) : Throwable(message)

abstract class DataFormat<M : ObjectMapper, B : MapperBuilder<M, B>>(factory: ObjectMapperFactory<M, B>) {
    val objectMapper: M by object : ReadOnlyProperty<DataFormat<M, B>, M> {
        lateinit var value: M
        val defaultObjectMapper: M by lazy {
            val factoryList = ServiceLoader.load(factory.javaClass).toList()
            if (factoryList.size > 1) {
                throw RuntimeException("Cannot have more than one MapperFactory declared.")
            }
            (factoryList.firstOrNull() ?: factory).createObjectMapper()
        }

        override fun getValue(thisRef: DataFormat<M, B>, property: KProperty<*>): M =
            if (!this::value.isInitialized)
                defaultObjectMapper
            else
                value

    }

    fun objectWriter(): ObjectWriter = objectMapper.copy().writer()
    inline fun <reified T> objectReader(): ObjectReader = objectMapper.copy().readerFor(T::class.java)
    inline fun <reified T> listObjectReader(): ObjectReader = objectMapper.copy().readerForListOf(T::class.java)
}

abstract class SchemaDataFormat<M : ObjectMapper, B : MapperBuilder<M, B>, S : FormatSchema>(
    factory: ObjectMapperFactory<M, B>
) : DataFormat<M, B>(factory) {
    abstract fun getSchema(clazz: Class<*>): S
    fun objectWriter(schema: S): ObjectWriter = objectMapper.copy().writer(schema)
    inline fun <reified T> objectReader(schema: S): ObjectReader = objectReader<T>().with(schema)
    inline fun <reified T> listObjectReader(schema: S): ObjectReader = listObjectReader<T>().with(schema)
}

inline fun <reified T> JsonParser.toJsonStream(): Stream<T> =
    StreamSupport.stream(Spliterators.spliteratorUnknownSize(JsonIterator(this, T::class.java), 0), false)

inline fun <reified T> JsonParser.toJsonSequence(): Sequence<T> = JsonSequence(this, T::class.java)

interface ObjectMapperFactory<M : ObjectMapper, B : MapperBuilder<M, B>> {
    fun createObjectMapper(): M =
        builder()
            .addModule(JavaTimeModule())
            .addModule(kotlinModule {
                enable(KotlinFeature.StrictNullChecks)
            })
            .addModule(Jdk8Module())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .addMixIn(Throwable::class.java, ThrowableMixIn::class.java)
            .build()

    fun builder(): B
}

class JsonSequence<T>(
    private val jsonParser: JsonParser,
    private val type: Class<T>
) : Sequence<T> {
    override fun iterator(): Iterator<T> =
        JsonIterator(jsonParser, type)
}

class JsonIterator<T>(
    private val jsonParser: JsonParser,
    private val type: Class<T>
) : Iterator<T> {
    init {
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw Exception("json must be an array")
        }
    }

    private var nextValue: T? = doInternalNext()

    override fun hasNext(): Boolean = nextValue != null

    override fun next(): T =
        nextValue!!.apply {
            nextValue = doInternalNext()
        }

    private fun doInternalNext(): T? =
        if (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            jsonParser.readValueAs(type)
        } else {
            jsonParser.close()
            null
        }
}
