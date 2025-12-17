package com.rootbly.openpulse.service

import com.rootbly.openpulse.fixture.GithubEventFixture
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
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@SpringBootTest
class GithubEventStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubEventStatisticDailyService,
    private val dailyRepository: GithubEventStatisticDailyRepository,
    private val eventRepository: GithubEventRepository
) {

    companion object {
        private val SEOUL_ZONE = ZoneId.of("Asia/Seoul")
        private val UTC_ZONE = ZoneId.of("UTC")
    }

    @BeforeEach
    fun clean() {
        dailyRepository.deleteAll()
        eventRepository.deleteAll()
    }

    @Test
    fun `generateDailyEventStatistic should aggregate hourly statistics into daily statistics`() {
        // Given: Create events for a specific day in UTC
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStartUTC = targetDayKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        // Create events throughout the day
        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStartUTC.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStartUTC.plus(5, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(3L, "PushEvent", dayStartUTC.plus(10, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(4L, "IssueCommentEvent", dayStartUTC.plus(2, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(5L, "IssueCommentEvent", dayStartUTC.plus(8, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(6L, "WatchEvent", dayStartUTC.plus(15, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDayKST)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(3, statistics.size, "Should have 3 event types")

        val pushStats = statistics.first { it.eventType == "PushEvent" }
        assertEquals(3, pushStats.eventCount)
        assertEquals(dayStartUTC, pushStats.statisticDay)

        val issueStats = statistics.first { it.eventType == "IssueCommentEvent" }
        assertEquals(2, issueStats.eventCount)

        val watchStats = statistics.first { it.eventType == "WatchEvent" }
        assertEquals(1, watchStats.eventCount)
    }

    @Test
    fun `generateDailyEventStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStartUTC = targetDayKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStartUTC.plus(1, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        val beforeExecution = Instant.now()

        // When
        service.generateDailyEventStatistic(targetDayKST)

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
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 0, 0)

        // When
        service.generateDailyEventStatistic(targetDayKST)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(0, statistics.size, "Should have no statistics when no events exist")
    }

    @Test
    fun `generateDailyEventStatistic should only aggregate events within the target day`() {
        // Given: Events on different days
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStartUTC = targetDayKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val events = listOf(
            // Events on target day (should be included)
            GithubEventFixture.createSimple(1L, "PushEvent", dayStartUTC.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStartUTC.plus(23, ChronoUnit.HOURS)),
            // Events on previous day (should be excluded)
            GithubEventFixture.createSimple(3L, "PushEvent", dayStartUTC.minus(1, ChronoUnit.HOURS)),
            // Events on next day (should be excluded)
            GithubEventFixture.createSimple(4L, "PushEvent", dayStartUTC.plus(25, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDayKST)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)

        val pushStats = statistics.first()
        assertEquals("PushEvent", pushStats.eventType)
        assertEquals(2, pushStats.eventCount, "Should only count events within the target day")
    }

    @Test
    fun `generateDailyEventStatistic should correctly convert KST to UTC`() {
        // Given: Target day in KST
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 14, 30) // 2024-01-15 14:30 KST
        val expectedDayStartUTC = LocalDateTime.of(2024, 1, 15, 0, 0)
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        // Create event at the start of the day in UTC
        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", expectedDayStartUTC.plus(1, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDayKST)

        // Then
        val statistics = dailyRepository.findAll()
        assertEquals(1, statistics.size)

        val stat = statistics.first()
        assertEquals(expectedDayStartUTC, stat.statisticDay, "Should store the day start in UTC")
    }

    @Test
    fun `generateDailyEventStatistic should handle multiple event types correctly`() {
        // Given: Various event types
        val targetDayKST = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStartUTC = targetDayKST
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStartUTC.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStartUTC.plus(2, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(3L, "IssueCommentEvent", dayStartUTC.plus(3, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(4L, "PullRequestEvent", dayStartUTC.plus(4, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(5L, "WatchEvent", dayStartUTC.plus(5, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(6L, "WatchEvent", dayStartUTC.plus(6, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(7L, "WatchEvent", dayStartUTC.plus(7, ChronoUnit.HOURS))
        )
        eventRepository.saveAll(events)

        // When
        service.generateDailyEventStatistic(targetDayKST)

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
        val dayStartUTC = yesterday.truncatedTo(ChronoUnit.DAYS)
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", dayStartUTC.plus(1, ChronoUnit.HOURS)),
            GithubEventFixture.createSimple(2L, "PushEvent", dayStartUTC.plus(5, ChronoUnit.HOURS))
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
}