package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticHourlyRepository
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

@SpringBootTest
class GithubRepoTopicStatisticHourlyServiceTest @Autowired constructor(
    private val service: GithubRepoTopicStatisticHourlyService,
    private val repository: GithubRepoTopicStatisticHourlyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `retrieveGithubRepoTopicStatisticHourly should return previous hour statistics`() {
        // Given - Hourly statistics are generated at the top of each hour for the previous hour
        // So if current time is 18:20, the latest complete statistics are for 17:00 (statisticHour = 17:00)
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS).toInstant(ZoneOffset.UTC)
        val previousHourStart = currentHourStart.minus(1, ChronoUnit.HOURS)
        val twoHoursAgoStart = currentHourStart.minus(2, ChronoUnit.HOURS)

        val previousHourStats = listOf(
            GithubRepoTopicStatisticHourly(
                topic = "kotlin",
                repoCount = 10,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            GithubRepoTopicStatisticHourly(
                topic = "spring",
                repoCount = 5,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        val twoHoursAgoStats = listOf(
            GithubRepoTopicStatisticHourly(
                topic = "java",
                repoCount = 15,
                statisticHour = twoHoursAgoStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        repository.saveAll(previousHourStats + twoHoursAgoStats)

        // When
        val result = service.retrieveGithubRepoTopicStatisticHourly()

        // Then
        assertEquals(2, result.size, "Should return only previous hour's statistics")
        assertEquals(previousHourStart, result[0].statisticHour)
        assertEquals(previousHourStart, result[1].statisticHour)
    }

    @Test
    fun `generateHourlyRepoTopicStatistic should aggregate topic statistics`() {
        // Given: Create repos with topics for a specific hour
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin", "spring"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """["kotlin", "jvm"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.plusMinutes(45), """["spring", "boot"]""", "Java")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

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
    fun `generateHourlyRepoTopicStatistic should only process repos within the target hour`() {
        // Given: Repos created in different hours
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            // Within target hour (should be included)
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """["kotlin"]""", "Kotlin"),
            // Previous hour (should be excluded)
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.minusMinutes(1), """["kotlin"]""", "Kotlin"),
            // Next hour (should be excluded)
            GithubRepoMetadataFixture.createSimple(4L, "repo4", hourStart.plusHours(1), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size)
        assertEquals(2, topicStats.first().repoCount, "Should only count repos within the target hour")
    }

    @Test
    fun `generateHourlyRepoTopicStatistic should handle repos with no topics gracefully`() {
        // Given: Repos with null or empty topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.plusMinutes(45), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size, "Should only count repos with valid topics")
        assertEquals("kotlin", topicStats.first().topic)
    }

    @Test
    fun `generateHourlyRepoTopicStatistic should handle empty result set gracefully`() {
        // Given: No repos in the target hour
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(0, topicStats.size, "Should have no topic statistics when no repos exist")
    }

    @Test
    fun `generateHourlyRepoTopicStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

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
    fun `generateHourlyRepoTopicStatistic should handle invalid JSON gracefully`() {
        // Given: Repo with invalid JSON topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size, "Should skip repos with invalid JSON")
        assertEquals("kotlin", topicStats.first().topic)
    }

    @Test
    fun `generateHourlyRepoTopicStatistic should truncate to hour boundary`() {
        // Given: Target time with minute component
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedHourStart = LocalDateTime.of(2024, 1, 15, 10, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedHourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoTopicStatistic(targetTime)

        // Then
        val topicStats = repository.findAll()
        assertEquals(1, topicStats.size)

        val expectedInstant = expectedHourStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, topicStats.first().statisticHour, "Should truncate to hour boundary")
    }
}
