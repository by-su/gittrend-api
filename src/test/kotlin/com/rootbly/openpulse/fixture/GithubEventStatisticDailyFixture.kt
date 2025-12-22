package com.rootbly.openpulse.fixture

import com.rootbly.openpulse.entity.statistic.event.GithubEventStatisticDaily
import java.time.Instant

object GithubEventStatisticDailyFixture {

    /**
     * Creates a GithubEventStatisticDaily with customizable fields for testing purposes
     */
    fun create(
        id: Long? = null,
        eventType: String = "PushEvent",
        eventCount: Int = 10,
        statisticDay: Instant = Instant.now(),
        createdAt: Instant = statisticDay,
        updatedAt: Instant = statisticDay
    ): GithubEventStatisticDaily {
        return GithubEventStatisticDaily(
            id = id,
            eventType = eventType,
            eventCount = eventCount,
            statisticDay = statisticDay,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    /**
     * Creates a simple GithubEventStatisticDaily with minimal configuration
     * Useful for tests that don't need full customization
     */
    fun createSimple(
        eventType: String = "PushEvent",
        eventCount: Int = 10,
        statisticDay: Instant = Instant.now()
    ): GithubEventStatisticDaily {
        return create(
            eventType = eventType,
            eventCount = eventCount,
            statisticDay = statisticDay
        )
    }
}