package com.github.squirrelgrip.extension.gson

import com.github.squirrelgrip.extension.json.GsonEngine
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.Reader
import java.io.StringReader

class GsonIterator<T>(
    private val jsonParser: JsonReader,
    private val type: Class<T>
) : Iterator<T> {
    constructor(json: String, type: Class<T>) : this(JsonReader(StringReader(json)), type)
    constructor(reader: Reader, type: Class<T>) : this(JsonReader(reader), type)

    init {
        if (jsonParser.peek() != JsonToken.BEGIN_ARRAY) {
            throw IllegalArgumentException("json must be an array")
        }
        jsonParser.beginArray()
    }

    private var nextValue: T? = doInternalNext()

    override fun hasNext(): Boolean = nextValue != null

    override fun next(): T =
        nextValue!!.apply {
            nextValue = doInternalNext()
        }

    private fun doInternalNext(): T? =
        if (jsonParser.hasNext() && jsonParser.peek() != JsonToken.END_ARRAY) {
            GsonEngine.get().fromJson(jsonParser, type)
        } else {
            if (jsonParser.peek() == JsonToken.END_ARRAY) {
                jsonParser.endArray()
            }
            null
        }
}
