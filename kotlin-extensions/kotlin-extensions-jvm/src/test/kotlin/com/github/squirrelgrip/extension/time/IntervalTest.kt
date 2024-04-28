package com.github.squirrelgrip.extension.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import java.time.Duration
import java.time.Instant
import java.time.Period
import java.util.stream.Stream

class IntervalTest {

    companion object {
        val period1Day = Period.ofDays(1)
        val period2Day = Period.ofDays(2)
        val duration1Day = Duration.ofDays(1)
        val duration2Day = Duration.ofDays(2)
        val now = Instant.now()
        val future1Day = now.plus(period1Day)
        val future2Day = now.plus(period2Day)
        val past1Day = now.minus(period1Day)
        val past2Day = now.minus(period2Day)
        val testSubject = Interval.of(past1Day, future1Day)
        val allBeforeInterval = Interval.of(past2Day, past1Day)
        val allBetweenInterval = Interval.of(past1Day, future1Day)
        val allAfterInterval = Interval.of(future1Day, future2Day)
        val allInterval = Interval.of(past2Day, future2Day)

        @JvmStatic
        fun create(): Stream<Interval> {
            return Stream.of(
                testSubject,
                Interval.of(past1Day, period2Day),
                Interval.of(future1Day, period2Day.negated()),
                Interval.of(period2Day, future1Day),
                Interval.of(period2Day.negated(), past1Day),
                Interval.of(past1Day, duration2Day),
                Interval.of(future1Day, duration2Day.negated()),
                Interval.of(duration2Day, future1Day),
                Interval.of(duration2Day.negated(), past1Day),
                Interval.of(now, period1Day).withStart(past1Day),
                Interval.of(period1Day, now).withEnd(future1Day),
                Interval.of(past1Day.toOffsetDateTime(), period2Day),
                Interval.of(future1Day.toOffsetDateTime(), period2Day.negated()),
                Interval.of(period2Day, future1Day.toOffsetDateTime()),
                Interval.of(period2Day.negated(), past1Day.toOffsetDateTime()),
                Interval.of(past1Day.toOffsetDateTime(), duration2Day),
                Interval.of(future1Day.toOffsetDateTime(), duration2Day.negated()),
                Interval.of(duration2Day, future1Day.toOffsetDateTime()),
                Interval.of(duration2Day.negated(), past1Day.toOffsetDateTime()),
                Interval.of(now.toOffsetDateTime(), period1Day).withStart(past1Day),
                Interval.of(period1Day, now.toOffsetDateTime()).withEnd(future1Day),
                Interval.parse("$past1Day/$future1Day"),
                Interval.parse("$past1Day/$period2Day"),
                Interval.parse("$period2Day/$future1Day"),
                Interval.parse("$future1Day/${period2Day.negated()}"),
                Interval.parse("${period2Day.negated()}/$past1Day"),
                Interval.parse("$future1Day/${duration2Day.negated()}"),
                Interval.parse("${duration2Day.negated()}/$past1Day"),
            )
        }
    }

    @ParameterizedTest
    @MethodSource
    fun create(interval: Interval) {
        assertThat(interval.start).isEqualTo(past1Day)
        assertThat(interval.end).isEqualTo(future1Day)
        assertThat(interval.toDuration()).isEqualTo(duration2Day)
    }

    @Test
    fun isBeforeGivenInstant() {
        assertThat(testSubject.isBefore(past2Day)).isFalse()
        assertThat(testSubject.isBefore(past1Day)).isFalse()
        assertThat(testSubject.isBefore(now)).isFalse()
        assertThat(testSubject.isBefore(future1Day)).isTrue()
        assertThat(testSubject.isBefore(future2Day)).isTrue()
    }

    @Test
    fun isAfterGivenInstant() {
        assertThat(testSubject.isAfter(past2Day)).isTrue()
        assertThat(testSubject.isAfter(past1Day)).isFalse()
        assertThat(testSubject.isAfter(now)).isFalse()
        assertThat(testSubject.isAfter(future1Day)).isFalse()
        assertThat(testSubject.isAfter(future2Day)).isFalse()
    }

    @Test
    fun containsGivenInstant() {
        assertThat(testSubject.contains(past2Day)).isFalse()
        assertThat(testSubject.contains(past1Day)).isTrue()
        assertThat(testSubject.contains(now)).isTrue()
        assertThat(testSubject.contains(future1Day.minusNanos(1))).isTrue()
        assertThat(testSubject.contains(future1Day)).isFalse()
        assertThat(testSubject.contains(future2Day)).isFalse()
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
        assertThat(testSubject.abuts(allBeforeInterval.withEnd(allBeforeInterval.end.minusMillis(1)))).isFalse()
        assertThat(testSubject.abuts(allBeforeInterval)).isTrue()
        assertThat(testSubject.abuts(allBeforeInterval.withEnd(allBeforeInterval.end.plusMillis(1)))).isFalse()
        assertThat(testSubject.abuts(allBetweenInterval)).isFalse()
        assertThat(testSubject.abuts(allAfterInterval.withStart(allBeforeInterval.start.minusMillis(1)))).isFalse()
        assertThat(testSubject.abuts(allAfterInterval)).isTrue()
        assertThat(testSubject.abuts(allAfterInterval.withStart(allBeforeInterval.start.plusMillis(1)))).isFalse()
        assertThat(testSubject.abuts(allInterval)).isFalse()
    }

