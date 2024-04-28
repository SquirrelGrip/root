package com.github.squirrelgrip.extension.collection

class ArrayHandler<T, U>(
    expression: String?,
    private val source: Array<T>,
    private val compiler: Compiler<U>,
    private val aliases: Map<String, String> = emptyMap(),
    private val transform: (T) -> U
) {
    private val predicate: ((U) -> Boolean)? =
        expression?.let {
            aliases.asSequence()
                .fold(it) { expression, (variable, value) ->
                    expression.replace(variable, "(${value})")
                }
        }?.let {
            compiler.compile(it)
        }

    private fun evaluate(
        predicate: (U) -> Boolean,
        item: T
    ): Boolean =
        predicate.invoke(transform.invoke(item))

    fun all(): Boolean =
        predicate?.let { predicate -> source.all { evaluate(predicate, it) } } ?: true

    fun any(): Boolean =
        predicate?.let { predicate -> source.any { evaluate(predicate, it) } } ?: true

    fun count(): Int =
        predicate?.let { predicate -> source.count { evaluate(predicate, it) } } ?: source.size

    fun filter(): List<T> =
        predicate?.let { predicate -> source.filter { evaluate(predicate, it) } } ?: source.toList()

    fun filterNot(): List<T> =
        predicate?.let { predicate -> source.filterNot { evaluate(predicate, it) } } ?: emptyList()

    fun find(): T? =
        if (predicate == null) {
            source.find { true }
        } else {
            source.find {
                evaluate(predicate, it)
            }
        }

    fun findLast(): T? =
        if (predicate == null) {
            source.lastOrNull()
        } else {
            source.findLast { evaluate(predicate, it) }
        }

    fun first(): T =
        predicate?.let { predicate -> source.first { evaluate(predicate, it) } } ?: source.first()

    fun firstOrNull(): T? =
        if (predicate == null) {
            source.firstOrNull()
        } else {
            source.firstOrNull { evaluate(predicate, it) }
        }

    fun indexOfFirst(): Int =
        predicate?.let { predicate -> source.indexOfFirst { evaluate(predicate, it) } } ?: 0

    fun indexOfLast(): Int =
        predicate?.let { predicate -> source.indexOfLast { evaluate(predicate, it) } } ?: (source.size - 1)

    fun last(): T =
        predicate?.let { predicate -> source.last { evaluate(predicate, it) } } ?: source.last()

    fun lastOrNull(): T? =
        if (predicate == null) {
            source.lastOrNull()
        } else {
            source.lastOrNull { evaluate(predicate, it) }
        }

    fun partition(): Pair<List<T>, List<T>> =
        predicate?.let { predicate -> source.partition { evaluate(predicate, it) } } ?: (source.toList() to emptyList())
}

fun Array<String>.allByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): Boolean =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.all()

fun <T> Array<T>.allMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Boolean =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).all()

fun <T> Array<T>.allFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
) = ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).all()

fun Array<String>.anyByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): Boolean =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.any()

fun <T> Array<T>.anyMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Boolean =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).any()

fun <T> Array<T>.anyFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): Boolean =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).any()

fun Array<String>.countByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): Int =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.count()

fun <T> Array<T>.countMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Int =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).count()

fun <T> Array<T>.countFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): Int =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).count()

fun Array<String>.filterByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): List<String> =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.filter()

fun <T> Array<T>.filterMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): List<T> =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).filter()

fun <T> Array<T>.filterFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): List<T> =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).filter()

fun Array<String>.filterNotByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): List<String> =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.filterNot()

fun <T> Array<T>.filterNotMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): List<T> =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).filterNot()

fun <T> Array<T>.filterNotFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): List<T> =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).filterNot()

fun Array<String>.findByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): String? =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.find()

fun <T> Array<T>.findMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): T? =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).find()

fun <T> Array<T>.findFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T? =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).find()

fun Array<String>.findLastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.findLast()

fun <T> Array<T>.findLastMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
) = ArrayHandler(expression, this, StringCompiler, aliases, transform).findLast()

fun <T> Array<T>.findLastFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T? =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).findLast()

fun Array<String>.firstByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): String =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.first()

fun <T> Array<T>.firstMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): T =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).first()

fun <T> Array<T>.firstFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).first()

fun Array<String>.firstOrNullByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
) =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.firstOrNull()

fun <T> Array<T>.firstOrNullMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): T? =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).firstOrNull()

fun <T> Array<T>.firstOrNullFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T? =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).firstOrNull()

fun Array<String>.indexOfFirstByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
) =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.indexOfFirst()

fun <T> Array<T>.indexOfFirstMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Int =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).indexOfFirst()

fun <T> Array<T>.indexOfFirstFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): Int =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).indexOfFirst()

fun Array<String>.indexOfLastByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): Int =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.indexOfLast()

fun <T> Array<T>.indexOfLastMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Int =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).indexOfLast()

fun <T> Array<T>.indexOfLastFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): Int =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).indexOfLast()

fun Array<String>.lastByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): String =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.last()

fun <T> Array<T>.lastMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): T =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).last()

fun <T> Array<T>.lastFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).last()

fun Array<String>.lastOrNullByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.lastOrNull()

fun <T> Array<T>.lastOrNullMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
) =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).lastOrNull()

fun <T> Array<T>.lastOrNullFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): T? =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).lastOrNull()

fun Array<String>.partitionByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap()
): Pair<List<String>, List<String>> =
    ArrayHandler(expression, this, StringCompiler, aliases) { it }.partition()

fun <T> Array<T>.partitionMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> String = { it.toString() }
): Pair<List<T>, List<T>> =
    ArrayHandler(expression, this, StringCompiler, aliases, transform).partition()

fun <T> Array<T>.partitionFlatMapByExpression(
    expression: String?,
    aliases: Map<String, String> = emptyMap(),
    transform: (T) -> Collection<String> = { setOf(it.toString()) }
): Pair<List<T>, List<T>> =
    ArrayHandler(expression, this, CollectionStringCompiler, aliases, transform).partition()
