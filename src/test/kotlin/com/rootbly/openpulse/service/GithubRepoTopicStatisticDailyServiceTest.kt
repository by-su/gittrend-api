package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticDailyRepository
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
class GithubRepoTopicStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubRepoTopicStatisticDailyService,
    private val repository: GithubRepoTopicStatisticDailyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `retrieveGithubRepoTopicStatisticDaily should return yesterday daily statistics`() {
        // Given - Daily statistics are generated at 00:00 for the previous day
        val now = LocalDateTime.now()
        val todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC)
        val yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS)
        val twoDaysAgoStart = todayStart.minus(2, ChronoUnit.DAYS)

        val yesterdayStats = listOf(
            GithubRepoTopicStatisticDaily(
                topic = "kotlin",
                repoCount = 10,
                statisticDay = yesterdayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            GithubRepoTopicStatisticDaily(
                topic = "spring",
                repoCount = 5,
                statisticDay = yesterdayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        val twoDaysAgoStats = listOf(
            GithubRepoTopicStatisticDaily(
                topic = "java",
                repoCount = 15,
                statisticDay = twoDaysAgoStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        repository.saveAll(yesterdayStats + twoDaysAgoStats)

        // When
        val result = service.retrieveGithubRepoTopicStatisticDaily()

        // Then
        assertEquals(2, result.size, "Should return only yesterday's statistics")
        assertEquals(yesterdayStart, result[0].statisticDay)
        assertEquals(yesterdayStart, result[1].statisticDay)
    }

    @Test
    fun `generateDailyRepoTopicStatistic should aggregate topic statistics`() {
        // Given: Create repos with topics for a specific day
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin", "spring"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(5), """["kotlin", "jvm"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(15), """["spring", "boot"]""", "Java")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(4, topicStats.size, "Should have 4 unique topics")

        val kotlinTopicCount = topicStats.first { it.topic == "kotlin" }.repoCount
        assertEquals(2, kotlinTopicCount, "kotlin topic should appear in 2 repos")

        val springTopicCount = topicStats.first { it.topic == "spring" }.repoCount
        assertEquals(2, springTopicCount, "spring topic should appear in 2 repos")

        val jvmTopicCount = topicStats.first { it.topic == "jvm" }.repoCount
        assertEquals(1, jvmTopicCount, "jvm topic should appear in 1 repo")

        val bootTopicCount = topicStats.first { it.topic == "boot" }.repoCount
        assertEquals(1, bootTopicCount, "boot topic should appear in 1 repo")
    }

    @Test
    fun `generateDailyRepoTopicStatistic should only process repos within the target day`() {
        // Given: Repos created in different days
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            // Within target day (should be included)
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(12), """["kotlin"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(23), """["kotlin"]""", "Kotlin"),
            // Previous day (should be excluded)
            GithubRepoMetadataFixture.createSimple(4L, "repo4", dayStart.minusMinutes(1), """["kotlin"]""", "Kotlin"),
            // Next day (should be excluded)
            GithubRepoMetadataFixture.createSimple(5L, "repo5", dayStart.plusDays(1), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size)
        assertEquals(3, topicStats.first().repoCount, "Should only count repos within the target day")
    }

    @Test
    fun `generateDailyRepoTopicStatistic should handle repos with no topics gracefully`() {
        // Given: Repos with null or empty topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(8), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(16), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size, "Should only count repos with valid topics")
        assertEquals("kotlin", topicStats.first().topic)
    }

    @Test
    fun `generateDailyRepoTopicStatistic should handle empty result set gracefully`() {
        // Given: No repos in the target day
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(0, topicStats.size, "Should have no topic statistics when no repos exist")
    }

    @Test
    fun `generateDailyRepoTopicStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        val afterExecution = Instant.now()

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size)

        val topicStat = topicStats.first()
        assertTrue(
            topicStat.createdAt >= beforeExecution && topicStat.createdAt <= afterExecution,
            "createdAt should be the current time"
        )
        assertTrue(
            topicStat.updatedAt >= beforeExecution && topicStat.updatedAt <= afterExecution,
            "updatedAt should be the current time"
        )
    }

    @Test
    fun `generateDailyRepoTopicStatistic should handle invalid JSON gracefully`() {
        // Given: Repo with invalid JSON topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(12), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size, "Should skip repos with invalid JSON")
        assertEquals("kotlin", topicStats.first().topic)
    }

    @Test
    fun `generateDailyRepoTopicStatistic should truncate to day boundary`() {
        // Given: Target time with hour and minute components
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedDayStart = LocalDateTime.of(2024, 1, 15, 0, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedDayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size)

        val expectedInstant = expectedDayStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, topicStats.first().statisticDay, "Should truncate to day boundary")
    }

    @Test
    fun `generateDailyRepoTopicStatistic should aggregate multiple repos with same topics`() {
        // Given: Multiple repos with overlapping topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin", "backend"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(6), """["kotlin", "backend"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(12), """["kotlin", "frontend"]""", "JavaScript"),
            GithubRepoMetadataFixture.createSimple(4L, "repo4", dayStart.plusHours(18), """["java", "backend"]""", "Java")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(4, topicStats.size, "Should have 4 unique topics")

        val topicCounts = topicStats.associate { it.topic to it.repoCount }
        assertEquals(3, topicCounts["kotlin"], "kotlin should appear in 3 repos")
        assertEquals(3, topicCounts["backend"], "backend should appear in 3 repos")
        assertEquals(1, topicCounts["frontend"], "frontend should appear in 1 repo")
        assertEquals(1, topicCounts["java"], "java should appear in 1 repo")
    }
}