    @Test
    fun overlaps() {
        assertThat(testSubject.overlaps(allBeforeInterval.withEnd(testSubject.start.minusMillis(1)))).isFalse()
        assertThat(testSubject.overlaps(allBeforeInterval)).isTrue()
        assertThat(testSubject.overlaps(allBeforeInterval.withEnd(testSubject.start.plusMillis(1)))).isTrue()
        assertThat(testSubject.overlaps(allBetweenInterval)).isTrue()
        assertThat(testSubject.overlaps(testSubject)).isTrue()
        assertThat(testSubject.overlaps(allAfterInterval.withStart(testSubject.end.minusMillis(1)))).isTrue()
        assertThat(testSubject.overlaps(allAfterInterval)).isFalse()
        assertThat(testSubject.overlaps(allAfterInterval.withStart(testSubject.end.plusMillis(1)))).isFalse()
        assertThat(testSubject.overlaps(allInterval)).isTrue()
    }

    @Test
    fun overlap() {
        assertThat(testSubject.overlap(allBeforeInterval.withEnd(testSubject.start.minusMillis(1))))
            .isNull()
        assertThat(testSubject.overlap(allBeforeInterval))
            .isEqualTo(testSubject.withEnd(testSubject.start))
        assertThat(testSubject.overlap(allBeforeInterval.withEnd(testSubject.start.plusMillis(1))))
            .isEqualTo(testSubject.withEnd(testSubject.start.plusMillis(1)))
        assertThat(testSubject.overlap(allBetweenInterval))
            .isEqualTo(allBetweenInterval)
        assertThat(testSubject.overlap(allAfterInterval.withStart(testSubject.end.minusMillis(1))))
            .isEqualTo(testSubject.withStart(testSubject.end.minusMillis(1)))
        assertThat(testSubject.overlap(allAfterInterval))
            .isNull()
        assertThat(testSubject.overlap(allAfterInterval.withStart(testSubject.end.plusMillis(1))))
            .isNull()
        assertThat(testSubject.overlap(allInterval))
            .isEqualTo(testSubject)
    }

    @Test
    fun join() {
        assertThrows<IllegalArgumentException> {
            testSubject.join(
                allBeforeInterval.withEnd(
                    testSubject.start.minusMillis(
                        1
                    )
                )
            )
        }
        assertThat(testSubject.join(allBeforeInterval))
            .isEqualTo(allBeforeInterval.withEnd(testSubject.end))
        assertThat(testSubject.join(allBeforeInterval.withEnd(testSubject.start.plusMillis(1))))
            .isEqualTo(allBeforeInterval.withEnd(testSubject.end))
        assertThat(testSubject.join(allBetweenInterval))
            .isEqualTo(allBetweenInterval)
        assertThat(testSubject.join(allAfterInterval.withStart(testSubject.end.minusMillis(1))))
            .isEqualTo(testSubject.withEnd(allAfterInterval.end))
        assertThat(testSubject.join(allAfterInterval))
            .isEqualTo(testSubject.withEnd(allAfterInterval.end))
        assertThrows<IllegalArgumentException> {
            testSubject.join(
                allAfterInterval.withStart(
                    testSubject.end.plusMillis(
                        1
                    )
                )
            )
        }
        assertThat(testSubject.join(allInterval))
            .isEqualTo(allInterval)
    }

    @Test
    fun gap() {
        assertThat(testSubject.gap(allBeforeInterval.withEnd(testSubject.start.minusMillis(1))))
            .isEqualTo(Interval.of(testSubject.start.minusMillis(1), testSubject.start))
        assertThat(testSubject.gap(allBeforeInterval))
            .isNull()
        assertThat(testSubject.gap(allBeforeInterval.withEnd(testSubject.start.plusMillis(1))))
            .isNull()
        assertThat(testSubject.gap(allBetweenInterval))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval.withStart(testSubject.end.minusMillis(1))))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval))
            .isNull()
        assertThat(testSubject.gap(allAfterInterval.withStart(testSubject.end.plusMillis(1))))
            .isEqualTo(Interval.of(testSubject.end, testSubject.end.plusMillis(1)))
        assertThat(testSubject.gap(allInterval))
            .isNull()
    }

    @Test
    fun contains() {
        assertThat(testSubject.contains(allBeforeInterval.withEnd(testSubject.start.minusMillis(1)))).isFalse()
        assertThat(testSubject.contains(allBeforeInterval)).isFalse()
        assertThat(testSubject.contains(allBeforeInterval.withEnd(testSubject.start.plusMillis(1)))).isFalse()
        assertThat(testSubject.contains(allBetweenInterval)).isTrue()
        assertThat(testSubject.contains(testSubject)).isTrue()
        assertThat(testSubject.contains(allAfterInterval.withStart(testSubject.end.minusMillis(1)))).isFalse()
        assertThat(testSubject.contains(allAfterInterval)).isFalse()
        assertThat(testSubject.contains(allAfterInterval.withStart(testSubject.end.plusMillis(1)))).isFalse()
        assertThat(testSubject.contains(allInterval)).isFalse()
    }
}
