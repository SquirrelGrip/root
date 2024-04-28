package com.github.squirrelgrip.util

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

@Suppress("UNCHECKED_CAST")
class InitOnceProperty<T> : ReadWriteProperty<Any, T> {

    private object EMPTY

    private var value: Any? = EMPTY

    override fun getValue(thisRef: Any, property: KProperty<*>): T {
        if (value == EMPTY) {
            throw IllegalStateException("Value isn't initialized")
        }
        return value as T
    }

    override fun setValue(thisRef: Any, property: KProperty<*>, value: T) {
        if (this.value != EMPTY) {
            throw IllegalStateException("Value is initialized")
        }
        this.value = value
    }
}

inline fun <reified T> initOnce(): ReadWriteProperty<Any, T> = InitOnceProperty()
