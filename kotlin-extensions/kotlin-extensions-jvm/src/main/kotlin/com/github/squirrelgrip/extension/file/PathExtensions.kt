package com.github.squirrelgrip.extension.file

import com.github.squirrelgrip.extension.io.toPrintWriter
import java.io.*
import java.nio.file.Path

/**
 * Creates a PrintWriter for the given File
 */
fun Path.toPrintWriter() = this.toWriter().toPrintWriter()

/**
 * Creates a FileWriter for the given File
 */
fun Path.toWriter() = FileWriter(this.toFile())

/**
 * Creates a FileOutputStream for the given File
 */
fun Path.toOutputStream() = FileOutputStream(this.toFile())

/**
 * Creates a FileReader for the given File
 */
fun Path.toReader() = FileReader(this.toFile())

/**
 * Creates a FileInputStream for the given File
 */
fun Path.toInputStream() = FileInputStream(this.toFile())

/**
 * Created a Path for a given String
 */
fun String.toPath() = Path.of(this)
