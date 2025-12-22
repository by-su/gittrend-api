package com.rootbly.openpulse.service

import com.rootbly.openpulse.common.util.TimeRangeCalculator
import com.rootbly.openpulse.entity.statistic.event.GithubEventStatisticDaily
import com.rootbly.openpulse.repository.GithubEventStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

/**
 * GitHub event daily statistics generation service
 *
 * Aggregates events by type within daily buckets.
 */
@Service
@Transactional(readOnly = true)
class GithubEventStatisticDailyService(
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository,
    private val githubEventStatisticDailyRepository: GithubEventStatisticDailyRepository
) {
    /**
     * Generates daily event statistics by aggregating hourly statistics
     *
     * @param targetDay Target day in server timezone (defaults to yesterday)
     */
    @Transactional
    fun generateDailyEventStatistic(targetDay: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val (dayStart, _) = TimeRangeCalculator.getDayRange(targetDay)
        val dayStartInstant = TimeRangeCalculator.toInstant(dayStart)

        val dayEndInstant = dayStartInstant.plus(1, ChronoUnit.DAYS)

        val statistics = githubEventStatisticsHourlyRepository.findByEventTypeStatisticByTimeRange(
            startTime = dayStartInstant,
            endTime = dayEndInstant
        )

        val now = Instant.now()
        val entities = statistics.map {
            GithubEventStatisticDaily(
                eventType = it.eventType,
                eventCount = it.count,
                statisticDay = dayStartInstant,
                createdAt = now,
                updatedAt = now,
            )
        }

        githubEventStatisticDailyRepository.saveAll(entities)
    }

    /**
     * Retrieve GitHub event daily statistics for the previous day window
     *
     * @return List of event type counts for the previous day
     */
    @Cacheable(value = ["dailyStatistics"], key = "'event'")
    fun retrieveDaily(): List<GithubEventStatisticDaily> {
        val timeRange = TimeRangeCalculator.getPreviousDayRange()
        return githubEventStatisticDailyRepository.findAllByStatisticDayGreaterThanEqualAndStatisticDayLessThan(timeRange.start, timeRange.end)
    }
}