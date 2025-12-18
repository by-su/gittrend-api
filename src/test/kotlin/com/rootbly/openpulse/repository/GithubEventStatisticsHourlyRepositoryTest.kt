package com.rootbly.openpulse.repository

import com.rootbly.openpulse.fixture.GithubEventStatisticHourlyFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class GithubEventStatisticsHourlyRepositoryTest @Autowired constructor(
    private val repository: GithubEventStatisticsHourlyRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `findByBetween should return statistics within time range`() {
        // Given
        val baseTime = Instant.parse("2025-01-01T00:00:00Z")
        val hourBefore = baseTime.minusSeconds(3600)
        val hourAfter = baseTime.plusSeconds(3600)

        val stat1 = GithubEventStatisticHourlyFixture.create(
            eventType = "PushEvent",
            eventCount = 10,
            statisticHour = hourBefore
        )

        val stat2 = GithubEventStatisticHourlyFixture.create(
            eventType = "PullRequestEvent",
            eventCount = 5,
            statisticHour = baseTime
        )

        val stat3 = GithubEventStatisticHourlyFixture.create(
            eventType = "IssueEvent",
            eventCount = 3,
            statisticHour = hourAfter
        )

        repository.saveAll(listOf(stat1, stat2, stat3))

        // When
        val result = repository.findAllByCreatedAtBetween(hourBefore, baseTime.plusSeconds(1))

        // Then
        assertEquals(2, result.size, "Should return 2 statistics within time range")
        assertEquals("PushEvent", result[0].eventType)
        assertEquals("PullRequestEvent", result[1].eventType)
    }
}