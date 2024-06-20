package com.github.squirrelgrip.extension.excel

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ExcelExtensionsTest {
    @Test
    fun loadWorkbook() {
        val workbook = javaClass.classLoader.getResourceAsStream("ISIN.xls")!!.toWorkbook()
        assertThat(workbook.numberOfSheets).isEqualTo(1)
    }

    @Test
    fun loadSheet() {
        val sheet = javaClass.classLoader.getResourceAsStream("ISIN.xls")!!.getSheetAt(0)
        assertThat(sheet.physicalNumberOfRows).isEqualTo(15685)
    }

}
