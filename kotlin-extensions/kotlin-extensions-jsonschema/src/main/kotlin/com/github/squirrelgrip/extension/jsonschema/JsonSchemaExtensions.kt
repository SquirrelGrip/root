package com.github.squirrelgrip.extension.jsonschema

import com.fasterxml.jackson.databind.JsonNode
import com.github.squirrelgrip.extension.json.Json
import com.github.victools.jsonschema.generator.*
import com.github.victools.jsonschema.module.jackson.JacksonModule
import java.lang.reflect.Type

object JsonSchema {
    val defaultSchemaGeneratorConfig = with()

    fun with(vararg options: Option): SchemaGeneratorConfig =
        defaultSchemaGeneratorConfigBuilder().apply {
            options.fold(this) { builder, option ->
                builder.with(option)
            }
        }.build()

    fun defaultSchemaGeneratorConfigBuilder(): SchemaGeneratorConfigBuilder =
        SchemaGeneratorConfigBuilder(
            Json.objectMapper,
            SchemaVersion.DRAFT_2020_12,
            OptionPreset.PLAIN_JSON
        ).with(
            JacksonModule()
        )

    fun generateSchema(
        type: Type,
        schemaGeneratorConfig: SchemaGeneratorConfig = defaultSchemaGeneratorConfig
    ): JsonNode =
        SchemaGenerator(schemaGeneratorConfig).generateSchema(type)

    fun createSchemaReference(
        type: Type,
        schemaGeneratorConfig: SchemaGeneratorConfig = defaultSchemaGeneratorConfig
    ): JsonNode =
        SchemaGenerator(schemaGeneratorConfig).buildMultipleSchemaDefinitions().createSchemaReference(type)
}

fun Type.toJsonSchema(
    schemaGeneratorConfig: SchemaGeneratorConfig = JsonSchema.defaultSchemaGeneratorConfig
): JsonNode =
    JsonSchema.generateSchema(
        this,
        schemaGeneratorConfig
    )

fun Type.createSchemaReference(
    schemaGeneratorConfig: SchemaGeneratorConfig = JsonSchema.defaultSchemaGeneratorConfig
): JsonNode =
    JsonSchema.createSchemaReference(
        this,
        schemaGeneratorConfig
    )

//fun Any.toJsonSchema(file: File) = JsonSchema.objectWriter().writeValue(file, this)
//fun Any.toJsonSchema(path: Path) = JsonSchema.objectWriter().writeValue(path.toFile(), this)
//fun Any.toJsonSchema(outputStream: OutputStream) = JsonSchema.objectWriter().writeValue(outputStream, this)
//fun Any.toJsonSchema(writer: Writer) = JsonSchema.objectWriter().writeValue(writer, this)
//fun Any.toJsonSchema(dataOutput: DataOutput) = JsonSchema.objectWriter().writeValue(dataOutput, this)

