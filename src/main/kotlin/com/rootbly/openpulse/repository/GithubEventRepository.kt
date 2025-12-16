package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.payload.EventTypeCount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.Instant

interface GithubEventRepository: JpaRepository<GithubEvent, Long> {
    @Query("""
        SELECT
            e.type as eventType,
            COUNT(e) as count
        FROM GithubEvent e
        WHERE e.createdAt >= :startTime AND e.createdAt < :endTime
        GROUP BY e.type
    """)
    fun findByEventTypeStatisticsByTimeRange(
        startTime: Instant,
        endTime: Instant
    ): List<EventTypeCount>

}