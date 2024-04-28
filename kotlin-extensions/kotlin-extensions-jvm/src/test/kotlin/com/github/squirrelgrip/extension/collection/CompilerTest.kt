package com.github.squirrelgrip.extension.collection

import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.util.*
import java.util.stream.Stream

internal class CompilerTest {
    companion object {
        val testSubject = CollectionStringCompiler

        val setOfA = setOf("A")
        val setOfB = setOf("B")
        val setOfC = setOf("C")
        val setOfAAndB = setOf("A", "B")
        val setOfAA = setOf("AA")
        val setOfAB = setOf("AB")
        val setOfAC = setOf("AC")
        val setOfAAndAB = setOf("AA", "AB")
        val setOfEmpty = setOf("")

        val collection = listOf(
            1 to setOfA,
            2 to setOfB,
            3 to setOfC,
            4 to setOfAAndB,
            5 to setOfAA,
            6 to setOfAB,
            7 to setOfAC,
            8 to setOfAAndAB,
            9 to setOfEmpty,
        )


        data class TestClass(val value: String)

        private val OBJECT_A = TestClass("A")
        private val OBJECT_B = TestClass("B")
        private val OBJECT_C = TestClass("C")

        val objectList = listOf(OBJECT_A, OBJECT_B, OBJECT_C)
        val objectArray = arrayOf(OBJECT_A, OBJECT_B, OBJECT_C)
        val objectSequence = sequenceOf(OBJECT_A, OBJECT_B, OBJECT_C)

        enum class TestEnum {
            A, B, C
        }

        val stringList = listOf("A", "B", "C")
        val stringArray = arrayOf("A", "B", "C")
        val stringSequence = sequenceOf("A", "B", "C")

        @JvmStatic
        fun compile(): Stream<Arguments> =
            Stream.of(
                Arguments.of("A", "B", "C"),
                Arguments.of("_A", "_B", "_C"),
                Arguments.of("A_A", "B_B", "C_C"),
                Arguments.of("A-A", "B-B", "C-C"),
                Arguments.of("\"A\"", "\"B\"", "\"C\""),
                Arguments.of("\"\\\"A\"", "\"\\\"B\"", "\"\\\"C\""),
                Arguments.of("\"\\\" A\"", "\"\\\" B\"", "\"\\\" C\""),
                Arguments.of("\\\"", "\\(", "\\)"),
                Arguments.of("\\!", "\\&", "\\|"),
                Arguments.of("\\n", "\\r", "\\t"),
                Arguments.of("\\\\A", "\\\\B", "\\\\C"),
                Arguments.of("\\(A", "\\(B", "\\(C"),
                Arguments.of("\\)A", "\\)B", "\\)C"),
                Arguments.of("\\!A", "\\!B", "\\!C"),
                Arguments.of("\\|A", "\\|B", "\\|C"),
                Arguments.of("\\&A", "\\&B", "\\&C"),
            )

        val reservedOperators = listOf("(", ")", "?", "*", "~")
        val unaryOperators = listOf("!")
        val binaryOperations = listOf("&", "|", "^", "=>")
        val escaped = listOf("\"", "\\")
        val validChars = listOf(
            "A",
            "@",
            "#",
            "$",
            "%",
            "{",
            "}",
            "[",
            "]",
            ":",
            ";",
            ",",
            ".",
            "<",
            "/",
            "+",
            "-",
            "_",
            "`",
            "1",
        )

        @JvmStatic
        fun validExpression(): Stream<Arguments> =
            listOf(
                validChars.map { it },
                validChars.map { " $it" },
                validChars.map { "$it*" },
                validChars.map { "$it\\\\*" },
                validChars.map { " $it " },
                validChars.map { " $it & $it " },
                (reservedOperators + unaryOperators + binaryOperations + escaped).map { "\\$it" },
                (reservedOperators + unaryOperators + binaryOperations + escaped).map { " \\$it" },
                (reservedOperators + unaryOperators + binaryOperations).map { "\"$it\"" },
                (reservedOperators + unaryOperators + binaryOperations).map { "\" $it\" " },
                (reservedOperators + unaryOperators + binaryOperations).map { "\" $it\"" },
                (reservedOperators + unaryOperators + binaryOperations).map { "\\$it" },
                escaped.map { "\"\\$it\"" },
                // A&B
                generateArguments(
                    validChars,
                    validChars,
                    binaryOperations
                ) { first, second, operator -> "$first$operator$second" },
                // A&!B
                generateArguments(
                    validChars,
                    validChars,
                    binaryOperations
                ) { first, second, operator -> "$first$operator!$second" },
                // A\&B eg. Letter, Backslash, And, Operand
                generateArguments(
                    validChars,
                    validChars,
                    (binaryOperations + unaryOperators + reservedOperators)
                ) { first, second, operator -> "$first\\$operator$second" },
                // AA\&B eg. Letter, Backslash, And, Operand
                generateArguments(
                    validChars,
                    validChars,
                    (binaryOperations + unaryOperators + reservedOperators)
                ) { first, second, operator -> "$first$first\\$operator$second" },
                // A\( eg. Letter, Backslash, Operand
                generateArguments(
                    validChars,
                    reservedOperators + unaryOperators + binaryOperations
                ) { first, second -> "$first\\$second" },
                // A\(A eg. Letter, Backslash, Operand, Letter
                generateArguments(
                    validChars,
                    reservedOperators + unaryOperators + binaryOperations
                ) { first, second -> "$first\\$second$first" },
                // "A(" eg. Double Quote, Letter, Operand, Double Quote
                generateArguments(
                    validChars,
                    reservedOperators + unaryOperators + binaryOperations
                ) { first, second -> "\"$first$second\"" },
                // A\" eg. Letter, Backslash, Escaped Char
                generateArguments(validChars, escaped) { first, second -> "$first\\$second" },
                // "A\"" eg. Double Quote, Letter, Backslash, Escaped Char, Double Quote
                generateArguments(validChars, escaped) { first, second -> "\"$first\\$second\"" },
                // A\"&!(A) eg. Letter, Backslash, Escaped Char, And, Not, Open Paren, Letter, Closed Paren
                generateArguments(validChars, escaped) { first, second -> "$first\\$second&!($first)" }
            ).flatten().map {
                Arguments.of(it)
            }.stream()

        @JvmStatic
        fun expressionTestEnum(): Stream<Arguments> =
            listOf(
                Arguments.of("A", mapOf("X" to "A|B"), listOf(TestEnum.A)),

                Arguments.of("(A)", mapOf("X" to "A|B"), listOf(TestEnum.A)),
                Arguments.of("!A", mapOf("X" to "A|B"), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("!(A)", mapOf("X" to "A|B"), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("!A|A", mapOf("X" to "A|B"), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("!A&A", mapOf("X" to "A|B"), emptyList<TestEnum>()),
                Arguments.of("(!A|B)|A", mapOf("X" to "A|B"), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("A=>B", mapOf("X" to "A|B"), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("X", mapOf("X" to "A|B"), listOf(TestEnum.A, TestEnum.B)),
                Arguments.of("!X", mapOf("X" to "A|B"), listOf(TestEnum.C)),
                Arguments.of("ALL", mapOf("ALL" to "A|B|C"), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("!ALL", mapOf("ALL" to "A|B|C"), emptyList<TestEnum>()),
                Arguments.of("", mapOf("ALL" to "A|B|C"), emptyList<TestEnum>()),
                Arguments.of(null, mapOf("ALL" to "A|B|C"), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),

                Arguments.of("A", emptyMap<String, String>(), listOf(TestEnum.A)),
                Arguments.of("(A)", emptyMap<String, String>(), listOf(TestEnum.A)),
                Arguments.of("!A", emptyMap<String, String>(), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("!(A)", emptyMap<String, String>(), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("!A|A", emptyMap<String, String>(), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("!A&A", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of("(!A|B)|A", emptyMap<String, String>(), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("A=>B", emptyMap<String, String>(), listOf(TestEnum.B, TestEnum.C)),
                Arguments.of("X", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of("!X", emptyMap<String, String>(), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
                Arguments.of("ALL", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of(null, emptyMap<String, String>(), listOf(TestEnum.A, TestEnum.B, TestEnum.C)),
            ).stream()

        @JvmStatic
        fun expressionString(): Stream<Arguments> =
            listOf(
                Arguments.of("A", mapOf("X" to "A|B"), listOf("A")),
                Arguments.of("(A)", mapOf("X" to "A|B"), listOf("A")),
                Arguments.of("!A", mapOf("X" to "A|B"), listOf("B", "C")),
                Arguments.of("!(A)", mapOf("X" to "A|B"), listOf("B", "C")),
                Arguments.of("!A|A", mapOf("X" to "A|B"), listOf("A", "B", "C")),
                Arguments.of("!A&A", mapOf("X" to "A|B"), emptyList<String>()),
                Arguments.of("(!A|B)|A", mapOf("X" to "A|B"), listOf("A", "B", "C")),
                Arguments.of("A=>B", mapOf("X" to "A|B"), listOf("B", "C")),
                Arguments.of("X", mapOf("X" to "A|B"), listOf("A", "B")),
                Arguments.of("!X", mapOf("X" to "A|B"), listOf("C")),
                Arguments.of("ALL", mapOf("ALL" to "A|B|C"), listOf("A", "B", "C")),
                Arguments.of("", mapOf("ALL" to "A|B|C"), emptyList<String>()),
                Arguments.of(null, mapOf("ALL" to "A|B|C"), listOf("A", "B", "C")),

                Arguments.of("A", emptyMap<String, String>(), listOf("A")),
                Arguments.of("(A)", emptyMap<String, String>(), listOf("A")),
                Arguments.of("!A", emptyMap<String, String>(), listOf("B", "C")),
                Arguments.of("!(A)", emptyMap<String, String>(), listOf("B", "C")),
                Arguments.of("!A|A", emptyMap<String, String>(), listOf("A", "B", "C")),
                Arguments.of("!A&A", emptyMap<String, String>(), emptyList<String>()),
                Arguments.of("(!A|B)|A", emptyMap<String, String>(), listOf("A", "B", "C")),
                Arguments.of("A=>B", emptyMap<String, String>(), listOf("B", "C")),
                Arguments.of("X", emptyMap<String, String>(), emptyList<String>()),
                Arguments.of("!X", emptyMap<String, String>(), listOf("A", "B", "C")),
                Arguments.of("ALL", emptyMap<String, String>(), emptyList<String>()),
                Arguments.of("", emptyMap<String, String>(), emptyList<String>()),
                Arguments.of(null, emptyMap<String, String>(), listOf("A", "B", "C")),
            ).stream()

        @JvmStatic
        fun expressionObject(): Stream<Arguments> =
            listOf(
                Arguments.of("A", mapOf("X" to "A|B"), listOf(OBJECT_A)),
                Arguments.of("(A)", mapOf("X" to "A|B"), listOf(OBJECT_A)),
                Arguments.of("!A", mapOf("X" to "A|B"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(A)", mapOf("X" to "A|B"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!A|A", mapOf("X" to "A|B"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("!A&A", mapOf("X" to "A|B"), emptyList<TestClass>()),
                Arguments.of("(!A|B)|A", mapOf("X" to "A|B"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>B", mapOf("X" to "A|B"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("X", mapOf("X" to "A|B"), listOf(OBJECT_A, OBJECT_B)),
                Arguments.of("!X", mapOf("X" to "A|B"), listOf(OBJECT_C)),
                Arguments.of("ALL", mapOf("ALL" to "A|B|C"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("", mapOf("ALL" to "A|B|C"), emptyList<TestClass>()),
                Arguments.of(null, mapOf("ALL" to "A|B|C"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),

                Arguments.of("A", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("(A)", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("!A", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(A)", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!A|A", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("!A&A", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("(!A|B)|A", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>B", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("X", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("!X", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("ALL", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of(null, emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
            ).stream()

        @JvmStatic
        fun expressionFlatMapObject(): Stream<Arguments> =
            listOf(
                Arguments.of("A", mapOf("X" to "AA|BB"), listOf(OBJECT_A)),
                Arguments.of("(A)", mapOf("X" to "AA|BB"), listOf(OBJECT_A)),
                Arguments.of("!A", mapOf("X" to "AA|BB"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(A)", mapOf("X" to "AA|BB"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!A|A", mapOf("X" to "AABB"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("!A&A", mapOf("X" to "AA|BB"), emptyList<TestClass>()),
                Arguments.of("(!A|B)|A", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>B", mapOf("X" to "AA|BB"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("X", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B)),
                Arguments.of("!X", mapOf("X" to "AA|BB"), listOf(OBJECT_C)),
                Arguments.of("ALL", mapOf("ALL" to "AA|BB|CC"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("", mapOf("ALL" to "AA|BB|CC"), emptyList<TestClass>()),
                Arguments.of(null, mapOf("ALL" to "AA|BB|CC"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),

                Arguments.of("AA", mapOf("X" to "AA|BB"), listOf(OBJECT_A)),
                Arguments.of("(AA)", mapOf("X" to "AA|BB"), listOf(OBJECT_A)),
                Arguments.of("!AA", mapOf("X" to "AA|BB"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(AA)", mapOf("X" to "AA|BB"), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!AA|AA", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("!AA&AA", mapOf("X" to "AA|BB"), emptyList<TestClass>()),
                Arguments.of("(!AA|BB)|AA", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>AA", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("X", mapOf("X" to "AA|BB"), listOf(OBJECT_A, OBJECT_B)),
                Arguments.of("!X", mapOf("X" to "AA|BB"), listOf(OBJECT_C)),
                Arguments.of("ALL", mapOf("ALL" to "AA|BB|CC"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("", mapOf("ALL" to "AA|BB|CC"), emptyList<TestClass>()),
                Arguments.of(null, mapOf("ALL" to "AA|BB|CC"), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),

                Arguments.of("A", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("(A)", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("!A", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(A)", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!A|A", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("(!A|B)|A", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>B", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("X", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("!X", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("ALL", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of(null, emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),

                Arguments.of("AA", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("(AA)", emptyMap<String, String>(), listOf(OBJECT_A)),
                Arguments.of("!AA", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!(AA)", emptyMap<String, String>(), listOf(OBJECT_B, OBJECT_C)),
                Arguments.of("!AA|AA", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("(!AA|BB)|AA", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("A=>AA", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("X", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("!X", emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
                Arguments.of("ALL", emptyMap<String, String>(), emptyList<TestClass>()),
                Arguments.of("", emptyMap<String, String>(), emptyList<TestEnum>()),
                Arguments.of(null, emptyMap<String, String>(), listOf(OBJECT_A, OBJECT_B, OBJECT_C)),
            ).stream()

        private fun generateArguments(
            list1: List<String>,
            list2: List<String>,
            expression: (first: String, second: String) -> String
        ): List<String> =
            list1.map { first ->
                list2.map { second ->
                    expression.invoke(first, second)
                }
            }.flatten()

        private fun generateArguments(
            list1: List<String>,
            list2: List<String>,
            operators: List<String>,
            expression: (first: String, second: String, operator: String) -> String
        ): List<String> =
            list1.map { first ->
                list2.map { second ->
                    operators.map { operator ->
                        expression.invoke(first, second, operator)
                    }
                }.flatten()
            }.flatten()

        fun assertValues(expression: String, vararg index: Int) {
            val compile = testSubject.compile(expression)
            collection.forEach { pair ->
                assertThat(compile.invoke(pair.second)).apply {
                    if (pair.first in index) {
                        isTrue()
                    } else {
                        isFalse()
                    }
                }
            }
            assertThat(getKeys(expression)).containsExactlyElementsOf(index.toList())
        }

        private fun getKeys(expression: String): List<Int> {
            return collection.filterFlatMapByExpression(expression) {
                it.second
            }.map {
                it.first
            }
        }


    }

    @ParameterizedTest
    @MethodSource
    fun compile(a: String, b: String, c: String) {
        assertThat(filter(a, b, c, escape(a))).containsExactly(a)
        assertThat(filter(a, b, c, escape(b))).containsExactly(b)
        assertThat(filter(a, b, c, escape(c))).containsExactly(c)
        assertThat(filter(a, b, c, "!${escape(a)}")).containsExactly(b, c)
        assertThat(filter(a, b, c, "!${escape(b)}")).containsExactly(a, c)
        assertThat(filter(a, b, c, "!${escape(c)}")).containsExactly(a, b)
        assertThat(filter(a, b, c, "")).isEmpty()
        assertThat(filter(a, b, c, "\"\"")).isEmpty()
        assertThat(filter(a, b, c, null)).containsExactly(a, b, c)
        assertThat(filter(a, b, c, "!(${escape(a)})")).containsExactly(b, c)
        assertThat(filter(a, b, c, "(${escape(a)})")).containsExactly(a)
        assertThat(filter(a, b, c, "(${escape(a)}|${escape(b)})")).containsExactly(a, b)
    }

    @ParameterizedTest
    @MethodSource
    fun validExpression(expression: String) {
        testSubject.compile(expression)
    }

    private fun escape(a: String): String =
        "\"${"([\"\\\\])".toRegex().replace(a, "\\\\$1")}\""

    private fun filter(
        objectA: String,
        objectB: String,
        objectC: String,
        expression: String?
    ): List<String> =
        listOf(objectA, objectB, objectC).filterMapByExpression(expression, emptyMap()) { it }

    @Test
    fun assertValues() {
        assertValues("A", 1, 4)
        assertValues("\"\"", 9)
        assertValues("(A)", 1, 4)
        assertValues("(!A)", 2, 3, 5, 6, 7, 8, 9)
        assertValues("!A", 2, 3, 5, 6, 7, 8, 9)
        assertValues("A|B", 1, 2, 4)
        assertValues("A&B", 4)
        assertValues("A|!B", 1, 3, 4, 5, 6, 7, 8, 9)
        assertValues("A|!B?", 1, 2, 3, 4, 5, 6, 7, 8, 9)
        assertValues("A|!A?", 1, 2, 3, 4, 9)
        assertValues("~AB?~", 1, 4, 6, 8)
        assertValues("~A[^B]~", 5, 7, 8)
        assertValues("A^B", 1, 2)
        assertValues("\"\"", 9)
        assertValues("*", 1, 2, 3, 4, 5, 6, 7, 8, 9)
        assertValues("A|!A", 1, 2, 3, 4, 5, 6, 7, 8, 9)
        assertValues("A|!A*", 1, 2, 3, 4, 9)
        assertValues("A|!A*", 1, 2, 3, 4, 9)
        assertValues("A&!B", 1)
        assertValues("?", 1, 2, 3, 4)
        assertValues("TRUE", 1, 2, 3, 4, 5, 6, 7, 8, 9)
        assertValues("FALSE")
        assertValues("A?", 5, 6, 7, 8)
    }

    @ParameterizedTest
    @MethodSource
    fun expressionTestEnum(expression: String?, alias: Map<String, String>, expected: List<TestEnum>) {
        assertThat(expression.allByExpression<TestEnum>(alias)).isEqualTo(expected.size == TestEnum.entries.size)
        assertThat(expression.anyByExpression<TestEnum>(alias)).isEqualTo(expected.isNotEmpty())
        assertThat(expression.countByExpression<TestEnum>(alias)).isEqualTo(expected.size)
        assertThat(expression.filterByExpression<TestEnum>(alias)).containsExactlyElementsOf(expected)
        assertThat(expression.filterNotByExpression<TestEnum>(alias)).containsExactlyElementsOf(
            EnumSet.complementOf(
                expected.toEnumSet()
            )
        )
        assertThat(expression.findByExpression<TestEnum>(alias)).isEqualTo(expected.firstOrNull())
        assertThat(expression.findLastByExpression<TestEnum>(alias)).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                expression.firstByExpression<TestEnum>(alias)
            }
        } else {
            assertThat(expression.firstByExpression<TestEnum>(alias)).isEqualTo(expected.first())
        }
        assertThat(expression.firstOrNullByExpression<TestEnum>(alias)).isEqualTo(expected.firstOrNull())
        assertThat(expression.indexOfFirstByExpression<TestEnum>(alias)).isEqualTo(TestEnum.entries.indexOf(expected.firstOrNull()))
        assertThat(expression.indexOfLastByExpression<TestEnum>(alias)).isEqualTo(TestEnum.entries.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                expression.lastByExpression<TestEnum>(alias)
            }
        } else {
            assertThat(expression.lastByExpression<TestEnum>(alias)).isEqualTo(expected.last())
        }
        assertThat(expression.lastOrNullByExpression<TestEnum>(alias)).isEqualTo(expected.lastOrNull())
        assertThat(expression.partitionByExpression<TestEnum>(alias)).isEqualTo(
            expected to EnumSet.complementOf(
                expected.toEnumSet()
            ).toList()
        )
    }

    @ParameterizedTest
    @MethodSource("expressionString")
    fun expressionStringList(expression: String?, alias: Map<String, String>, expected: List<String>) {
        assertThat(stringList.allByExpression(expression, alias)).isEqualTo(expected.size == stringList.size)
        assertThat(stringList.anyByExpression(expression, alias)).isEqualTo(expected.isNotEmpty())
        assertThat(stringList.countByExpression(expression, alias)).isEqualTo(expected.size)
        assertThat(stringList.filterByExpression(expression, alias)).containsExactlyElementsOf(expected)
        assertThat(
            stringList.filterNotByExpression(
                expression,
                alias
            )
        ).containsExactlyElementsOf(stringList - expected.toSet())
        assertThat(stringList.findByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(stringList.findLastByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringList.firstByExpression(expression, alias)
            }
        } else {
            assertThat(stringList.firstByExpression(expression, alias)).isEqualTo(expected.first())
        }
        assertThat(stringList.firstOrNullByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(
            stringList.indexOfFirstByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringList.indexOf(expected.firstOrNull()))
        assertThat(
            stringList.indexOfLastByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringList.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringList.lastByExpression(expression, alias)
            }
        } else {
            assertThat(stringList.lastByExpression(expression, alias)).isEqualTo(expected.last())
        }
        assertThat(stringList.lastOrNullByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        assertThat(
            stringList.partitionByExpression(
                expression,
                alias
            )
        ).isEqualTo(expected to (stringList - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionString")
    fun expressionStringSequence(expression: String?, alias: Map<String, String>, expected: List<String>) {
        assertThat(
            stringSequence.allByExpression(
                expression,
                alias
            )
        ).isEqualTo(expected.size == stringSequence.toList().size)
        assertThat(stringSequence.anyByExpression(expression, alias)).isEqualTo(expected.isNotEmpty())
        assertThat(stringSequence.countByExpression(expression, alias)).isEqualTo(expected.size)
        assertThat(stringSequence.filterByExpression(expression, alias).toList()).containsExactlyElementsOf(expected)
        assertThat(stringSequence.filterNotByExpression(expression, alias).toList()).containsExactlyElementsOf(
            stringSequence.toList() - expected.toSet()
        )
        assertThat(stringSequence.findByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(stringSequence.findLastByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringSequence.firstByExpression(expression, alias)
            }
        } else {
            assertThat(stringSequence.firstByExpression(expression, alias)).isEqualTo(expected.first())
        }
        assertThat(stringSequence.firstOrNullByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(
            stringSequence.indexOfFirstByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringSequence.indexOf(expected.firstOrNull()))
        assertThat(
            stringSequence.indexOfLastByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringSequence.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringSequence.lastByExpression(expression, alias)
            }
        } else {
            assertThat(stringSequence.lastByExpression(expression, alias)).isEqualTo(expected.last())
        }
        assertThat(stringSequence.lastOrNullByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        assertThat(
            stringSequence.partitionByExpression(
                expression,
                alias
            )
        ).isEqualTo(expected to (stringSequence.toList() - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionString")
    fun expressionStringArray(expression: String?, alias: Map<String, String>, expected: List<String>) {
        assertThat(stringArray.allByExpression(expression, alias)).isEqualTo(expected.size == stringArray.size)
        assertThat(stringArray.anyByExpression(expression, alias)).isEqualTo(expected.isNotEmpty())
        assertThat(stringArray.countByExpression(expression, alias)).isEqualTo(expected.size)
        assertThat(stringArray.filterByExpression(expression, alias)).containsExactlyElementsOf(expected)
        assertThat(
            stringArray.filterNotByExpression(
                expression,
                alias
            )
        ).containsExactlyElementsOf(stringArray.toList() - expected.toSet())
        assertThat(stringArray.findByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(stringArray.findLastByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringArray.firstByExpression(expression, alias)
            }
        } else {
            assertThat(stringArray.firstByExpression(expression, alias)).isEqualTo(expected.first())
        }
        assertThat(stringArray.firstOrNullByExpression(expression, alias)).isEqualTo(expected.firstOrNull())
        assertThat(
            stringArray.indexOfFirstByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringArray.indexOf(expected.firstOrNull()))
        assertThat(
            stringArray.indexOfLastByExpression(
                expression,
                alias
            )
        ).isEqualTo(stringArray.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                stringArray.lastByExpression(expression, alias)
            }
        } else {
            assertThat(stringArray.lastByExpression(expression, alias)).isEqualTo(expected.last())
        }
        assertThat(stringArray.lastOrNullByExpression(expression, alias)).isEqualTo(expected.lastOrNull())
        assertThat(
            stringArray.partitionByExpression(
                expression,
                alias
            )
        ).isEqualTo(expected to (stringArray.toList() - expected.toSet()))
    }


    @ParameterizedTest
    @MethodSource("expressionObject")
    fun expressionObjectList(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        assertThat(
            objectList.allMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.size == objectList.size)
        assertThat(objectList.anyMapByExpression(expression, alias) { it.value }).isEqualTo(expected.isNotEmpty())
        assertThat(objectList.countMapByExpression(expression, alias) { it.value }).isEqualTo(expected.size)
        assertThat(objectList.filterMapByExpression(expression, alias) { it.value }).containsExactlyElementsOf(expected)
        assertThat(objectList.filterNotMapByExpression(expression, alias) { it.value }).containsExactlyElementsOf(
            objectList - expected.toSet()
        )
        assertThat(objectList.findMapByExpression(expression, alias) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(objectList.findLastMapByExpression(expression, alias) { it.value }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectList.firstMapByExpression(expression, alias) { it.value }
            }
        } else {
            assertThat(objectList.firstMapByExpression(expression, alias) { it.value }).isEqualTo(expected.first())
        }
        assertThat(
            objectList.firstOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(objectList.indexOfFirstMapByExpression(expression, alias) { it.value }).isEqualTo(
            objectList.indexOf(
                expected.firstOrNull()
            )
        )
        assertThat(objectList.indexOfLastMapByExpression(expression, alias) { it.value }).isEqualTo(
            objectList.indexOf(
                expected.lastOrNull()
            )
        )
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectList.lastMapByExpression(expression, alias) { it.value }
            }
        } else {
            assertThat(objectList.lastMapByExpression(expression, alias) { it.value }).isEqualTo(expected.last())
        }
        assertThat(
            objectList.lastOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.lastOrNull())
        assertThat(
            objectList.partitionMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected to (objectList - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionObject")
    fun expressionObjectSequence(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        assertThat(
            objectSequence.allMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.size == objectSequence.toList().size)
        assertThat(objectSequence.anyMapByExpression(expression, alias) { it.value }).isEqualTo(expected.isNotEmpty())
        assertThat(objectSequence.countMapByExpression(expression, alias, { it.value })).isEqualTo(expected.size)
        assertThat(objectSequence.filterMapByExpression(expression, alias) { it.value }
            .toList()).containsExactlyElementsOf(expected)
        assertThat(objectSequence.filterNotMapByExpression(expression, alias) { it.value }
            .toList()).containsExactlyElementsOf(objectSequence.toList() - expected.toSet())
        assertThat(objectSequence.findMapByExpression(expression, alias) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(
            objectSequence.findLastMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectSequence.firstMapByExpression(expression, alias) { it.value }
            }
        } else {
            assertThat(objectSequence.firstMapByExpression(expression, alias) { it.value }).isEqualTo(expected.first())
        }
        assertThat(
            objectSequence.firstOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(
            objectSequence.indexOfFirstMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(objectSequence.indexOf(expected.firstOrNull()))
        assertThat(
            objectSequence.indexOfLastMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(objectSequence.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectSequence.lastMapByExpression(expression, alias) { it.value }
            }
        } else {
            assertThat(objectSequence.lastMapByExpression(expression, alias) { it.value }).isEqualTo(expected.last())
        }
        assertThat(
            objectSequence.lastOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.lastOrNull())
        assertThat(
            objectSequence.partitionMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected to (objectSequence.toList() - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionObject")
    fun expressionObjectArray(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        assertThat(
            objectArray.allMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.size == objectArray.size)
        assertThat(objectArray.anyMapByExpression(expression, alias) { it.value }).isEqualTo(expected.isNotEmpty())
        assertThat(objectArray.countMapByExpression(expression, alias) { it.value }).isEqualTo(expected.size)
        assertThat(objectArray.filterMapByExpression(expression, alias) { it.value }
            .toList()).containsExactlyElementsOf(expected)
        assertThat(objectArray.filterNotMapByExpression(expression, alias) { it.value }
            .toList()).containsExactlyElementsOf(objectArray.toList() - expected.toSet())
        assertThat(objectArray.findMapByExpression(expression, alias) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(objectArray.findLastMapByExpression(expression, alias) { it.value }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectArray.firstMapByExpression(expression, alias, { it.value })
            }
        } else {
            assertThat(objectArray.firstMapByExpression(expression, alias) { it.value }).isEqualTo(expected.first())
        }
        assertThat(
            objectArray.firstOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.firstOrNull())
        assertThat(
            objectArray.indexOfFirstMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(objectArray.indexOf(expected.firstOrNull()))
        assertThat(
            objectArray.indexOfLastMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(objectArray.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectArray.lastMapByExpression(expression, alias) { it.value }
            }
        } else {
            assertThat(objectArray.lastMapByExpression(expression, alias) { it.value }).isEqualTo(expected.last())
        }
        assertThat(
            objectArray.lastOrNullMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected.lastOrNull())
        assertThat(
            objectArray.partitionMapByExpression(
                expression,
                alias
            ) { it.value }).isEqualTo(expected to (objectArray.toList() - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionFlatMapObject")
    fun expressionFlatMapObjectList(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        println("apply $expression to ${objectSequence.toList()} where $alias => $expected")
        assertThat(objectList.allFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size == objectList.size)
        assertThat(objectList.anyFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.isNotEmpty())
        assertThat(objectList.countFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size)
        assertThat(objectList.filterFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).containsExactlyElementsOf(expected)
        assertThat(objectList.filterNotFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).containsExactlyElementsOf(objectList - expected.toSet())
        assertThat(objectList.findFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectList.findLastFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectList.firstFlatMapByExpression(expression, alias) { listOf(it.value, "${it.value}${it.value}") }
            }
        } else {
            assertThat(objectList.firstFlatMapByExpression(expression, alias) {
                listOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.first())
        }
        assertThat(objectList.firstOrNullFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectList.indexOfFirstFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectList.indexOf(expected.firstOrNull()))
        assertThat(objectList.indexOfLastFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectList.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectList.lastFlatMapByExpression(expression, alias) { listOf(it.value, "${it.value}${it.value}") }
            }
        } else {
            assertThat(objectList.lastFlatMapByExpression(expression, alias) {
                listOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.last())
        }
        assertThat(objectList.lastOrNullFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        assertThat(objectList.partitionFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected to (objectList - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionFlatMapObject")
    fun expressionFlatObjectSequence(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        println("apply $expression to ${objectSequence.toList()} where $alias => $expected")
        assertThat(objectSequence.allFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size == objectSequence.toList().size)
        assertThat(objectSequence.anyFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.isNotEmpty())
        assertThat(objectSequence.countFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size)
        assertThat(objectSequence.filterFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }.toList()).containsExactlyElementsOf(expected)
        assertThat(objectSequence.filterNotFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }.toList()).containsExactlyElementsOf(objectSequence.toList() - expected.toSet())
        assertThat(objectSequence.findFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectSequence.findLastFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectSequence.firstFlatMapByExpression(expression, alias) {
                    sequenceOf(
                        it.value,
                        "${it.value}${it.value}"
                    )
                }
            }
        } else {
            assertThat(objectSequence.firstFlatMapByExpression(expression, alias) {
                sequenceOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.first())
        }
        assertThat(objectSequence.firstOrNullFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectSequence.indexOfFirstFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectSequence.indexOf(expected.firstOrNull()))
        assertThat(objectSequence.indexOfLastFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectSequence.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectSequence.lastFlatMapByExpression(expression, alias) {
                    sequenceOf(
                        it.value,
                        "${it.value}${it.value}"
                    )
                }
            }
        } else {
            assertThat(objectSequence.lastFlatMapByExpression(expression, alias) {
                sequenceOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.last())
        }
        assertThat(objectSequence.lastOrNullFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        assertThat(objectSequence.partitionFlatMapByExpression(expression, alias) {
            sequenceOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected to (objectSequence.toList() - expected.toSet()))
    }

    @ParameterizedTest
    @MethodSource("expressionFlatMapObject")
    fun expressionFlatObjectArray(expression: String?, alias: Map<String, String>, expected: List<TestClass>) {
        assertThat(objectArray.allFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size == objectArray.size)
        assertThat(objectArray.anyFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.isNotEmpty())
        assertThat(objectArray.countFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.size)
        assertThat(objectArray.filterFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }.toList()).containsExactlyElementsOf(expected)
        assertThat(objectArray.filterNotFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }.toList()).containsExactlyElementsOf(objectArray.toList() - expected.toSet())
        assertThat(objectArray.findFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectArray.findLastFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectArray.firstFlatMapByExpression(expression, alias) {
                    listOf(
                        it.value,
                        "${it.value}${it.value}"
                    )
                }
            }
        } else {
            assertThat(objectArray.firstFlatMapByExpression(expression, alias) {
                listOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.first())
        }
        assertThat(objectArray.firstOrNullFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.firstOrNull())
        assertThat(objectArray.indexOfFirstFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectArray.indexOf(expected.firstOrNull()))
        assertThat(objectArray.indexOfLastFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(objectArray.indexOf(expected.lastOrNull()))
        if (expected.isEmpty()) {
            assertThrows<NoSuchElementException> {
                objectArray.lastFlatMapByExpression(expression, alias) { listOf(it.value, "${it.value}${it.value}") }
            }
        } else {
            assertThat(objectArray.lastFlatMapByExpression(expression, alias) {
                listOf(
                    it.value,
                    "${it.value}${it.value}"
                )
            }).isEqualTo(expected.last())
        }
        assertThat(objectArray.lastOrNullFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected.lastOrNull())
        assertThat(objectArray.partitionFlatMapByExpression(expression, alias) {
            listOf(
                it.value,
                "${it.value}${it.value}"
            )
        }).isEqualTo(expected to (objectArray.toList() - expected.toSet()))
    }
}

