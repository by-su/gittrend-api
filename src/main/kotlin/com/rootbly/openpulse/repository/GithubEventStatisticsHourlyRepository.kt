package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.statistic.event.GithubEventStatisticHourly
import com.rootbly.openpulse.payload.EventTypeCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface GithubEventStatisticsHourlyRepository: JpaRepository<GithubEventStatisticHourly, Long> {

    @Query(
        """
        SELECT 
            e.type AS eventType, 
            COUNT(e) AS count
        FROM GithubEvent e
        WHERE e.createdAt >= :startTime AND e.createdAt < :endTime
        GROUP BY e.type
        """
    )
    fun findByEventTypeStatisticByTimeRange(startTime: Instant, endTime: Instant): List<EventTypeCount>

    fun findAllByCreatedAtBetween(startTime: Instant, endTime: Instant): List<GithubEventStatisticHourly>

}