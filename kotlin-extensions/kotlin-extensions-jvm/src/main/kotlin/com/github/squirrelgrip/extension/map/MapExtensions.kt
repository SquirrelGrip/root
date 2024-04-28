package com.github.squirrelgrip.extension.map

/**
 * Reverses a List<Pair<K, V>> into a Map<V, List<K>>
 * @return reversed Map<V, List<K>>
 */
fun <K, V> Iterable<Pair<K, V>>.swap(): Map<V, List<K>> =
    groupBy { it.second }.mapValues { (_, value) ->
        value.map {
            it.first
        }
    }

/**
 * Reverses a map of K to V into a map of V to List<K>
 * @return reversed Map<V, List<K>>
 */
fun <K, V> Map<K, V>.swap(): Map<V, List<K>> =
    toList().swap()

/**
 * Reverses a map of K to Iterable<V> into a map of V to List<K>
 * @return reversed Map<V, List<K>>
 */
fun <K, V> Map<K, Iterable<V>>.swapWithCollection(): Map<V, List<K>> =
    entries.flatMap { e ->
        e.value.map { e.key to it }
    }.swap()

/**
 * Flattens a deep Map into a Map of paths to values
 */
fun Map<String, *>.flatten(): Map<String, *> {
    return toList().flatMap { it.flatten() }.toMap()
}

fun Pair<String, *>.flatten(): List<Pair<String, *>> {
    if (second is Map<*, *>) {
        val map = second as Map<*, *>
        return map.map {
            "$first/${it.key}" to it.value
        }.flatMap { it.flatten() }
    }
    if (second is List<*>) {
        val list = second as List<*>
        return list.indices.map { it ->
            "$first/$it" to list[it]
        }.flatMap { it.flatten() }
    }
    return listOf(this)
}
