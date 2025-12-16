package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.repository.GithubEventRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@Service
@Transactional(readOnly = true)
class GithubEventStatisticHourlyService(
    private val githubEventRepository: GithubEventRepository,
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository
) {

    companion object {
        private val SEOUL_ZONE = ZoneId.of("Asia/Seoul")
        private val UTC_ZONE = ZoneId.of("UTC")
    }

    @Transactional
    fun generateHourlyEventStatistics(targetHourKST: LocalDateTime = LocalDateTime.now()) {
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