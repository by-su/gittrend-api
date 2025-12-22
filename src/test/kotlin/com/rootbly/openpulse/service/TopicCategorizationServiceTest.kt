package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticHourlyRepository
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.cache.CacheManager
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import kotlin.test.assertEquals

@SpringBootTest
class TopicCategorizationServiceTest @Autowired constructor(
    private val cacheManager: CacheManager,
    private val categorizationService: TopicCategorizationService,
    private val hourlyRepository: GithubRepoTopicStatisticHourlyRepository,
    private val dailyRepository: GithubRepoTopicStatisticDailyRepository,
    private val hourlyService: GithubRepoTopicStatisticHourlyService,
    private val dailyService: GithubRepoTopicStatisticDailyService
) {

    @BeforeEach
    fun setup() {
        // Clear repositories
        hourlyRepository.deleteAll()
        dailyRepository.deleteAll()

        // Clear all caches
        cacheManager.cacheNames.forEach { cacheName ->
            cacheManager.getCache(cacheName)?.clear()
        }
    }

    @Test
    fun `TopicCategorizationService should inject GithubRepoTopicStatisticHourlyService`() {
        // Then
        assertNotNull(hourlyService)
    }

    @Test
    fun `TopicCategorizationService should inject GithubRepoTopicStatisticDailyService`() {
        // Then
        assertNotNull(dailyService)
    }

    @Test
    fun `analyzeHourlyCategoryStats should fetch hourly stats and analyze`() {
        // Given
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS).toInstant(ZoneOffset.UTC)
        val previousHourStart = currentHourStart.minus(1, ChronoUnit.HOURS)

        val hourlyStats = listOf(
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
        hourlyRepository.saveAll(hourlyStats)

        // When
        val result = categorizationService.analyzeHourlyCategoryStats()

        // Then
        assertNotNull(result)
    }

    @Test
    fun `analyzeHourlyCategoryStats should cache results`() {
        // Given
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS).toInstant(ZoneOffset.UTC)
        val previousHourStart = currentHourStart.minus(1, ChronoUnit.HOURS)

        val hourlyStats = listOf(
            GithubRepoTopicStatisticHourly(
                topic = "kotlin",
                repoCount = 10,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        hourlyRepository.saveAll(hourlyStats)

        // When - First call
        categorizationService.analyzeHourlyCategoryStats()

        // Then - Check cache
        val cache = cacheManager.getCache("hourlyStatistics")
        assertNotNull(cache)
        val cachedValue = cache?.get("topicCategorization")
        assertNotNull(cachedValue)
    }

    @Test
    fun `analyzeHourlyCategoryStats should return cached data on subsequent calls`() {
        // Given
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(ChronoUnit.HOURS).toInstant(ZoneOffset.UTC)
        val previousHourStart = currentHourStart.minus(1, ChronoUnit.HOURS)

        val hourlyStats = listOf(
            GithubRepoTopicStatisticHourly(
                topic = "kotlin",
                repoCount = 10,
                statisticHour = previousHourStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        hourlyRepository.saveAll(hourlyStats)

        // When - First call
        val firstResult = categorizationService.analyzeHourlyCategoryStats()

        // Clear repository to ensure second call uses cache
        hourlyRepository.deleteAll()

        // When - Second call (should use cache)
        val secondResult = categorizationService.analyzeHourlyCategoryStats()

        // Then - Results should be equal
        assertEquals(firstResult, secondResult)
    }

    @Test
    fun `analyzeDailyCategoryStats should fetch daily stats and analyze`() {
        // Given
        val now = LocalDateTime.now()
        val todayStart = now.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC)

        val dailyStats = listOf(
            GithubRepoTopicStatisticDaily(
                topic = "kotlin",
                repoCount = 100,
                statisticDay = todayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            ),
            GithubRepoTopicStatisticDaily(
                topic = "spring",
                repoCount = 50,
                statisticDay = todayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        dailyRepository.saveAll(dailyStats)

        // When
        val result = categorizationService.analyzeDailyCategoryStats()

        // Then
        assertNotNull(result)
    }

    @Test
    fun `analyzeDailyCategoryStats should cache results`() {
        // Given
        val now = LocalDateTime.now()
        val todayStart = now.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC)

        val dailyStats = listOf(
            GithubRepoTopicStatisticDaily(
                topic = "kotlin",
                repoCount = 100,
                statisticDay = todayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        dailyRepository.saveAll(dailyStats)

        // When - First call
        categorizationService.analyzeDailyCategoryStats()

        // Then - Check cache
        val cache = cacheManager.getCache("dailyStatistics")
        assertNotNull(cache)
        val cachedValue = cache?.get("topicCategorization")
        assertNotNull(cachedValue)
    }

    @Test
    fun `analyzeDailyCategoryStats should return cached data on subsequent calls`() {
        // Given
        val now = LocalDateTime.now()
        val todayStart = now.toLocalDate().atStartOfDay().toInstant(ZoneOffset.UTC)

        val dailyStats = listOf(
            GithubRepoTopicStatisticDaily(
                topic = "kotlin",
                repoCount = 100,
                statisticDay = todayStart,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )
        )
        dailyRepository.saveAll(dailyStats)

        // When - First call
        val firstResult = categorizationService.analyzeDailyCategoryStats()

        // Clear repository to ensure second call uses cache
        dailyRepository.deleteAll()

        // When - Second call (should use cache)
        val secondResult = categorizationService.analyzeDailyCategoryStats()

        // Then - Results should be equal
        assertEquals(firstResult, secondResult)
    }
}
