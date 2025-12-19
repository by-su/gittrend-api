package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
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
class GithubRepoLanguageStatisticDailyServiceTest @Autowired constructor(
    private val service: GithubRepoMetadataStatisticDailyService,
    private val repository: GithubRepoLanguageStatisticDailyRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
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
}