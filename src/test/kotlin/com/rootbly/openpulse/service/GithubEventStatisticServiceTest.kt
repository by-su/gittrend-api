package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.fixture.GithubEventFixture
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
import java.time.ZoneId
import java.time.temporal.ChronoUnit

@SpringBootTest
class GithubEventStatisticServiceTest @Autowired constructor(
    private val service: GithubEventStatisticHourlyService,
    private val githubEventRepository: GithubEventRepository,
    private val githubEventStatisticsHourlyRepository: GithubEventStatisticsHourlyRepository
) {

    companion object {
        private val SEOUL_ZONE = ZoneId.of("Asia/Seoul")
        private val UTC_ZONE = ZoneId.of("UTC")

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

    private fun createHourStartKST(year: Int, month: Int, day: Int, hour: Int, minute: Int = 0, second: Int = 0): LocalDateTime {
        return LocalDateTime.of(year, month, day, hour, minute, second)
            .truncatedTo(ChronoUnit.HOURS)
    }

    private fun convertKSTtoUTC(kstDateTime: LocalDateTime): Instant {
        return kstDateTime
            .atZone(SEOUL_ZONE)
            .withZoneSameInstant(UTC_ZONE)
            .toInstant()
    }

    private fun assertStatisticHourMatches(statistic: GithubEventStatisticHourly, expectedHour: Instant) {
        assertEquals(expectedHour, statistic.statisticHour, "Statistic hour should match expected time")
        assertEquals(0, statistic.statisticHour.atZone(UTC_ZONE).minute, "Minutes should be truncated to 0")
        assertEquals(0, statistic.statisticHour.atZone(UTC_ZONE).second, "Seconds should be truncated to 0")
    }

    private fun assertEventCount(statistics: List<GithubEventStatisticHourly>, eventType: String, expectedCount: Int) {
        val stat = statistics.first { it.eventType == eventType }
        assertEquals(expectedCount, stat.eventCount, "Event count for $eventType should be $expectedCount")
    }

    @Test
    fun `updateHourlyStatistics should create statistics for events in the specified hour`() {
        // Given
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR, minute = 30)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStartUTC.plus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStartUTC.plus(20, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PullRequestEvent", hourStartUTC.plus(30, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(2, statistics.size, "Should have 2 event types")

        assertEventCount(statistics, "PushEvent", 2)
        assertEventCount(statistics, "PullRequestEvent", 1)

        val expectedStatisticHour = convertKSTtoUTC(targetHourKST)
        statistics.forEach { assertStatisticHourMatches(it, expectedStatisticHour) }
    }

    @Test
    fun `updateHourlyStatistics should truncate time to hour`() {
        // Given - 14:30:45 should be truncated to 14:00:00
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR, minute = 30, second = 45)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val event = GithubEventFixture.createSimple(1L, "IssueEvent", hourStartUTC.plus(15, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val expectedStatisticHour = convertKSTtoUTC(targetHourKST)
        assertStatisticHourMatches(statistics[0], expectedStatisticHour)
    }

    @Test
    fun `updateHourlyStatistics should only include events within the hour range`() {
        // Given
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStartUTC.plus(30, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStartUTC.minus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PushEvent", hourStartUTC.plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should have exactly one statistic entry")
        assertEquals("PushEvent", statistics[0].eventType, "Event type should be PushEvent")
        assertEquals(1, statistics[0].eventCount, "Should only count events within the hour range")
    }

    @Test
    fun `updateHourlyStatistics should handle multiple event types correctly`() {
        // Given
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val events = listOf(
            GithubEventFixture.createSimple(1L, "PushEvent", hourStartUTC.plus(10, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(2L, "PushEvent", hourStartUTC.plus(20, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(3L, "PushEvent", hourStartUTC.plus(30, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(4L, "PullRequestEvent", hourStartUTC.plus(15, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(5L, "PullRequestEvent", hourStartUTC.plus(25, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(6L, "IssueEvent", hourStartUTC.plus(35, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(7L, "IssueCommentEvent", hourStartUTC.plus(40, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(8L, "IssueCommentEvent", hourStartUTC.plus(45, ChronoUnit.MINUTES)),
            GithubEventFixture.createSimple(9L, "IssueCommentEvent", hourStartUTC.plus(50, ChronoUnit.MINUTES))
        )
        githubEventRepository.saveAll(events)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

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
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertTrue(statistics.isEmpty(), "Should not create statistics when there are no events")
    }

    @Test
    fun `updateHourlyStatistics should correctly convert KST to UTC for timezone boundary`() {
        // Given - KST midnight (00:00) should be previous day 15:00 UTC
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, hour = 0)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val event = GithubEventFixture.createSimple(1L, "PushEvent", hourStartUTC.plus(30, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val expectedInstant = convertKSTtoUTC(targetHourKST)
        assertEquals(expectedInstant, statistics[0].statisticHour, "Statistic hour should match expected UTC time")

        val utcDateTime = statistics[0].statisticHour.atZone(UTC_ZONE).toLocalDateTime()
        assertEquals(TEST_DAY - 1, utcDateTime.dayOfMonth, "UTC day should be previous day")
        assertEquals(15, utcDateTime.hour, "UTC hour should be 15:00 (KST 00:00 - 9 hours)")
    }

    @Test
    fun `updateHourlyStatistics should set createdAt and updatedAt to hourStartUTC`() {
        // Given
        val targetHourKST = createHourStartKST(TEST_YEAR, TEST_MONTH, TEST_DAY, TEST_HOUR)
        val hourStartUTC = convertKSTtoUTC(targetHourKST)

        val event = GithubEventFixture.createSimple(1L, "PushEvent", hourStartUTC.plus(30, ChronoUnit.MINUTES))
        githubEventRepository.save(event)

        // When
        service.generateHourlyEventStatistics(targetHourKST)

        // Then
        val statistics = githubEventStatisticsHourlyRepository.findAll()
        assertEquals(1, statistics.size, "Should create exactly one statistic entry")

        val statistic = statistics[0]
        assertEquals(statistic.statisticHour, statistic.createdAt, "createdAt should equal statisticHour")
        assertEquals(statistic.statisticHour, statistic.updatedAt, "updatedAt should equal statisticHour")
    }

}