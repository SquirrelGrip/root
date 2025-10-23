package com.github.squirrelgrip.extension.jackson

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.core.JsonToken

class JsonIterator<T>(
    private val jsonParser: JsonParser,
    private val type: Class<T>
) : Iterator<T> {
    init {
        if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
            throw Exception("json must be an array")
        }
    }

    private var nextValue: T? = doInternalNext()

    override fun hasNext(): Boolean = nextValue != null

    override fun next(): T =
        nextValue!!.apply {
            nextValue = doInternalNext()
        }

    private fun doInternalNext(): T? =
        if (jsonParser.nextToken() != JsonToken.END_ARRAY) {
            jsonParser.readValueAs(type)
        } else {
            jsonParser.close()
            null
        }
}
