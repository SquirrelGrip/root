package com.github.squirrelgrip.dependency.serial

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper
import com.github.squirrelgrip.dependency.model.Version
import com.github.squirrelgrip.extension.xml.toInstance
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class VersionDeserializerTest {
    @Test
    fun deserialise() {
        val test: TestObject = """<Test><major>1.2.3</major></Test>""".toInstance()

        assertThat(test.major.value).isEqualTo("1.2.3")
    }

    @Test
    fun nullValue() {
        val test: TestObject = """<Test></Test>""".toInstance()

        assertThat(test.major).isNotNull()
        assertThat(test.major.value).isEqualTo("")
    }

    @Test
    fun multipleValues() {
        val test: TestMultipleVersion = """<Test><major>1.2.3</major><major>1.2.4</major></Test>""".toInstance()

        assertThat(test.major).hasSize(2)
        assertThat(test.major[0].value).isEqualTo("1.2.3")
        assertThat(test.major[1].value).isEqualTo("1.2.4")
    }

    @Test
    fun multipleMajors() {
        val test: TestMultipleMajor = """<Test><majors><major>1.2.3</major><major>1.2.4</major></majors></Test>""".toInstance()

        assertThat(test.majors).hasSize(2)
        assertThat(test.majors[0].value).isEqualTo("1.2.3")
        assertThat(test.majors[1].value).isEqualTo("1.2.4")
    }
}

data class TestObject(
    @JsonProperty(value = "major")
    val major: Version = Version.NO_VERSION
)

data class TestMultipleVersion(
    @JsonProperty(value = "major")
    @JacksonXmlElementWrapper(useWrapping = false)
    val major: List<Version> = emptyList()
)

data class TestMultipleMajor(
    @JsonProperty(value = "majors")
    @JacksonXmlElementWrapper(useWrapping = true, localName = "majors")
    val majors: List<Version> = emptyList()
)