package com.github.squirrelgrip.extension.yaml

import com.github.squirrelgrip.Sample
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class YamlExtensionTest {
    @Test
    fun `toYaml`() {
        val yaml = Sample().toYaml()
        assertThat(yaml).isEqualTo(
            "---\n" +
                "v: 0\n" +
                "s: \"A Simple String\"\n" +
                "m:\n" +
                "  a: \"AAA\"\n" +
                "l:\n" +
                "- \"1\"\n" +
                "- \"AAA\"\n"
        )
        val actual1 = yaml.toInstance<Sample>()
        assertThat(actual1).isEqualTo(Sample())
    }
}
