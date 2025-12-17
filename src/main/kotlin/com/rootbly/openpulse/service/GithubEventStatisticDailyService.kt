package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticDaily
import com.rootbly.openpulse.repository.GithubEventStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
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
    companion object {
        private val SEOUL_ZONE = ZoneId.of("Asia/Seoul")
        private val UTC_ZONE = ZoneId.of("UTC")
    }

    /**
     * Generates daily event statistics by aggregating hourly statistics
     *
     * Converts server time (KST) to database time (UTC) before querying.
     * Server runs in Korea with KST timezone, but GitHub events are stored in UTC.
     *
     * @param targetDayKST Target day in server timezone (defaults to yesterday)
     */
    @Transactional
    fun generateDailyEventStatistic(targetDayKST: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val dayStartKST = targetDayKST.truncatedTo(ChronoUnit.DAYS)
        val dayStartUTC = dayStartKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val dayEndUTC = dayStartUTC.plus(1, ChronoUnit.DAYS)

        val statistics = githubEventStatisticsHourlyRepository.findByEventTypeStatisticsByTimeRange(
            startTime = dayStartUTC,
            endTime = dayEndUTC
        )

        val now = Instant.now()
        val entities = statistics.map {
            GithubEventStatisticDaily(
                eventType = it.eventType,
                eventCount = it.count,
                statisticDay = dayStartUTC,
                createdAt = now,
                updatedAt = now,
            )
        }

        githubEventStatisticDailyRepository.saveAll(entities)
    }
}