package com.rootbly.openpulse.service

import com.rootbly.openpulse.fixture.GithubEventFixture
import com.rootbly.openpulse.fixture.GithubEventStatisticDailyFixture
import com.rootbly.openpulse.repository.GithubEventRepository
import com.rootbly.openpulse.repository.GithubEventStatisticDailyRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

@SpringBootTest
class GithubEventStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubEventStatisticDailyService,
    private val dailyRepository: GithubEventStatisticDailyRepository,
    private val eventRepository: GithubEventRepository
) {

    @BeforeEach
    fun clean() {
        dailyRepository.deleteAll()
        eventRepository.deleteAll()
    }

    @Test
    fun `generateDailyEventStatistic should aggregate hourly statistics into daily statistics`() {
        // Given: Create events for a specific day
        val targetDay = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetDay.toInstant(ZoneOffset.UTC)

        // Create events throughout the day
        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStart.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStart.plus(5, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(3L, "PushEvent", dayStart.plus(10, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(4L, "IssueCommentEvent", dayStart.plus(2, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(5L, "IssueCommentEvent", dayStart.plus(8, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(6L, "WatchEvent", dayStart.plus(15, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDay)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(3, statistics.size, "Should have 3 event types")

        val pushStats = statistics.first { it.eventType == "PushEvent" }
        assertEquals(3, pushStats.eventCount)
        assertEquals(dayStart, pushStats.statisticDay)

        val issueStats = statistics.first { it.eventType == "IssueCommentEvent" }
        assertEquals(2, issueStats.eventCount)

        val watchStats = statistics.first { it.eventType == "WatchEvent" }
        assertEquals(1, watchStats.eventCount)
    }

    @Test
    fun `generateDailyEventStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetDay = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetDay.toInstant(ZoneOffset.UTC)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStart.plus(1, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        val beforeExecution = Instant.now()

        // When
        service.generateDailyEventStatistic(targetDay)

        val afterExecution = Instant.now()

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)

        val stat = statistics.first()
        assertTrue(
            stat.createdAt >= beforeExecution && stat.createdAt <= afterExecution,
            "createdAt should be the current time, not the statistic day"
        )
        assertTrue(
            stat.updatedAt >= beforeExecution && stat.updatedAt <= afterExecution,
            "updatedAt should be the current time, not the statistic day"
        )
    }

    @Test
    fun `generateDailyEventStatistic should handle empty statistics gracefully`() {
        // Given: No events for the target day
        val targetDay = LocalDateTime.of(2024, 1, 15, 0, 0)

        // When
        service.generateDailyEventStatistic(targetDay)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(0, statistics.size, "Should have no statistics when no events exist")
    }

    @Test
    fun `generateDailyEventStatistic should only aggregate events within the target day`() {
        // Given: Events on different days
        val targetDay = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetDay.toInstant(ZoneOffset.UTC)

        val events = listOf(
            // Events on target day (should be included)
            GithubEventFixture.createSimple(1L, "PushEvent", dayStart.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStart.plus(23, ChronoUnit.HOURS)),
            // Events on previous day (should be excluded)
            GithubEventFixture.createSimple(3L, "PushEvent", dayStart.minus(1, ChronoUnit.HOURS)),
            // Events on next day (should be excluded)
            GithubEventFixture.createSimple(4L, "PushEvent", dayStart.plus(25, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDay)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)

        val pushStats = statistics.first()
        assertEquals("PushEvent", pushStats.eventType)
        assertEquals(2, pushStats.eventCount, "Should only count events within the target day")
    }

    @Test
    fun `generateDailyEventStatistic should truncate to day boundary`() {
        // Given: Target day with time component
        val targetDay = LocalDateTime.of(2024, 1, 15, 14, 30)
        val expectedDayStart = LocalDateTime.of(2024, 1, 15, 0, 0).toInstant(ZoneOffset.UTC)

        // Create event at the start of the day
        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", expectedDayStart.plus(1, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDay)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)

        val stat = statistics.first()
        assertEquals(expectedDayStart, stat.statisticDay, "Should truncate to day boundary")
    }

    @Test
    fun `generateDailyEventStatistic should handle multiple event types correctly`() {
        // Given: Various event types
        val targetDay = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetDay.toInstant(ZoneOffset.UTC)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStart.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStart.plus(2, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(3L, "IssueCommentEvent", dayStart.plus(3, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(4L, "PullRequestEvent", dayStart.plus(4, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(5L, "WatchEvent", dayStart.plus(5, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(6L, "WatchEvent", dayStart.plus(6, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(7L, "WatchEvent", dayStart.plus(7, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDay)

        // Then
        val statistics = dailyRepository.findAll().sortedBy { it.eventType }
        assertEquals(4, statistics.size)

        assertEquals("IssueCommentEvent", statistics[0].eventType)
        assertEquals(1, statistics[0].eventCount)

        assertEquals("PullRequestEvent", statistics[1].eventType)
        assertEquals(1, statistics[1].eventCount)

        assertEquals("PushEvent", statistics[2].eventType)
        assertEquals(2, statistics[2].eventCount)

        assertEquals("WatchEvent", statistics[3].eventType)
        assertEquals(3, statistics[3].eventCount)
    }

    @Test
    fun `generateDailyEventStatistic should use default parameter for yesterday`() {
        // Given: Events from yesterday
        val yesterday = LocalDateTime.now().minusDays(1)
        val dayStart = yesterday.truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStart.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStart.plus(5, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When: Call without parameter (should default to yesterday)
        service.generateDailyEventStatistic()

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)
        assertEquals("PushEvent", statistics.first().eventType)
        assertEquals(2, statistics.first().eventCount)
    }

    @Test
    fun `retrieveDaily should return statistics from previous day window`() {
        // Given
        val currentTime = LocalDateTime.now()
        val dayStart = currentTime.truncatedTo(ChronoUnit.DAYS)
        val previousDayStart = dayStart.minusDays(1)

        val startTime = previousDayStart.toInstant(ZoneOffset.UTC)
        val endTime = dayStart.toInstant(ZoneOffset.UTC)

        val stat1 = GithubEventStatisticDailyFixture.create(
            eventType = "PushEvent",
            eventCount = 10,
            createdAt = startTime.plus(1, ChronoUnit.HOURS)
        )

        val stat2 = GithubEventStatisticDailyFixture.create(
            eventType = "PullRequestEvent",
            eventCount = 5,
            createdAt = startTime.plus(12, ChronoUnit.HOURS)
        )

        val stat3 = GithubEventStatisticDailyFixture.create(
            eventType = "IssueEvent",
            eventCount = 3,
            createdAt = endTime.plus(1, ChronoUnit.HOURS)
        )

        dailyRepository.saveAll(listOf(stat1, stat2, stat3))

        // When
        val result = service.retrieveDaily()

        // Then
        assertEquals(2, result.size, "Should return 2 statistics from previous day window")
        assertTrue(result.any { it.eventType == "PushEvent" }, "Should include PushEvent")
        assertTrue(result.any { it.eventType == "PullRequestEvent" }, "Should include PullRequestEvent")
        assertTrue(result.none { it.eventType == "IssueEvent" }, "Should not include IssueEvent from current day")
    }
}