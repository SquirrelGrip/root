package com.github.squirrelgrip.plugin.serial

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.github.squirrelgrip.extension.xml.Xml
import com.github.squirrelgrip.extension.xml.toInstance
import com.github.squirrelgrip.plugin.model.Version
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

class VersionDeserializerTest {
    companion object {
        @JvmStatic
        @BeforeAll
        fun beforeAll() {
            Xml.objectMapper.registerModule(
                SimpleModule().apply {
                    addDeserializer(
                        Version::class.java,
                        VersionDeserializer()
                    )
                }
            )
        }
    }
    @Test
    fun deserialise() {
        val test: TestObject = """<Test><version>1.2.1</version></Test>""".toInstance()

        assertThat(test.version.value).isEqualTo("1.2.1")
    }

    @Test
    fun nullValue() {
        val test: TestObject = """<Test></Test>""".toInstance()

        assertThat(test.version).isNotNull()
        assertThat(test.version.value).isEqualTo("")
    }

    @Test
    fun multipleValues() {
        val test: TestMultipleVersion = """<Test><version>1.2.2</version><version>1.2.3</version></Test>""".toInstance()

        assertThat(test.versions).hasSize(2)
        assertThat(test.versions[0].value).isEqualTo("1.2.2")
        assertThat(test.versions[1].value).isEqualTo("1.2.3")
    }

    @Test
    fun multipleVersions() {
        val test: TestMultipleVersionWithWrapper = """<Test><versions><version>1.2.4</version><version>1.2.5</version></versions></Test>""".toInstance()

        assertThat(test.versions).hasSize(2)
        assertThat(test.versions[0].value).isEqualTo("1.2.4")
        assertThat(test.versions[1].value).isEqualTo("1.2.5")
    }
}

data class TestObject(
    @JsonProperty(value = "version")
    val version: Version = Version.NO_VERSION
)

data class TestMultipleVersion(
    @JsonProperty(value = "version")
    @JacksonXmlElementWrapper(useWrapping = false)
    val versions: List<Version> = emptyList()
)

data class TestMultipleVersionWithWrapper(
    @JsonProperty(value = "versions")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "versions")
    val versions: List<Version> = emptyList()
)
