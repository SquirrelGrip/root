package com.github.squirrelgrip.extension.jackson

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.cfg.MapperBuilder
import java.util.ServiceLoader
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

abstract class JacksonDataFormat<M : ObjectMapper, B : MapperBuilder<M, B>>(
    factory: ObjectMapperFactory<M, B>
) : DataFormat {
    val objectMapper: M by object : ReadOnlyProperty<JacksonDataFormat<M, B>, M> {
        lateinit var value: M
        val defaultObjectMapper: M by lazy {
            val factoryList = ServiceLoader.load(factory.javaClass).toList()
            if (factoryList.size > 1) {
                throw RuntimeException("Cannot have more than one MapperFactory declared.")
            }
            (factoryList.firstOrNull() ?: factory).createObjectMapper()
        }

        override fun getValue(
            thisRef: JacksonDataFormat<M, B>,
            property: KProperty<*>
        ): M =
            if (!this::value.isInitialized) {
                defaultObjectMapper
            } else {
                value
            }
    }

    fun objectWriter(): ObjectWriter = objectMapper.copy().writer()

    inline fun <reified T> objectReader(): ObjectReader = objectMapper.copy().readerFor(T::class.java)

    inline fun <reified T> listObjectReader(): ObjectReader = objectMapper.copy().readerForListOf(T::class.java)
}
