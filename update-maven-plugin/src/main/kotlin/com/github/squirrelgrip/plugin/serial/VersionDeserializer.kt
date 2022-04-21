package com.github.squirrelgrip.plugin.serial

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.github.squirrelgrip.plugin.model.Version

class VersionDeserializer : StdDeserializer<Version>(Version::class.java) {
    override fun deserialize(parser: JsonParser, deserializer: DeserializationContext): Version {
        val node: JsonNode = parser.codec.readTree(parser)
        val value: String = node.asText()
        return if (value.isBlank()) Version.NO_VERSION else Version(value)
    }

    override fun getNullValue(ctxt: DeserializationContext?): Version {
        return Version.NO_VERSION
    }
}
