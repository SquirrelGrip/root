package com.github.squirrelgrip.extension.time

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import java.time.Instant
import java.time.Year
import java.time.YearMonth
import java.time.ZoneOffset
import java.time.temporal.Temporal
import java.util.stream.Stream

internal class TimeExtensionsKtTest {
    companion object {
        val now = Instant.now()
        val utc = ZoneOffset.UTC
        val singapore = ZoneOffset.ofHours(8)
        val alaska = ZoneOffset.ofHours(-8)

        @JvmStatic
        fun toInstance(): Stream<Arguments> {
            return Stream.of(
                Arguments.of(now, utc),

                Arguments.of(now.toOffsetDateTime(), utc),
                Arguments.of(now.toOffsetDateTime(singapore), utc),
                Arguments.of(now.toOffsetDateTime(), singapore),
                Arguments.of(now.toOffsetDateTime(), alaska),

                Arguments.of(now.toZonedDateTime(), utc),
                Arguments.of(now.toZonedDateTime(singapore), utc),
                Arguments.of(now.toZonedDateTime(), singapore),
                Arguments.of(now.toZonedDateTime(), alaska),

                Arguments.of(now.toLocalDateTime(), utc),
                Arguments.of(now.toLocalDateTime(utc), utc),
                Arguments.of(now.toLocalDateTime(singapore), singapore),
                Arguments.of(now.toLocalDateTime(alaska), alaska),

                Arguments.of(now.toLocalDateTime().toLocalTime(), utc),
                Arguments.of(now.toLocalDateTime(utc).toLocalTime(), utc),
                Arguments.of(now.toLocalDateTime(singapore).toLocalTime(), singapore),
                Arguments.of(now.toLocalDateTime(alaska).toLocalTime(), alaska),

                Arguments.of(now.toLocalDateTime().toLocalDate(), utc),
                Arguments.of(now.toLocalDateTime(utc).toLocalDate(), utc),
                Arguments.of(now.toLocalDateTime(singapore).toLocalDate(), singapore),
                Arguments.of(now.toLocalDateTime(alaska).toLocalDate(), alaska),

                Arguments.of(Year.now(), utc),
                Arguments.of(Year.now(utc), utc),
                Arguments.of(Year.now(singapore), singapore),
                Arguments.of(Year.now(alaska), alaska),

                Arguments.of(YearMonth.now(), utc),
                Arguments.of(YearMonth.now(utc), utc),
                Arguments.of(YearMonth.now(singapore), singapore),
                Arguments.of(YearMonth.now(alaska), alaska),
            )
        }
    }

    @ParameterizedTest
    @MethodSource
    fun toInstance(testSubject: Temporal, zone: ZoneOffset) {
        assertThat(testSubject.toInstant(zone, now.toLocalDate(zone), now.toLocalDateTime(zone).toLocalTime())).describedAs("${testSubject.javaClass} in $zone").isEqualTo(now)
    }
}
