package com.github.squirrelgrip.extension.file

import com.github.squirrelgrip.extension.io.toPrintWriter
import java.io.*
import java.nio.file.Path

/**
 * Creates a PrintWriter for the given File
 */
fun File.toPrintWriter() = this.toWriter().toPrintWriter()

/**
 * Creates a FileWriter for the given File
 */
fun File.toWriter() = FileWriter(this)

/**
 * Creates a FileOutputStream for the given File
 */
fun File.toOutputStream() = FileOutputStream(this)

/**
 * Creates a FileReader for the given File
 */
fun File.toReader() = FileReader(this)

/**
 * Creates a FileInputStream for the given File
 */
fun File.toInputStream() = FileInputStream(this)

/**
 * Created a File for a given String
 */
fun String.toFile() = File(this)
