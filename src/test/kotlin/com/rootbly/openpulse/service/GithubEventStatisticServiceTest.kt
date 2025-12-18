package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.fixture.GithubEventFixture
import com.rootbly.openpulse.fixture.GithubEventStatisticHourlyFixture
import com.rootbly.openpulse.repository.GithubEventRepository
import com.rootbly.openpulse.repository.GithubEventStatisticsHourlyRepository
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
class GithubEventStatisticServiceTest @Autowired constructor(
    private val service: GithubEventStatisticHourlyService,
    private val githubEventRepository: GithubEventRepository,
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository
) {

    companion object {
        private const val TEST_YEAR = 2025
        private const val TEST_MONTH = 12
        private const val TEST_DAY = 16
        private const val TEST_HOUR = 14
    }

    @BeforeEach
    fun clean() {
        githubEventStatisticsHourlyRepository.deleteAll()
        githubEventRepository.deleteAll()
    }

    private fun createHourStart(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0, second: Int = 0): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
            .truncatedTo(ChronoUnit.HOURS)
    }

    private fun assertStatisticHourMatches(statistic: GithubEventStatisticHourly, expectedHour: Instant) {
        assertEquals(expectedHour, statistic.statisticHour, "Statistic hour should match expected time")
        assertEquals(0, statistic.statisticHour.atZone(ZoneOffset.UTC).minute, "Minutes should be truncated to 0")
        assertEquals(0, statistic.statisticHour.atZone(ZoneOffset.UTC).second, "Seconds should be truncated to 0")
    }

    private fun assertEventCount(statistics: List<GithubEventStatisticHourly>, eventType: String, expectedCount: Int) {
        val stat = statistics.first { it.eventType == eventType }
        assertEquals(expectedCount, stat.eventCount, "Event count for $eventType should be $expectedCount")
    }

    @Test
    fun `updateHourlyStatistics should create statistics for events in the specified hour`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR, minute = 30)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStart.plus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStart.plus(20, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PullRequestEvent", hourStart.plus(30, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(2, statistics.size, "Should have 2 event types")

        assertEventCount(statistics, "PushEvent", 2)
        assertEventCount(statistics, "PullRequestEvent", 1)

        val expectedStatisticHour = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)
        statistics.forEach { assertStatisticHourMatches(it, expectedStatisticHour) }
    }

    @Test
    fun `updateHourlyStatistics should truncate time to hour`() {
        // Given - 14:30:45 should be truncated to 14:00:00
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR, minute = 30, second = 45)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val event = GithubEventFixture.createSimple(1L, "IssueEvent", hourStart.plus(15, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val expectedStatisticHour = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)
        assertStatisticHourMatches(statistics[0], expectedStatisticHour)
    }

    @Test
    fun `updateHourlyStatistics should only include events within the hour range`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStart.plus(30, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStart.minus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PushEvent", hourStart.plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should have exactly one statistic entry")
        assertEquals("PushEvent", statistics[0].eventType, "Event type should be PushEvent")
        assertEquals(1, statistics[0].eventCount, "Should only count events within the hour range")
    }

    @Test
    fun `updateHourlyStatistics should handle multiple event types correctly`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStart.plus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStart.plus(20, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PushEvent", hourStart.plus(30, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(4L, "PullRequestEvent", hourStart.plus(15, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(5L, "PullRequestEvent", hourStart.plus(25, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(6L, "IssueEvent", hourStart.plus(35, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(7L, "IssueCommentEvent", hourStart.plus(40, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(8L, "IssueCommentEvent", hourStart.plus(45, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(9L, "IssueCommentEvent", hourStart.plus(50, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(4, statistics.size, "Should have 4 different event types")

        val statMap = statistics.associateBy { it.eventType }
        assertEquals(3, statMap["PushEvent"]?.eventCount, "PushEvent count should be 3")
        assertEquals(2, statMap["PullRequestEvent"]?.eventCount, "PullRequestEvent count should be 2")
        assertEquals(1, statMap["IssueEvent"]?.eventCount, "IssueEvent count should be 1")
        assertEquals(3, statMap["IssueCommentEvent"]?.eventCount, "IssueCommentEvent count should be 3")
    }

    @Test
    fun `updateHourlyStatistics should handle empty events gracefully`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertTrue(statistics.isEmpty(), "Should not create statistics when there are no events")
    }

    @Test
    fun `updateHourlyStatistics should handle hour boundary correctly`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, hour = 0)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val event = GithubEventFixture.createSimple(1L, "PushEvent", hourStart.plus(30, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val expectedInstant = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)
        assertEquals(expectedInstant, statistics[0].statisticHour, "Statistic hour should match expected time")
    }

    @Test
    fun `updateHourlyStatistics should set createdAt and updatedAt to hourStart`() {
        // Given
        val targetHour = createHourStart(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStart = targetHour.toInstant(ZoneOffset.UTC).minus(1, ChronoUnit.HOURS)

        val event = GithubEventFixture.createSimple(1L, "PushEvent", hourStart.plus(30, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHour)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val statistic = statistics[0]
        assertEquals(statistic.statisticHour, statistic.createdAt, "createdAt should equal statisticHour")
        assertEquals(statistic.statisticHour, statistic.updatedAt, "updatedAt should equal statisticHour")
    }

    @Test
    fun `retrieveGithubEventHourlyStatistic should return statistics from previous hour`() {
        // Given
        val currentTime = LocalDateTime.now()
        val hourStart = currentTime.truncatedTo(ChronoUnit.HOURS)
        val previousHourStart = hourStart.minusHours(1)

        val startTime = previousHourStart.toInstant(ZoneOffset.UTC)
        val endTime = hourStart.toInstant(ZoneOffset.UTC)

        val stat1 = GithubEventStatisticHourlyFixture.create(
            eventType = "PushEvent",
            eventCount = 10,
            statisticHour = startTime
        )

        val stat2 = GithubEventStatisticHourlyFixture.create(
            eventType = "PullRequestEvent",
            eventCount = 5,
            statisticHour = startTime.plus(30, ChronoUnit.MINUTES)
        )

        val stat3 = GithubEventStatisticHourlyFixture.create(
            eventType = "IssueEvent",
            eventCount = 3,
            statisticHour = endTime.plus(10, ChronoUnit.MINUTES)
        )

        githubEventStatisticsHourlyRepository.saveAll(listOf(stat1, stat2, stat3))

        // When
        val result = service.retrieveGithubEventStatisticHourly()

        // Then
        assertEquals(2, result.size, "Should return 2 statistics from previous hour")
        assertTrue(result.any { it.eventType == "PushEvent" && it.eventCount == 10 }, "Should include PushEvent statistic")
        assertTrue(result.any { it.eventType == "PullRequestEvent" && it.eventCount == 5 }, "Should include PullRequestEvent statistic")
        assertTrue(result.none { it.eventType == "IssueEvent" }, "Should not include statistics outside time range")
    }

}