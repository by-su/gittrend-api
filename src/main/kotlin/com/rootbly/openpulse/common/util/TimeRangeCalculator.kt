package com.rootbly.openpulse.common.util

import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * Utility class for calculating time ranges for statistics
 */
object TimeRangeCalculator {

    data class TimeRange(
        val start: Instant,
        val end: Instant
    )

    /**
     * Calculates the previous hour range
     * Example: current time 18:20 -> 17:00 ~ 18:00
     */
    fun getPreviousHourRange(now: LocalDateTime = LocalDateTime.now()): TimeRange {
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS)
        val previousHourStart = currentHourStart.minusHours(1)
        val previousHourEnd = currentHourStart

        return TimeRange(
            start = previousHourStart.toInstant(ZoneOffset.UTC),
            end = previousHourEnd.toInstant(ZoneOffset.UTC)
        )
    }

    /**
     * Calculates the previous day range
     * Example: current 12-29 15:00 -> 12-28 00:00 ~ 12-29 00:00
     */
    fun getPreviousDayRange(now: LocalDateTime = LocalDateTime.now()): TimeRange {
        val todayStart = now.truncatedTo(ChronoUnit.DAYS)
        val yesterdayStart = todayStart.minusDays(1)
        val yesterdayEnd = todayStart

        return TimeRange(
            start = yesterdayStart.toInstant(ZoneOffset.UTC),
            end = yesterdayEnd.toInstant(ZoneOffset.UTC)
        )
    }

    /**
     * Calculates hour range for a specific target time
     */
    fun getHourRange(targetTime: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)
        val hourEnd = hourStart.plusHours(1)
        return hourStart to hourEnd
    }

    /**
     * Calculates day range for a specific target time
     */
    fun getDayRange(targetTime: LocalDateTime): Pair<LocalDateTime, LocalDateTime> {
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)
        val dayEnd = dayStart.plusDays(1)
        return dayStart to dayEnd
    }

    /**
     * Converts LocalDateTime to Instant (UTC)
     */
    fun toInstant(dateTime: LocalDateTime): Instant {
        return dateTime.toInstant(ZoneOffset.UTC)
    }
}
