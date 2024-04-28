package com.github.squirrelgrip.extension.io

import java.io.*
import java.nio.charset.Charset

fun Reader.toBufferedReader(): BufferedReader =
    BufferedReader(this)

fun Writer.toBufferedWriter(): BufferedWriter =
    BufferedWriter(this)

fun Writer.toPrintWriter(): PrintWriter =
    PrintWriter(this)

fun OutputStream.toWriter(charset: Charset = Charset.defaultCharset()): Writer =
    OutputStreamWriter(this, charset)

fun InputStream.toReader(charset: Charset = Charset.defaultCharset()): Reader =
    InputStreamReader(this, charset)
