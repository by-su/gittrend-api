package com.rootbly.openpulse.service

import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
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
class GithubRepoMetadataStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubRepoMetadataStatisticDailyService,
    private val topicRepository: GithubRepoTopicStatisticDailyRepository,
    private val languageRepository: GithubRepoLanguageStatisticDailyRepository,
    private val metadataRepository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        topicRepository.deleteAll()
        languageRepository.deleteAll()
        metadataRepository.deleteAll()
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should aggregate topic statistics`() {
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
        service.generateDailyRepoMetadataStatistic(targetTime)

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
    fun `generateDailyRepoMetadataStatistics should aggregate language statistics`() {
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
        service.generateDailyRepoMetadataStatistic(targetTime)

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
    fun `generateDailyRepoMetadataStatistics should only process repos within the target day`() {
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
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size)
        assertEquals(3, topicStats.first().repoCount, "Should only count repos within the target day")

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(3, languageStats.first().repoCount, "Should only count repos within the target day")
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should handle repos with no topics or language gracefully`() {
        // Given: Repos with null or empty topics/language
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, null, null),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(8), """[]""", ""),
            GithubRepoMetadataFixture.createSimple(3L, "repo3", dayStart.plusHours(16), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size, "Should only count repos with valid topics")
        assertEquals("kotlin", topicStats.first().topic)

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size, "Should only count repos with valid language")
        assertEquals("Kotlin", languageStats.first().language)
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should handle empty result set gracefully`() {
        // Given: No repos in the target day
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)

        // When
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(0, topicStats.size, "Should have no topic statistics when no repos exist")

        val languageStats = languageRepository.findAll()
        assertEquals(0, languageStats.size, "Should have no language statistics when no repos exist")
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should use current time for createdAt and updatedAt`() {
        // Given
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        val beforeExecution = Instant.now()

        // When
        service.generateDailyRepoMetadataStatistic(targetTime)

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
    fun `generateDailyRepoMetadataStatistics should handle invalid JSON gracefully`() {
        // Given: Repo with invalid JSON topics
        val targetTime = LocalDateTime.of(2024, 1, 15, 0, 0)
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", dayStart, """invalid json""", "Kotlin"),
            GithubRepoMetadataFixture.createSimple(2L, "repo2", dayStart.plusHours(12), """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size, "Should skip repos with invalid JSON")
        assertEquals("kotlin", topicStats.first().topic)

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size, "Should count both repos for language statistics")
        assertEquals(2, languageStats.first().repoCount)
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should truncate to day boundary`() {
        // Given: Target time with hour and minute components
        val targetTime = LocalDateTime.of(2024, 1, 15, 10, 45)
        val expectedDayStart = LocalDateTime.of(2024, 1, 15, 0, 0)

        val repos = listOf(
            GithubRepoMetadataFixture.createSimple(1L, "repo1", expectedDayStart, """["kotlin"]""", "Kotlin")
        )
        metadataRepository.saveAll(repos)

        // When
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(1, topicStats.size)

        val expectedInstant = expectedDayStart.toInstant(ZoneOffset.UTC)
        assertEquals(expectedInstant, topicStats.first().statisticDay, "Should truncate to day boundary")

        val languageStats = languageRepository.findAll()
        assertEquals(1, languageStats.size)
        assertEquals(expectedInstant, languageStats.first().statisticDay, "Should truncate to day boundary")
    }

    @Test
    fun `generateDailyRepoMetadataStatistics should aggregate multiple repos with same topics`() {
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
        service.generateDailyRepoMetadataStatistic(targetTime)

        // Then
        val topicStats = topicRepository.findAll()
        assertEquals(4, topicStats.size, "Should have 4 unique topics")

        val topicCounts = topicStats.associate { it.topic to it.repoCount }
        assertEquals(3, topicCounts["kotlin"], "kotlin should appear in 3 repos")
        assertEquals(3, topicCounts["backend"], "backend should appear in 3 repos")
        assertEquals(1, topicCounts["frontend"], "frontend should appear in 1 repo")
        assertEquals(1, topicCounts["java"], "java should appear in 1 repo")

        val languageStats = languageRepository.findAll()
        assertEquals(3, languageStats.size, "Should have 3 unique languages")

        val languageCounts = languageStats.associate { it.language to it.repoCount }
        assertEquals(2, languageCounts["Kotlin"], "Kotlin should appear in 2 repos")
        assertEquals(1, languageCounts["JavaScript"], "JavaScript should appear in 1 repo")
        assertEquals(1, languageCounts["Java"], "Java should appear in 1 repo")
    }
}