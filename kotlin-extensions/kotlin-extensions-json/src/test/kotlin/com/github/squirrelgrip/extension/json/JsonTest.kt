package com.github.squirrelgrip.extension.json

import com.fasterxml.jackson.databind.ObjectMapper
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.Test

class JsonTest {
    @Test
    fun jsonUsesObjectMapperPassedIn() {
        val objectMapper1 = ObjectMapper()
        val objectMapper2: ObjectMapper = Json.objectMapper
        Assertions.assertThat(objectMapper2).isNotSameAs(objectMapper1)
    }
}
