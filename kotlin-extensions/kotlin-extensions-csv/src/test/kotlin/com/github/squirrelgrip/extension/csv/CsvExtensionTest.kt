package com.github.squirrelgrip.extension.csv

import org.assertj.core.api.AssertionsForInterfaceTypes.assertThat
import org.junit.jupiter.api.Test

class CsvExtensionTest {
    @Test
    fun `toCsv`() {
        assertThat(Sample().toCsv()).isEqualTo("""
            l,s,v
            1;AAA,"A Simple String",0
            
            """.trimIndent())
        assertThat("""
            l,s,v
            1;AAA,"A Simple String",0
            
            """.trimIndent().toInstance<Sample>()).isEqualTo(Sample())
    }

    @Test
    fun `toCsv List`() {
        assertThat(listOf(Sample(), Sample()).toCsv<Sample>()).isEqualTo("""
            l,s,v
            1;AAA,"A Simple String",0
            1;AAA,"A Simple String",0
    
            """.trimIndent())
    }
}
