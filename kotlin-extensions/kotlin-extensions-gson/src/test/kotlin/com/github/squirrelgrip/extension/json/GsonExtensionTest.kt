package com.github.squirrelgrip.extension.json

import com.github.squirrelgrip.Sample
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import java.time.Instant

class GsonExtensionTest {
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
        ).isEqualTo(
            """[{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]},{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}]"""
        )
        assertThat(
            """[{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]},{"v":0,"s":"A Simple String","m":{"a":"AAA"},"l":["1","AAA"]}]""".toInstanceList<Sample>()
        ).isEqualTo(
            listOf(Sample(), Sample())
        )
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
        assertThat("""abcd""".isJson()).isTrue()

        assertThat("""{}""".byteInputStream().isJson()).isTrue()
        assertThat("""[]""".byteInputStream().isJson()).isTrue()
        assertThat("""""".byteInputStream().isJson()).isTrue()
        assertThat("""{"a":1}""".byteInputStream().isJson()).isTrue()
        assertThat("""abcd""".byteInputStream().isJson()).isTrue()
    }
}
