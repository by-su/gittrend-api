package com.rootbly.openpulse.repository

import com.rootbly.openpulse.fixture.GithubEventStatisticDailyFixture
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class GithubEventStatisticDailyRepositoryTest @Autowired constructor(
    private val repository: GithubEventStatisticDailyRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `findAllByCreatedAtBetween should return statistics within time range`() {
        // Given
        val baseTime = Instant.parse("2025-01-01T00:00:00Z")
        val dayBefore = baseTime.minusSeconds(86400)
        val dayAfter = baseTime.plusSeconds(86400)

        val stat1 = GithubEventStatisticDailyFixture.create(
            eventType = "PushEvent",
            eventCount = 10,
            createdAt = dayBefore
        )

        val stat2 = GithubEventStatisticDailyFixture.create(
            eventType = "PullRequestEvent",
            eventCount = 5,
            createdAt = baseTime
        )

        val stat3 = GithubEventStatisticDailyFixture.create(
            eventType = "IssueEvent",
            eventCount = 3,
            createdAt = dayAfter
        )

        repository.saveAll(listOf(stat1, stat2, stat3))

        // When
        val result = repository.findAllByCreatedAtBetween(dayBefore, baseTime.plusSeconds(1))

        // Then
        assertEquals(2, result.size, "Should return 2 statistics within time range")
        assertEquals("PushEvent", result[0].eventType)
        assertEquals("PullRequestEvent", result[1].eventType)
    }
}