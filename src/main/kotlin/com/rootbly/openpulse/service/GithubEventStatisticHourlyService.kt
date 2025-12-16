package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.repository.GithubEventRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

/**
 * GitHub event hourly statistics generation service
 *
 * Aggregates events by type within hourly buckets.
 * Handles timezone conversion between server time (KST) and database time (UTC).
 */
@Service
@Transactional(readOnly = true)
class GithubEventStatisticHourlyService(
    private val githubEventRepository: GithubEventRepository,
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private val SEOUL_ZONE = ZoneId.of("Asia/Seoul")
        private val UTC_ZONE = ZoneId.of("UTC")
    }

    /**
     * Generates hourly event statistics for the target hour
     *
     * Converts server time (KST) to database time (UTC) before querying.
     * Server runs in Korea with KST timezone, but GitHub events are stored in UTC.
     *
     * Example: Server time 14:00 KST → Query events from 05:00-06:00 UTC
     *
     * @param targetHourKST Target hour in server timezone (defaults to current server time)
     */
    @Transactional
    fun generateHourlyEventStatistics(targetHourKST: LocalDateTime = LocalDateTime.now()) {
        // Truncate to hour boundary and convert server time (KST) → database time (UTC)
        val hourStartKST = targetHourKST.truncatedTo(ChronoUnit.HOURS)
        val hourStartUTC = hourStartKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val hourEndUTC = hourStartUTC.plus(1, ChronoUnit.HOURS)
        
        val statistics = githubEventRepository.findByEventTypeStatisticsByTimeRange(
            startTime = hourStartUTC,
            endTime = hourEndUTC
        )
        
        val entities = statistics.map {
            GithubEventStatisticHourly(
                eventType = it.eventType,
                eventCount = it.count,
                statisticHour = hourStartUTC,
                createdAt = hourStartUTC,
                updatedAt = hourStartUTC,
            )
        }.toList()

        githubEventStatisticsHourlyRepository.saveAll(entities)
    }
}