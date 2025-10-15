package com.github.squirrelgrip.extension.jsonschema

import com.fasterxml.classmate.ResolvedType
import com.fasterxml.classmate.TypeResolver
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyDescription
import com.fasterxml.jackson.annotation.JsonTypeName
import com.github.squirrelgrip.extension.json.toJson
import com.github.squirrelgrip.extension.json.toJsonNode
import com.github.victools.jsonschema.generator.Option
import com.github.victools.jsonschema.module.jackson.JacksonModule
import com.github.victools.jsonschema.module.jakarta.validation.JakartaValidationModule
import jakarta.validation.constraints.Min
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class JsonSchemaExtensionTest {
    @Test
    fun toJsonSchema_BasicClass() {
        assertThat(BasicClass::class.java.toJsonSchema()).isEqualTo(
            """
                {
                    "${"\$"}schema":"https://json-schema.org/draft/2020-12/schema",
                    "type":"object",
                    "properties":{
                        "d":{
                            "type":"number"
                        },
                        "f":{
                            "type":"number"
                        },
                        "i":{
                            "type":"integer"
                        },
                        "dNull":{
                            "type":"number"
                        },
                        "fNull":{
                            "type":"number"
                        },
                        "iNull":{
                            "type":"integer"
                        },
                        "listDouble":{
                            "type":"array",
                            "items":{
                                "type":"number"
                            }
                        },
                        "listFloat":{
                            "type":"array",
                            "items":{
                                "type":"number"
                            }
                        },
                        "listInt":{
                            "type":"array",
                            "items":{
                                "type":"integer"
                            }
                        },
                        "listString":{
                            "type":"array",
                            "items":{
                                "type":"string"
                            }
                        },
                        "s":{
                            "type":"string"
                        },
                        "sNull":{
                            "type":"string"
                        }
                    }
                }
            """.toJsonNode()
        )
    }

    @Test
    fun toJsonSchema_JacksonClass() {
        assertThat(JacksonClass::class.java.toJsonSchema()).isEqualTo(
            """
                {
                    "${"\$"}schema":"https://json-schema.org/draft/2020-12/schema",
                    "type":"object",
                    "properties":{
                        "d":{
                            "type":"number"
                        },
                        "f":{
                            "type":"number"
                        },
                        "i":{
                            "type":"integer"
                        },
                        "dNull":{
                            "type":"number"
                        },
                        "fNull":{
                            "type":"number"
                        },
                        "iNull":{
                            "type":"integer"
                        },
                        "listDouble":{
                            "type":"array",
                            "items":{
                                "type":"number"
                            }
                        },
                        "listFloat":{
                            "type":"array",
                            "items":{
                                "type":"number"
                            }
                        },
                        "listInt":{
                            "type":"array",
                            "items":{
                                "type":"integer"
                            }
                        },
                        "listString":{
                            "type":"array",
                            "items":{
                                "type":"string"
                            }
                        },
                        "s":{
                            "type":"string"
                        },
                        "sNull":{
                            "type":"string"
                        }
                    }
                }
            """.toJsonNode()
        )
    }

    @Test
    fun toJsonSchema_WithSubClass_WithoutDefinition() {
        assertThat(WithSubClass::class.java.toJsonSchema()).isEqualTo(
            """
                {
                    "${"\$"}schema":"https://json-schema.org/draft/2020-12/schema",
                    "type":"object",
                    "properties":{
                        "name":{
                            "type":"string"
                        },
                        "subClass":{
                            "type":"object",
                            "properties":{
                                "a":{
                                    "type":"string"
                                },
                                "b":{
                                    "type":"integer"
                                }
                            }
                        }
                    }
                }
            """.toJsonNode()
        )
    }

    @Test
    fun toJsonSchema_WithSubClass_WithDefinition() {
        assertThat(
            WithSubClass::class.java.toJsonSchema(JsonSchema.with(Option.DEFINITIONS_FOR_ALL_OBJECTS))
        ).isEqualTo(
            """
                {
                    "${"\$"}schema":"https://json-schema.org/draft/2020-12/schema",
                    "${"\$"}defs":{
                        "SubClass":{
                            "type":"object",
                            "properties":{
                                "a":{
                                    "type":"string"
                                },
                                "b":{
                                    "type":"integer"
                                }
                            }
                        }
                    },
                    "type":"object",
                    "properties":{
                        "name":{
                            "type":"string"
                        },
                        "subClass":{
                            "${"\$"}ref":"#/${"\$"}defs/SubClass"
                        }
                    }
                }
            """.toJsonNode()
        )
    }

    @Test
    fun createSchemaReference_WithSubClass() {
        assertThat(WithSubClass::class.java.createSchemaReference()).isEqualTo(
            "{}".toJsonNode()
        )
    }

    @Test
    fun toJsonSchema_ArbitraryClass() {
        assertThat(
            ArbitraryClass::class.java.toJsonSchema(
                JsonSchema.defaultSchemaGeneratorConfigBuilder().apply {
                    with(JakartaValidationModule())
                }.build()
            ).toPrettyString()
        ).isEqualTo(
//              "${"\$"}id": "https://example.com/person.schema.json",
//              "title": "Person",
            """
            {
              "${"\$"}schema": "https://json-schema.org/draft/2020-12/schema",
              "type": "object",
              "properties": {
                "age": {
                  "type": "integer",
                  "description": "Age in years which must be equal to or greater than zero.",
                  "minimum": 0
                },
                "firstName": {
                  "type": "string",
                  "description": "The person's first name."
                },
                "lastName": {
                  "type": "string",
                  "description": "The person's last name."
                }
              }
            }
            """.toJsonNode().toPrettyString()
        )
    }

    @Test
    @Disabled
    fun toJsonSchema_ArbitraryDataClass() {
        val dataClass = ArbitraryDataClass("Adrian", "Richter", 18)
        assertThat(dataClass.toJson().toJsonNode()).isEqualTo(
            """
            {
                "firstName":"Adrian",
                "lastName":"Richter",
                "age":18
            }
            """.toJsonNode()
        )
        val typeResolver = TypeResolver()
        val listType: ResolvedType = typeResolver.resolve(ArbitraryClass::class.java)
        assertThat(
            ArbitraryDataClass::class.java.toJsonSchema(
                JsonSchema.defaultSchemaGeneratorConfigBuilder().apply {
                    with(JakartaValidationModule())
                    with(JacksonModule())
                }.build()
            ).toPrettyString()
        ).isEqualTo(
            """
            {
              "${"\$"}schema": "https://json-schema.org/draft/2020-12/schema",
              "type": "object",
              "properties": {
                "age": {
                  "type": "integer",
                  "description": "Age in years which must be equal to or greater than zero.",
                  "minimum": 0
                },
                "firstName": {
                  "type": "string",
                  "description": "The person's first name."
                },
                "lastName": {
                  "type": "string",
                  "description": "The person's last name."
                }
              }
            }
            """.toJsonNode().toPrettyString()
        )
    }
}

