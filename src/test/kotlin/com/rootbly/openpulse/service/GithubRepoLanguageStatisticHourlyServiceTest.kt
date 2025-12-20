package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticHourlyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
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
class GithubRepoLanguageStatisticHourlyServiceTest @Autowired constructor(
    private val service: GithubRepoLanguageStatisticHourlyService,
    private val repository: GithubRepoLanguageStatisticHourlyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `retrieveGithubRepoLanguageStatisticHourly should return previous hour statistics`() {
        // Given - Hourly statistics are generated at the top of each hour for the previous hour
        // So if current time is 18:20, the latest complete statistics are for 17:00 (statisticHour = 17:00)
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS).toInstant(ZoneOffset.UTC)
        val previousHourStart = currentHourStart.minus(1, ChronoUnit.HOURS)
        val twoHoursAgoStart = currentHourStart.minus(2, ChronoUnit.HOURS)

        val previousHourStats = listOf(
            GithubRepoLanguageStatisticHourly(
                language = "Kotlin",
                repoCount = 10,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            GithubRepoLanguageStatisticHourly(
                language = "Java",
                repoCount = 5,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        val twoHoursAgoStats = listOf(
            GithubRepoLanguageStatisticHourly(
                language = "Python",
                repoCount = 15,
                statisticHour = twoHoursAgoStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )

        repository.saveAll(previousHourStats + twoHoursAgoStats)

        // When
        val result = service.retrieveGithubRepoLanguageStatisticHourly()

        // Then
        assertEquals(2, result.size, "Should return only previous hour's statistics")
        assertEquals(previousHourStart, result[0].statisticHour)
        assertEquals(previousHourStart, result[1].statisticHour)
    }

    @Test
    fun `generateHourlyRepoLanguageStatistic should aggregate language statistics`() {
        // Given: Create repos with languages for a specific hour
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(20), """["java"]""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.plusMinutes(40), """["spring"]""", "Java"),
            GithubRepoMetadataFixture.createSimple(4L, "repo4", hourStart.plusMinutes(50), """["python"]""", "Python")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

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
    fun `generateHourlyRepoLanguageStatistic should only process repos within the target hour`() {
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
        service.generateHourlyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(2, languageStats.first().repoCount, "Should only count repos within the target hour")
    }

    @Test
    fun `generateHourlyRepoLanguageStatistic should handle repos with no language gracefully`() {
        // Given: Repos with null or empty language
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.plusMinutes(45), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size, "Should only count repos with valid language")
        assertEquals("Kotlin", languageStats.first().language)
    }

    @Test
    fun `generateHourlyRepoLanguageStatistic should handle empty result set gracefully`() {
        // Given: No repos in the target hour
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(0, languageStats.size, "Should have no language statistics when no repos exist")
    }

    @Test
    fun `generateHourlyRepoLanguageStatistic should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

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
    fun `generateHourlyRepoLanguageStatistic should handle repos with invalid topics gracefully`() {
        // Given: Repo with invalid JSON topics - language statistics should still count both repos
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size, "Should count both repos for language statistics")
        assertEquals(2, languageStats.first().repoCount)
    }

    @Test
    fun `generateHourlyRepoLanguageStatistic should truncate to hour boundary`() {
        // Given: Target time with minute component
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedHourStart = LocalDateTime.of(2024, 1, 15, 10, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedHourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoLanguageStatistic(targetTime)

        // Then
        val languageStats = repository.findAll()
        assertEquals(1, languageStats.size)

        val expectedInstant = expectedHourStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, languageStats.first().statisticHour, "Should truncate to hour boundary")
    }
}