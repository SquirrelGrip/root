package com.github.squirrelgrip.extension.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.LocalDate
import java.time.Period
import java.util.stream.Stream

class DateIntervalTest {

    companion object {
        val period0Days = Period.ofDays(0)
        val period1Day = Period.ofDays(1)
        val period7Days = Period.ofDays(7)
        val period14Days = Period.ofDays(14)
        val period180Days = Period.ofDays(180)
        val today = LocalDate.now()
        val future7Days = today.plus(period7Days)
        val future14Days = today.plus(period14Days)
        val future180Days = today.plus(period180Days)
        val past7Days = today.minus(period7Days)
        val past14Days = today.minus(period14Days)
        val past180Days = today.minus(period180Days)
        val testSubject = DateInterval.of(past7Days, future7Days)
        val allBeforeInterval = DateInterval.of(past14Days, past7Days)
        val allBetweenInterval = DateInterval.of(past7Days, future7Days)
        val allAfterInterval = DateInterval.of(future7Days, future14Days)
        val allInterval = DateInterval.of(past14Days, future14Days)
        val allYearInterval = DateInterval.of(past180Days, future180Days)
        val allFebInterval = DateInterval.of(LocalDate.of(2023, 2, 1), LocalDate.of(2023, 3, 1))

        @JvmStatic
        fun create(): Stream<DateInterval> {
            return Stream.of(
                testSubject,
                DateInterval.of(past7Days, period14Days),
                DateInterval.of(future7Days, period14Days.negated()),
                DateInterval.of(period14Days, future7Days),
                DateInterval.of(period14Days.negated(), past7Days),
                DateInterval.of(today, period7Days).withStart(past7Days),
                DateInterval.of(period7Days, today).withEnd(future7Days),
                DateInterval.parse("$past7Days/$future7Days"),
                DateInterval.parse("$past7Days/$period14Days"),
                DateInterval.parse("$period14Days/$future7Days"),
                DateInterval.parse("$future7Days/${period14Days.negated()}"),
                DateInterval.parse("${period14Days.negated()}/$past7Days"),
            )
        }

        @JvmStatic
        fun properties(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(allBeforeInterval, 7),
                Arguments.of(allBetweenInterval, 14),
                Arguments.of(testSubject, 14),
                Arguments.of(allAfterInterval, 7),
                Arguments.of(allInterval, 28),
                Arguments.of(allFebInterval, 28),
                Arguments.of(allYearInterval, 360),
            )
        }
    }

    @ParameterizedTest
    @MethodSource
    fun create(interval: DateInterval) {
        assertThat(interval.start).isEqualTo(past7Days)
        assertThat(interval.end).isEqualTo(future7Days)
        assertThat(interval.toPeriod()).isEqualTo(period14Days)
    }

    @Test
    fun sameDayInterval() {
        assertThat(DateInterval.of(today, today).toPeriod()).isEqualTo(period0Days)
    }

    @Test
    fun isBeforeGivenInstant() {
        assertThat(testSubject.isBefore(past14Days)).isFalse()
        assertThat(testSubject.isBefore(past7Days)).isFalse()
        assertThat(testSubject.isBefore(today)).isFalse()
        assertThat(testSubject.isBefore(future7Days)).isTrue()
        assertThat(testSubject.isBefore(future14Days)).isTrue()
    }

    @Test
    fun isAfterGivenInstant() {
        assertThat(testSubject.isAfter(past14Days)).isTrue()
        assertThat(testSubject.isAfter(past7Days)).isFalse()
        assertThat(testSubject.isAfter(today)).isFalse()
        assertThat(testSubject.isAfter(future7Days)).isFalse()
        assertThat(testSubject.isAfter(future14Days)).isFalse()
    }

    @Test
    fun containsGivenInstant() {
        assertThat(testSubject.contains(past14Days)).isFalse()
        assertThat(testSubject.contains(past7Days)).isTrue()
        assertThat(testSubject.contains(today)).isTrue()
        assertThat(testSubject.contains(future7Days.minusDays(1))).isTrue()
        assertThat(testSubject.contains(future7Days)).isFalse()
        assertThat(testSubject.contains(future14Days)).isFalse()
    }

    @Test
    fun isBeforeGivenInterval() {
        assertThat(testSubject.isBefore(allBeforeInterval)).isFalse()
        assertThat(testSubject.isBefore(allBetweenInterval)).isFalse()
        assertThat(testSubject.isBefore(allAfterInterval)).isTrue()
        assertThat(testSubject.isBefore(allInterval)).isFalse()
    }

    @Test
    fun isAfterGivenInterval() {
        assertThat(testSubject.isAfter(allBeforeInterval)).isTrue()
        assertThat(testSubject.isAfter(allBetweenInterval)).isFalse()
        assertThat(testSubject.isAfter(allAfterInterval)).isFalse()
        assertThat(testSubject.isAfter(allInterval)).isFalse()
    }

    @Test
    fun abuts() {
        assertThat(testSubject.abuts(allBeforeInterval.withEnd(allBeforeInterval.end.minusDays(1)))).isFalse()
        assertThat(testSubject.abuts(allBeforeInterval)).isTrue()
        assertThat(testSubject.abuts(allBeforeInterval.withEnd(allBeforeInterval.end.plusDays(1)))).isFalse()
        assertThat(testSubject.abuts(allBetweenInterval)).isFalse()
        assertThat(testSubject.abuts(allAfterInterval.withStart(allBeforeInterval.start.minusDays(1)))).isFalse()
        assertThat(testSubject.abuts(allAfterInterval)).isTrue()
        assertThat(testSubject.abuts(allAfterInterval.withStart(allBeforeInterval.start.plusDays(1)))).isFalse()
        assertThat(testSubject.abuts(allInterval)).isFalse()
    }

    @Test
    fun overlaps() {
        assertThat(testSubject.overlaps(allBeforeInterval.withEnd(testSubject.start.minusDays(1)))).isFalse()
        assertThat(testSubject.overlaps(allBeforeInterval)).isTrue()
        assertThat(testSubject.overlaps(allBeforeInterval.withEnd(testSubject.start.plusDays(1)))).isTrue()
        assertThat(testSubject.overlaps(allBetweenInterval)).isTrue()
        assertThat(testSubject.overlaps(testSubject)).isTrue()
        assertThat(testSubject.overlaps(allAfterInterval.withStart(testSubject.end.minusDays(1)))).isTrue()
        assertThat(testSubject.overlaps(allAfterInterval)).isFalse()
        assertThat(testSubject.overlaps(allAfterInterval.withStart(testSubject.end.plusDays(1)))).isFalse()
        assertThat(testSubject.overlaps(allInterval)).isTrue()
    }

    @Test
    fun overlap() {
        assertThat(testSubject.overlap(allBeforeInterval.withEnd(testSubject.start.minusDays(1))))
            .isNull()
        assertThat(testSubject.overlap(allBeforeInterval))
            .isEqualTo(testSubject.withEnd(testSubject.start))
        assertThat(testSubject.overlap(allBeforeInterval.withEnd(testSubject.start.plusDays(1))))
            .isEqualTo(testSubject.withEnd(testSubject.start.plusDays(1)))
        assertThat(testSubject.overlap(allBetweenInterval))
            .isEqualTo(allBetweenInterval)
        assertThat(testSubject.overlap(allAfterInterval.withStart(testSubject.end.minusDays(1))))
            .isEqualTo(testSubject.withStart(testSubject.end.minusDays(1)))
        assertThat(testSubject.overlap(allAfterInterval))
            .isNull()
        assertThat(testSubject.overlap(allAfterInterval.withStart(testSubject.end.plusDays(1))))
            .isNull()
        assertThat(testSubject.overlap(allInterval))
            .isEqualTo(testSubject)
    }

    @Test
    fun join() {
        assertThrows<IllegalArgumentException> {
            testSubject.join(allBeforeInterval.withEnd(testSubject.start.minusDays(1)))
        }
        assertThrows<IllegalArgumentException> {
            testSubject.join(allAfterInterval.withStart(testSubject.end.plusDays(1)))
        }
        assertThat(testSubject.join(allBeforeInterval))
            .isEqualTo(allBeforeInterval.withEnd(testSubject.end))
        assertThat(testSubject.join(allBeforeInterval.withEnd(testSubject.start.plusDays(1))))
            .isEqualTo(allBeforeInterval.withEnd(testSubject.end))
        assertThat(testSubject.join(allBetweenInterval))
            .isEqualTo(allBetweenInterval)
        assertThat(testSubject.join(allAfterInterval.withStart(testSubject.end.minusDays(1))))
            .isEqualTo(testSubject.withEnd(allAfterInterval.end))
        assertThat(testSubject.join(allAfterInterval))
            .isEqualTo(testSubject.withEnd(allAfterInterval.end))
        assertThat(testSubject.join(allInterval))
            .isEqualTo(allInterval)
    }

    @Test
    fun gap() {
        assertThat(testSubject.gap(allBeforeInterval.withEnd(testSubject.start.minusDays(1))))
            .isEqualTo(DateInterval.of(testSubject.start.minusDays(1), testSubject.start))
        assertThat(testSubject.gap(allBeforeInterval))
            .isNull()
        assertThat(testSubject.gap(allBeforeInterval.withEnd(testSubject.start.plusDays(1))))
            .isNull()
        assertThat(testSubject.gap(allBetweenInterval))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval.withStart(testSubject.end.minusDays(1))))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval.withStart(testSubject.end.plusDays(1))))
            .isEqualTo(DateInterval.of(testSubject.end, testSubject.end.plusDays(1)))
        assertThat(testSubject.gap(allInterval))
            .isNull()
    }

    @Test
    fun contains() {
        assertThat(testSubject.contains(allBeforeInterval.withEnd(testSubject.start.minusDays(1)))).isFalse()
        assertThat(testSubject.contains(allBeforeInterval)).isFalse()
        assertThat(testSubject.contains(allBeforeInterval.withEnd(testSubject.start.plusDays(1)))).isFalse()
        assertThat(testSubject.contains(allBetweenInterval)).isTrue()
        assertThat(testSubject.contains(testSubject)).isTrue()
        assertThat(testSubject.contains(allAfterInterval.withStart(testSubject.end.minusDays(1)))).isFalse()
        assertThat(testSubject.contains(allAfterInterval)).isFalse()
        assertThat(testSubject.contains(allAfterInterval.withStart(testSubject.end.plusDays(1)))).isFalse()
        assertThat(testSubject.contains(allInterval)).isFalse()
    }

    @ParameterizedTest
    @MethodSource
    fun properties(testSubject: DateInterval, expectedSize: Int) {
        assertThat(testSubject).hasSize(expectedSize)
        assertThrows<IndexOutOfBoundsException> { testSubject[expectedSize] }
        assertThrows<IndexOutOfBoundsException> { testSubject[-1] }
        assertThat(testSubject.containsAll(listOf(testSubject.start))).isTrue()
        assertThat(testSubject[0]).isEqualTo(testSubject.start)
        assertThat(testSubject[testSubject.size - 1]).isEqualTo(testSubject.end.minusDays(1))
        assertThat(testSubject.indexOf(testSubject.start)).isEqualTo(0)
        assertThat(testSubject.indexOf(testSubject.end.minusDays(1))).isEqualTo(testSubject.size - 1)
        assertThat(testSubject.first()).isEqualTo(testSubject.start)
        assertThat(testSubject.last()).isEqualTo(testSubject.end.minusDays(1))
    }
}
