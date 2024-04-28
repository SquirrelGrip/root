package com.github.squirrelgrip.extension.collection

class CollectionHandler<T, U>(
    expression: String?,
    private val source: Collection<T>,
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

    private inline fun evaluate(predicate: (U) -> Boolean, it: T): Boolean =
        predicate.invoke(transform.invoke(it))

    fun all(): Boolean = predicate?.let { predicate -> source.all { evaluate(predicate, it) } } ?: true
    fun any(): Boolean = predicate?.let { predicate -> source.any { evaluate(predicate, it) } } ?: true
    fun count(): Int = predicate?.let { predicate -> source.count { evaluate(predicate, it) } } ?: source.size
    fun filter(): List<T> = predicate?.let { predicate -> source.filter { evaluate(predicate, it) } } ?: source.toList()
    fun filterNot(): List<T> = predicate?.let { predicate -> source.filterNot { evaluate(predicate, it) } } ?: emptyList()

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

    fun first(): T = predicate?.let { predicate -> source.first { evaluate(predicate, it) } } ?: source.first()

    fun firstOrNull(): T? =
        if (predicate == null) {
            source.firstOrNull()
        } else {
            source.firstOrNull { evaluate(predicate, it) }
        }

    fun indexOfFirst(): Int = predicate?.let { predicate -> source.indexOfFirst { evaluate(predicate, it) } } ?: 0
    fun indexOfLast(): Int = predicate?.let { predicate -> source.indexOfLast { evaluate(predicate, it) } } ?: (source.size - 1)
    fun last(): T = predicate?.let { predicate -> source.last { evaluate(predicate, it) } } ?: source.last()

    fun lastOrNull(): T? =
        if (predicate == null) {
            source.lastOrNull()
        } else {
            source.lastOrNull { evaluate(predicate, it) }
        }

    fun partition(): Pair<List<T>, List<T>> = predicate?.let { predicate -> source.partition { evaluate(predicate, it) } } ?: (source.toList() to emptyList())
}

fun Collection<String>.allByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.all()
fun <T> Collection<T>.allMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).all()
fun <T> Collection<T>.allFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }) = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).all()

fun Collection<String>.anyByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.any()
fun <T> Collection<T>.anyMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).any()
fun <T> Collection<T>.anyFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }) = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).any()

fun Collection<String>.countByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.count()
fun <T> Collection<T>.countMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).count()
fun <T> Collection<T>.countFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }) = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).count()

fun Collection<String>.filterByExpression(expression: String?, aliases: Map<String, String> = emptyMap()): List<String> = CollectionHandler(expression, this, StringCompiler, aliases) { it }.filter()
fun <T> Collection<T>.filterMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }): List<T> = CollectionHandler(expression, this, StringCompiler, aliases, transform).filter()
fun <T> Collection<T>.filterFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }): List<T> = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).filter()

fun Collection<String>.filterNotByExpression(expression: String?, aliases: Map<String, String> = emptyMap()): List<String> = CollectionHandler(expression, this, StringCompiler, aliases) { it }.filterNot()
fun <T> Collection<T>.filterNotMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }): List<T> = CollectionHandler(expression, this, StringCompiler, aliases, transform).filterNot()
fun <T> Collection<T>.filterNotFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }): List<T> = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).filterNot()

fun Collection<String>.findByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.find()
fun <T> Collection<T>.findMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).find()
fun <T> Collection<T>.findFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).find()

fun Collection<String>.findLastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.findLast()
fun <T> Collection<T>.findLastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).findLast()
fun <T> Collection<T>.findLastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).findLast()

fun Collection<String>.firstByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.first()
fun <T> Collection<T>.firstMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).first()
fun <T> Collection<T>.firstFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).first()

fun Collection<String>.firstOrNullByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.firstOrNull()
fun <T> Collection<T>.firstOrNullMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).firstOrNull()
fun <T> Collection<T>.firstOrNullFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).firstOrNull()

fun Collection<String>.indexOfFirstByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.indexOfFirst()
fun <T> Collection<T>.indexOfFirstMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).indexOfFirst()
fun <T> Collection<T>.indexOfFirstFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).indexOfFirst()

fun Collection<String>.indexOfLastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.indexOfLast()
fun <T> Collection<T>.indexOfLastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).indexOfLast()
fun <T> Collection<T>.indexOfLastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).indexOfLast()

fun Collection<String>.lastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.last()
fun <T> Collection<T>.lastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).last()
fun <T> Collection<T>.lastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).last()

fun Collection<String>.lastOrNullByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.lastOrNull()
fun <T> Collection<T>.lastOrNullMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).lastOrNull()
fun <T> Collection<T>.lastOrNullFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) })= CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).lastOrNull()

fun Collection<String>.partitionByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = CollectionHandler(expression, this, StringCompiler, aliases) { it }.partition()
fun <T> Collection<T>.partitionMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = CollectionHandler(expression, this, StringCompiler, aliases, transform).partition()
fun <T> Collection<T>.partitionFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Collection<String> = { setOf(it.toString()) }) = CollectionHandler(expression, this, CollectionStringCompiler, aliases, transform).partition()
