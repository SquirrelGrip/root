package com.github.squirrelgrip.extension.collection

import java.util.*

inline fun <reified E : Enum<E>> Collection<E>?.toEnumSet(): EnumSet<E> =
    if (this == null) {
        EnumSet.allOf(E::class.java)
    } else if (this.isEmpty()) {
        EnumSet.noneOf(E::class.java)
    } else {
        EnumSet.copyOf(this)
    }

inline fun <reified E : Enum<E>> String?.allByExpression(aliases: Map<String, String> = emptyMap()): Boolean = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.all()
inline fun <reified E : Enum<E>> String?.anyByExpression(aliases: Map<String, String> = emptyMap()): Boolean = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.any()
inline fun <reified E : Enum<E>> String?.countByExpression(aliases: Map<String, String> = emptyMap()): Int = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.count()
inline fun <reified E : Enum<E>> String?.filterByExpression(aliases: Map<String, String> = emptyMap()): List<E> = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.filter()
inline fun <reified E : Enum<E>> String?.filterNotByExpression(aliases: Map<String, String> = emptyMap()): List<E> = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.filterNot()
inline fun <reified E : Enum<E>> String?.findByExpression(aliases: Map<String, String> = emptyMap()): E? = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.find()
inline fun <reified E : Enum<E>> String?.findLastByExpression(aliases: Map<String, String> = emptyMap()): E? = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.findLast()
inline fun <reified E : Enum<E>> String?.firstByExpression(aliases: Map<String, String> = emptyMap()): E = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.first()
inline fun <reified E : Enum<E>> String?.firstOrNullByExpression(aliases: Map<String, String> = emptyMap()): E? = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.firstOrNull()
inline fun <reified E : Enum<E>> String?.indexOfFirstByExpression(aliases: Map<String, String> = emptyMap()): Int = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.indexOfFirst()
inline fun <reified E : Enum<E>> String?.indexOfLastByExpression(aliases: Map<String, String> = emptyMap()): Int = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.indexOfLast()
inline fun <reified E : Enum<E>> String?.lastByExpression(aliases: Map<String, String> = emptyMap()): E = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.last()
inline fun <reified E : Enum<E>> String?.lastOrNullByExpression(aliases: Map<String, String> = emptyMap()): E? = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.lastOrNull()
inline fun <reified E : Enum<E>> String?.partitionByExpression(aliases: Map<String, String> = emptyMap()): Pair<List<E>, List<E>> = ArrayHandler(this, enumValues<E>(), StringCompiler, aliases) { it.name }.partition()
