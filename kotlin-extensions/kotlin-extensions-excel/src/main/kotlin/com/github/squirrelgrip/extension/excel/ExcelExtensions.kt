package com.github.squirrelgrip.extension.excel

import com.github.squirrelgrip.extension.file.toInputStream
import org.apache.poi.ss.usermodel.*
import java.io.ByteArrayInputStream
import java.io.File
import java.io.InputStream
import java.nio.file.Path

fun File.toWorkbook(): Workbook = this.toInputStream().toWorkbook()
fun Path.toWorkbook(): Workbook = this.toInputStream().toWorkbook()
fun InputStream.toWorkbook(): Workbook = WorkbookFactory.create(this)
fun ByteArray.toWorkbook(): Workbook = ByteArrayInputStream(this).toWorkbook()

fun File.getSheetAt(index: Int): Sheet = this.getSheetAt(index)
fun Path.getSheetAt(index: Int): Sheet = this.getSheetAt(index)
fun InputStream.getSheetAt(index: Int): Sheet = this.toWorkbook().getSheetAt(index)
fun ByteArray.getSheetAt(index: Int): Sheet = ByteArrayInputStream(this).getSheetAt(index)

fun File.getSheet(name: String): Sheet = this.getSheet(name)
fun Path.getSheet(name: String): Sheet = this.getSheet(name)
fun InputStream.getSheet(name: String): Sheet = this.toWorkbook().getSheet(name)
fun ByteArray.getSheet(name: String): Sheet = ByteArrayInputStream(this).getSheet(name)

fun Workbook.dump() {
    for (i in 0..numberOfSheets - 1) {
        getSheetAt(i).dump()
    }
}

fun Sheet.dump() {
    rowIterator().asSequence().forEach {
        println("${it.rowNum} - ${it.toCsv()}")
    }
}

fun Row.toCsv() =
    asSequence().map {
        when (it.cellType) {
            CellType.STRING -> it.stringCellValue
            CellType.NUMERIC -> it.numericCellValue
            CellType.BLANK -> ""
            CellType.FORMULA -> it.cellFormula
            CellType.BOOLEAN -> it.booleanCellValue.toString()
            CellType.ERROR -> "ERROR"
            else -> "UNKNOWN"
        }
    }.joinToString(",") {
        "\"$it\""
    }
