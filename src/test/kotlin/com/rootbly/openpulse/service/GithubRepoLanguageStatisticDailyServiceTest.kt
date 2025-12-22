package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.statistic.language.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
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
class GithubRepoLanguageStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubRepoLanguageStatisticDailyService,
    private val repository: GithubRepoLanguageStatisticDailyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `retrieveGithubRepoLanguageStatisticDaily should return yesterday daily statistics`() {
        // Given - Daily statistics are generated at 00:00 for the previous day
        // So if current time is Dec 19 18:25, the latest complete statistics are for Dec 18 (statisticDay = Dec 18 00:00)
        val now = LocalDateTime.now()
        val todayStart = now.truncatedTo(ChronoUnit.DAYS).toInstant(ZoneOffset.UTC)
        val yesterdayStart = todayStart.minus(1, ChronoUnit.DAYS)
        val twoDaysAgoStart = todayStart.minus(2, ChronoUnit.DAYS)

        val yesterdayStats = listOf(
            GithubRepoLanguageStatisticDaily(
                language = "Kotlin",
                repoCount = 10,
                statisticDay = yesterdayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            GithubRepoLanguageStatisticDaily(
                language = "Java",
                repoCount = 5,
                statisticDay = yesterdayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        val twoDaysAgoStats = listOf(
            GithubRepoLanguageStatisticDaily(
                language = "Python",
                repoCount = 15,
                statisticDay = twoDaysAgoStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        repository.saveAll(yesterdayStats + twoDaysAgoStats)

        // When
        val result = service.retrieveGithubRepoLanguageStatisticDaily()

        // Then
        assertEquals(2, result.size, "Should return only yesterday's statistics")
        assertEquals(yesterdayStart, result[0].statisticDay)
        assertEquals(yesterdayStart, result[1].statisticDay)
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should aggregate language statistics`() {
        // Given: Create repos with languages for a specific day
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(8), """["java"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(16), """["spring"]""", "Java"),
            GithubRepoMetadataFixture.createSimple(4L, "repo4", dayStart.plusHours(20), """["python"]""", "Python")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(3, languageStats.size, "Should have 3 unique languages")

        val kotlinCount = languageStats.first { it.language == "Kotlin" }.repoCount
        assertEquals(2, kotlinCount, "Kotlin should appear in 2 repos")

        val javaCount = languageStats.first { it.language == "Java" }.repoCount
        assertEquals(1, javaCount, "Java should appear in 1 repo")

        val pythonCount = languageStats.first { it.language == "Python" }.repoCount
        assertEquals(1, pythonCount, "Python should appear in 1 repo")
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should only process repos within the target day`() {
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
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(3, languageStats.first().repoCount, "Should only count repos within the target day")
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should handle repos with no language gracefully`() {
        // Given: Repos with null or empty language
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(8), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(16), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size, "Should only count repos with valid language")
        assertEquals("Kotlin", languageStats.first().language)
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should handle empty result set gracefully`() {
        // Given: No repos in the target day
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(0, languageStats.size, "Should have no language statistics when no repos exist")
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        val afterExecution = Instant.now()

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size)

        val languageStat = languageStats.first()
        assertTrue(
            languageStat.createdAt >= beforeExecution && languageStat.createdAt <= afterExecution,
            "createdAt should be the current time"
        )
        assertTrue(
            languageStat.updatedAt >= beforeExecution && languageStat.updatedAt <= afterExecution,
            "updatedAt should be the current time"
        )
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should handle repos with invalid topics gracefully`() {
        // Given: Repo with invalid JSON topics - language statistics should still count both repos
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(12), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size, "Should count both repos for language statistics")
        assertEquals(2, languageStats.first().repoCount)
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should truncate to day boundary`() {
        // Given: Target time with hour and minute components
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedDayStart = LocalDateTime.of(2024, 1, 15, 0, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedDayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size)

        val expectedInstant = expectedDayStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, languageStats.first().statisticDay, "Should truncate to day boundary")
    }

    @Test
    fun `generateDailyRepoLanguageStatistic should aggregate multiple repos with same language`() {
        // Given: Multiple repos with overlapping languages
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
        service.generateDailyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(3, languageStats.size, "Should have 3 unique languages")

        val languageCounts = languageStats.associate { it.language to it.repoCount }
        assertEquals(2, languageCounts["Kotlin"], "Kotlin should appear in 2 repos")
        assertEquals(1, languageCounts["JavaScript"], "JavaScript should appear in 1 repo")
        assertEquals(1, languageCounts["Java"], "Java should appear in 1 repo")
    }
}