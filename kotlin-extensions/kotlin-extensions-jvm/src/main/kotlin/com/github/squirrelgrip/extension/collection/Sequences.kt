package com.github.squirrelgrip.extension.collection

class SequenceHandler<T, U>(
    expression: String?,
    private val source: Sequence<T>,
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
    fun count(): Int = predicate?.let { predicate -> source.count { evaluate(predicate, it) } } ?: source.count()
    fun filter(): Sequence<T> = predicate?.let { predicate -> source.filter { evaluate(predicate, it) } } ?: source
    fun filterNot(): Sequence<T> = predicate?.let { predicate -> source.filterNot { evaluate(predicate, it) } } ?: emptySequence()

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
    fun indexOfLast(): Int = predicate?.let { predicate -> source.indexOfLast { evaluate(predicate, it) } } ?: (source.indexOfLast { true })
    fun last(): T = predicate?.let { predicate -> source.last { evaluate(predicate, it) } } ?: source.last()

    fun lastOrNull(): T? =
        if (predicate == null) {
            source.lastOrNull()
        } else {
            source.lastOrNull { evaluate(predicate, it) }
        }

    fun partition(): Pair<List<T>, List<T>> = predicate?.let { predicate -> source.partition { evaluate(predicate, it) } } ?: (source.toList() to emptyList())
}

fun Sequence<String>.allByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.all()
fun <T> Sequence<T>.allMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).all()
fun <T> Sequence<T>.allFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }) = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).all()

fun Sequence<String>.anyByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.any()
fun <T> Sequence<T>.anyMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).any()
fun <T> Sequence<T>.anyFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }) = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).any()

fun Sequence<String>.countByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.count()
fun <T> Sequence<T>.countMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).count()
fun <T> Sequence<T>.countFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }) = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).count()

fun Sequence<String>.filterByExpression(expression: String?, aliases: Map<String, String> = emptyMap()): Sequence<String> = SequenceHandler(expression, this, StringCompiler, aliases) { it }.filter()
fun <T> Sequence<T>.filterMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }): Sequence<T> = SequenceHandler(expression, this, StringCompiler, aliases, transform).filter()
fun <T> Sequence<T>.filterFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }): Sequence<T> = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).filter()

fun Sequence<String>.filterNotByExpression(expression: String?, aliases: Map<String, String> = emptyMap()): Sequence<String> = SequenceHandler(expression, this, StringCompiler, aliases) { it }.filterNot()
fun <T> Sequence<T>.filterNotMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }): Sequence<T> = SequenceHandler(expression, this, StringCompiler, aliases, transform).filterNot()
fun <T> Sequence<T>.filterNotFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }): Sequence<T> = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).filterNot()

fun Sequence<String>.findByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.find()
fun <T> Sequence<T>.findMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).find()
fun <T> Sequence<T>.findFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).find()

fun Sequence<String>.findLastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.findLast()
fun <T> Sequence<T>.findLastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).findLast()
fun <T> Sequence<T>.findLastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).findLast()

fun Sequence<String>.firstByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.first()
fun <T> Sequence<T>.firstMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).first()
fun <T> Sequence<T>.firstFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).first()

fun Sequence<String>.firstOrNullByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.firstOrNull()
fun <T> Sequence<T>.firstOrNullMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).firstOrNull()
fun <T> Sequence<T>.firstOrNullFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).firstOrNull()

fun Sequence<String>.indexOfFirstByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.indexOfFirst()
fun <T> Sequence<T>.indexOfFirstMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).indexOfFirst()
fun <T> Sequence<T>.indexOfFirstFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).indexOfFirst()

fun Sequence<String>.indexOfLastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.indexOfLast()
fun <T> Sequence<T>.indexOfLastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).indexOfLast()
fun <T> Sequence<T>.indexOfLastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).indexOfLast()

fun Sequence<String>.lastByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.last()
fun <T> Sequence<T>.lastMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).last()
fun <T> Sequence<T>.lastFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).last()

fun Sequence<String>.lastOrNullByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.lastOrNull()
fun <T> Sequence<T>.lastOrNullMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).lastOrNull()
fun <T> Sequence<T>.lastOrNullFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) })= SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).lastOrNull()

fun Sequence<String>.partitionByExpression(expression: String?, aliases: Map<String, String> = emptyMap()) = SequenceHandler(expression, this, StringCompiler, aliases) { it }.partition()
fun <T> Sequence<T>.partitionMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> String = { it.toString() }) = SequenceHandler(expression, this, StringCompiler, aliases, transform).partition()
fun <T> Sequence<T>.partitionFlatMapByExpression(expression: String?, aliases: Map<String, String> = emptyMap(), transform: (T) -> Sequence<String> = { sequenceOf(it.toString()) }) = SequenceHandler(expression, this, SequenceStringCompiler, aliases, transform).partition()
