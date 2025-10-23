package com.github.squirrelgrip.extension.jackson

import com.fasterxml.jackson.core.JsonParser

class JsonSequence<T>(
    private val jsonParser: JsonParser,
    private val type: Class<T>
) : Sequence<T> {
    override fun iterator(): Iterator<T> = JsonIterator(jsonParser, type)
}
