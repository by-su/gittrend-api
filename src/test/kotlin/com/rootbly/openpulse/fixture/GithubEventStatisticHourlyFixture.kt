package com.rootbly.openpulse.fixture

import com.rootbly.openpulse.entity.statistic.event.GithubEventStatisticHourly
import java.time.Instant

object GithubEventStatisticHourlyFixture {

    /**
     * Creates a GithubEventStatisticHourly with customizable fields for testing purposes
     */
    fun create(
        id: Long? = null,
        eventType: String = "PushEvent",
        eventCount: Int = 10,
        statisticHour: Instant = Instant.now(),
        createdAt: Instant = statisticHour,
        updatedAt: Instant = statisticHour
    ): GithubEventStatisticHourly {
        return GithubEventStatisticHourly(
            id = id,
            eventType = eventType,
            eventCount = eventCount,
            statisticHour = statisticHour,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    /**
     * Creates a simple GithubEventStatisticHourly with minimal configuration
     * Useful for tests that don't need full customization
     */
    fun createSimple(
        eventType: String = "PushEvent",
        eventCount: Int = 10,
        statisticHour: Instant = Instant.now()
    ): GithubEventStatisticHourly {
        return create(
            eventType = eventType,
            eventCount = eventCount,
            statisticHour = statisticHour
        )
    }
}