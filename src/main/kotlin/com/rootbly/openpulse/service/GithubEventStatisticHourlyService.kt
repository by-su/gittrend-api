package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.repository.GithubEventRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * GitHub event hourly statistics generation service
 *
 * Aggregates events by type within hourly buckets.
 */
@Service
@Transactional(readOnly = true)
class GithubEventStatisticHourlyService(
    private val githubEventRepository: GithubEventRepository,
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates hourly event statistics for the target hour
     *
     * @param targetHour Target hour in server timezone (defaults to current server time)
     */
    @Transactional
    fun generateHourlyEventStatistics(targetHour: LocalDateTime = LocalDateTime.now()) {
        val hourStart = targetHour.truncatedTo(ChronoUnit.HOURS)
        val hourStartInstant = hourStart.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val hourEndInstant = hourStartInstant.plus(1, ChronoUnit.HOURS)

        val statistics = githubEventRepository.findByEventTypeStatisticsByTimeRange(
            startTime = hourStartInstant,
            endTime = hourEndInstant
        )

        val entities = statistics.map {
            GithubEventStatisticHourly(
                eventType = it.eventType,
                eventCount = it.count,
                statisticHour = hourStartInstant,
                createdAt = hourStartInstant,
                updatedAt = hourStartInstant,
            )
        }.toList()

        githubEventStatisticsHourlyRepository.saveAll(entities)
    }
}