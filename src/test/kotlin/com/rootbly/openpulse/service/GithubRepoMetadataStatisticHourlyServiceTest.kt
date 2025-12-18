package com.rootbly.openpulse.service

import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticHourlyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticHourlyRepository
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
class GithubRepoMetadataStatisticHourlyServiceTest @Autowired constructor(
    private val service: GithubRepoMetadataStatisticHourlyService,
    private val topicRepository: GithubRepoTopicStatisticHourlyRepository,
    private val languageRepository: GithubRepoLanguageStatisticHourlyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        topicRepository.deleteAll()
        languageRepository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should aggregate topic statistics`() {
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
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
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
    fun `generateHourlyRepoMetadataStatistics should aggregate language statistics`() {
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
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val languageStats = languageRepository.findAll()
        assertEquals(3, languageStats.size, "Should have 3 unique languages")

        val kotlinCount = languageStats.first { it.language == "Kotlin" }.repoCount
        assertEquals(2, kotlinCount, "Kotlin should appear in 2 repos")

        val javaCount = languageStats.first { it.language == "Java" }.repoCount
        assertEquals(1, javaCount, "Java should appear in 1 repo")

        val pythonCount = languageStats.first { it.language == "Python" }.repoCount
        assertEquals(1, pythonCount, "Python should appear in 1 repo")
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should only process repos within the target hour`() {
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
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size)
        assertEquals(2, topicStats.first().repoCount, "Should only count repos within the target hour")

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(2, languageStats.first().repoCount, "Should only count repos within the target hour")
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should handle repos with no topics or language gracefully`() {
        // Given: Repos with null or empty topics/language
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", hourStart.plusMinutes(45), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size, "Should only count repos with valid topics")
        assertEquals("kotlin", topicStats.first().topic)

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size, "Should only count repos with valid language")
        assertEquals("Kotlin", languageStats.first().language)
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should handle empty result set gracefully`() {
        // Given: No repos in the target hour
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)

        // When
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(0, topicStats.size, "Should have no topic statistics when no repos exist")

        val languageStats = languageRepository.findAll()
        assertEquals(0, languageStats.size, "Should have no language statistics when no repos exist")
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateHourlyRepoMetadataStatistic(targetTime)

        val afterExecution = Instant.now()

        // Then
        val topicStats = topicRepository.findAll()
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

        val languageStats = languageRepository.findAll()
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
    fun `generateHourlyRepoMetadataStatistics should handle invalid JSON gracefully`() {
        // Given: Repo with invalid JSON topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 0)
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", hourStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", hourStart.plusMinutes(30), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size, "Should skip repos with invalid JSON")
        assertEquals("kotlin", topicStats.first().topic)

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size, "Should count both repos for language statistics")
        assertEquals(2, languageStats.first().repoCount)
    }

    @Test
    fun `generateHourlyRepoMetadataStatistics should truncate to hour boundary`() {
        // Given: Target time with minute component
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedHourStart = LocalDateTime.of(2024, 1, 15, 10, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedHourStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateHourlyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size)

        val expectedInstant = expectedHourStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, topicStats.first().statisticHour, "Should truncate to hour boundary")

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(expectedInstant, languageStats.first().statisticHour, "Should truncate to hour boundary")
    }

}