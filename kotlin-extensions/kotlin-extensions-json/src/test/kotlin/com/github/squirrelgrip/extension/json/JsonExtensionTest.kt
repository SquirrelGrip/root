package com.github.squirrelgrip.extension.json

import com.flipkart.zjsonpatch.JsonDiff
import com.github.squirrelgrip.Sample
import com.github.squirrelgrip.extension.map.flatten
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class JsonExtensionTest {
    @Test
    fun convertToMap() {
        assertThat(Sample().convertToMap().flatten()).isEqualTo(
            mapOf(
                "m/a" to "AAA",
                "s" to "A Simple String",
                "v" to 0,
                "l/0" to "1",
                "l/1" to "AAA"
            )
        )
        assertThat(Sample().convertToMap()).isEqualTo(
            mapOf(
                "m" to mapOf("a" to "AAA"),
                "s" to "A Simple String",
                "v" to 0,
                "l" to listOf("1", "AAA")
            )
        )
    }

    @Test
    fun toJson() {
        assertThat(Sample().toJson()).isEqualTo("""{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}""")
        assertThat("""{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}""".toInstance<Sample>()).isEqualTo(
            Sample()
        )
    }

    @Test
    fun toInstanceList() {
        assertThat(
            listOf(
                Sample(),
                Sample()
            ).toJson()
        ).isEqualTo("""[{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]},{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}]""")
        assertThat("""[{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]},{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}]""".toInstanceList<Sample>()).isEqualTo(
            listOf(Sample(), Sample())
        )
    }

    @Test
    fun `exception to json`() {
        val diff = JsonDiff.asJson(
            Exception("Message").toJson().toJsonNode(),
            """{"cause":null,"message":"Message","localizedMessage":"Message","suppressed":[]}""".toJsonNode()
        )
        assertThat(diff).isEmpty()
        val exception =
            """{"cause":null,"message":"Message","localizedMessage":"Message","suppressed":[]}""".toInstance<Exception>()
        assertThat(exception.message).isEqualTo(Exception("Message").message)
        assertThat(exception.cause).isEqualTo(Exception("Message").cause)
        assertThat(exception.localizedMessage).isEqualTo(Exception("Message").localizedMessage)
    }

    @Test
    fun `write Instant`() {
        val now = Instant.now()
        assertThat(now.toJson()).isEqualTo(""""$now"""")
    }

    @Test
    fun isJson() {
        assertThat("""{}""".isJson()).isTrue()
        assertThat("""[]""".isJson()).isTrue()
        assertThat("""""".isJson()).isTrue()
        assertThat("""{"a":1}""".isJson()).isTrue()
        assertThat("""abcd""".isJson()).isFalse()

        assertThat("""{}""".byteInputStream().isJson()).isTrue()
        assertThat("""[]""".byteInputStream().isJson()).isTrue()
        assertThat("""""".byteInputStream().isJson()).isTrue()
        assertThat("""{"a":1}""".byteInputStream().isJson()).isTrue()
        assertThat("""abcd""".byteInputStream().isJson()).isFalse()
    }
}
