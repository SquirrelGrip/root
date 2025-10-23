package com.github.squirrelgrip.extension.jackson

import com.fasterxml.jackson.core.FormatSchema
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.fasterxml.jackson.databind.ObjectWriter
import com.fasterxml.jackson.databind.cfg.MapperBuilder

abstract class SchemaDataFormat<M : ObjectMapper, B : MapperBuilder<M, B>, S : FormatSchema>(
    factory: ObjectMapperFactory<M, B>
) : JacksonDataFormat<M, B>(factory) {
    abstract fun getSchema(clazz: Class<*>): S

    fun objectWriter(schema: S): ObjectWriter = objectMapper.copy().writer(schema)

    inline fun <reified T> objectReader(schema: S): ObjectReader = objectReader<T>().with(schema)

    inline fun <reified T> listObjectReader(schema: S): ObjectReader = listObjectReader<T>().with(schema)
}
