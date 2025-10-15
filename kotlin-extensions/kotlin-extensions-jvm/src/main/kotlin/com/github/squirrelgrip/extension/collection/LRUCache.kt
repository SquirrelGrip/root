package com.github.squirrelgrip.extension.collection

class LRUCache<K, V>(capacity: Int = 1000) : Map<K, V> {
    private val cache: LinkedHashMap<K, V> =
        object : LinkedHashMap<K, V>(capacity, 0.75f, true) {
            override fun removeEldestEntry(eldest: MutableMap.MutableEntry<K, V>?): Boolean {
                return size > capacity
            }
        }

    fun computeIfAbsent(
        key: K,
        function: (K) -> V
    ): V = cache.computeIfAbsent(key, function)

    override val size: Int
        get() = cache.size
    override val keys: Set<K>
        get() = cache.keys
    override val values: Collection<V>
        get() = cache.values
    override val entries: Set<Map.Entry<K, V>>
        get() = cache.entries

    override fun isEmpty(): Boolean = cache.isEmpty()

    override fun containsKey(key: K): Boolean = cache.containsKey(key)

    override fun containsValue(value: V): Boolean = cache.containsValue(value)

    override operator fun get(key: K): V? = cache[key]
}
