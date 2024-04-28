package com.github.squirrelgrip.extension.time

import java.time.Duration
import java.time.LocalDate
import java.time.Period
import java.util.*
import kotlin.collections.AbstractList

class DateInterval private constructor(
    val start: LocalDate,
    val end: LocalDate
) : AbstractList<LocalDate>() {
    companion object {
        fun of(start: LocalDate, end: LocalDate): DateInterval =
            if (start.isEqualOrBefore(end)) DateInterval(
                start,
                end
            ) else throw IllegalArgumentException("Start is after end")

        fun of(start: LocalDate, period: Period): DateInterval =
            if (period.isNegative) of(start.plus(period), start) else of(start, start.plus(period))

        fun of(period: Period, end: LocalDate): DateInterval =
            if (period.isNegative) of(end, end.minus(period)) else of(end.minus(period), end)

        fun of(duration: Duration, end: LocalDate): DateInterval =
            if (duration.isNegative) of(end, end.minus(duration)) else of(end.minus(duration), end)

        fun parse(str: String): DateInterval {
            val split = str.split("/")
            return when {
                split[0].matches(Regex("[-+]?P.*")) -> of(Period.parse(split[0]), LocalDate.parse(split[1]))
                split[1].matches(Regex("[-+]?P.*")) -> of(LocalDate.parse(split[0]), Period.parse(split[1]))
                else -> of(LocalDate.parse(split[0]), LocalDate.parse(split[1]))
            }
        }

        fun subListRangeCheck(fromIndex: Int, toIndex: Int, size: Int) {
            if (fromIndex < 0) throw IndexOutOfBoundsException("fromIndex = $fromIndex")
            if (toIndex > size) throw IndexOutOfBoundsException("toIndex = $toIndex")
            if (fromIndex > toIndex) throw IllegalArgumentException(
                "fromIndex(" + fromIndex +
                    ") > toIndex(" + toIndex + ")"
            )
        }
    }

    fun toPeriod(): Period = Period.between(start, end)

    fun isBefore(date: LocalDate): Boolean = end.isEqualOrBefore(date)

    fun isBefore(interval: DateInterval): Boolean = isBefore(interval.start)

    fun isAfter(date: LocalDate): Boolean = start.isAfter(date)

    fun isAfter(interval: DateInterval): Boolean = start.isEqualOrAfter(interval.end)

    override fun contains(element: LocalDate): Boolean = (element.isEqualOrAfter(start)) && element.isBefore(end)

    //    Does this interval abut with the interval specified.
    fun abuts(interval: DateInterval): Boolean = interval.end == start || interval.start == end

    fun overlaps(interval: DateInterval): Boolean =
        contains(interval.start) || contains(interval.end) || (interval.contains(start) && interval.contains(end))

    fun contains(interval: DateInterval): Boolean = contains(interval.start) && interval.end.isEqualOrBefore(end)

    //    Gets the gap between this interval and another interval.
    fun gap(interval: DateInterval): DateInterval? =
        if (abuts(interval) || overlaps(interval)) {
            null
        } else if (isBefore(interval)) {
            of(end, interval.start)
        } else {
            of(interval.end, start)
        }

    //    Gets the overlap between this interval and another interval.
    fun overlap(interval: DateInterval): DateInterval? =
        if (overlaps(interval)) {
            val tempStart = if (start.isBefore(interval.start)) interval.start else start
            val tempEnd = if (interval.end.isAfter(end)) end else interval.end
            of(tempStart, tempEnd)
        } else {
            null
        }

    fun join(interval: DateInterval): DateInterval? =
        when {
            abuts(interval) || overlaps(interval) -> {
                val tempStart = if (start.isBefore(interval.start)) start else interval.start
                val tempEnd = if (interval.end.isAfter(end)) interval.end else end
                of(tempStart, tempEnd)
            }
            else -> throw IllegalArgumentException("Interval does not abut or overlap.")
        }

    /**
     * Creates a new interval with the specified period after the start instant.
     * @param period
     * @return the new interval with same start with specified period
     */
    fun withPeriodAfterStart(period: Period): DateInterval = of(start, period)

    /**
     * Creates a new interval with the specified period before the end instant.
     * @param period
     * @return the new interval with same end with specified period
     */
    fun withPeriodBeforeEnd(period: Period): DateInterval = of(period, end)

    /**
     * Creates a new interval with the specified start instant.
     * @param start
     * @return the new interval with same end with specified start
     */
    fun withStart(start: LocalDate): DateInterval = of(start, end)

    /**
     * Creates a new interval with the specified end instant.
     * @param end
     * @return the new interval with same start with specified end
     */
    fun withEnd(end: LocalDate): DateInterval = of(start, end)

    override fun toString(): String = "$start/$end"

    override fun equals(other: Any?): Boolean =
        when {
            this === other -> true
            javaClass != other?.javaClass -> false
            else -> {
                other as DateInterval
                start == other.start && end == other.end
            }
        }

    override fun hashCode(): Int = 31 * start.hashCode() + end.hashCode()

    override val size: Int
        get() = (end.toEpochDay() - start.toEpochDay()).toInt()

    override fun get(index: Int): LocalDate {
        Objects.checkIndex(index, size)
        return start.plusDays(index.toLong())
    }
}