data class BasicClass(
    val s: String,
    val i: Int,
    val f: Float,
    val d: Double,
    val sNull: String?,
    val iNull: Int?,
    val fNull: Float?,
    val dNull: Double?,
    val listString: List<String>,
    val listInt: List<Int>,
    val listFloat: List<Float>,
    val listDouble: List<Double>
)

data class JacksonClass(
    @param:JsonProperty(value = "s", required = true) @param:JsonPropertyDescription("s") val s: String,
    @param:JsonProperty(value = "i", required = true) val i: Int,
    @param:JsonProperty(value = "f", required = true) val f: Float,
    @param:JsonProperty(value = "d", required = true) val d: Double,
    @param:JsonProperty(value = "sNull", required = false) val sNull: String?,
    @param:JsonProperty(value = "iNull", required = false) val iNull: Int?,
    @param:JsonProperty(value = "fNull", required = false) val fNull: Float?,
    @param:JsonProperty(value = "dNull", required = false) val dNull: Double?,
    @param:JsonProperty(value = "listString", required = false) val listString: List<String>,
    @param:JsonProperty(value = "listInt", required = false) val listInt: List<Int>,
    @param:JsonProperty(value = "listFloat", required = false) val listFloat: List<Float>,
    @param:JsonProperty(value = "listDouble", required = false) val listDouble: List<Double>
)

data class SubClass(
    val a: String,
    val b: Int
)

data class WithSubClass(
    val name: String,
    val subClass: SubClass
)

@JsonTypeName("Person")
class ArbitraryClass {
    @JsonProperty(value = "firstName", required = true)
    @JsonPropertyDescription("The person's first name.")
    var firstName: String = ""

    @JsonProperty(value = "lastName", required = true)
    @JsonPropertyDescription("The person's last name.")
    var lastName: String = ""

    @JsonProperty(value = "age", required = true)
    @JsonPropertyDescription("Age in years which must be equal to or greater than zero.")
    @Min(value = 0)
    var age: Int = 0
}

@JsonTypeName("Person")
data class ArbitraryDataClass(
    @param:JsonProperty(value = "firstName", required = true)
    @param:JsonPropertyDescription("The person's first name.")
    val first: String,
    @param:JsonProperty(value = "lastName", required = true)
    @param:JsonPropertyDescription("The person's last name.")
    val last: String,
    @param:JsonProperty(value = "age", required = true)
    @param:JsonPropertyDescription("Age in years which must be equal to or greater than zero.")
    @param:Min(value = 0)
    val age: Int
)
