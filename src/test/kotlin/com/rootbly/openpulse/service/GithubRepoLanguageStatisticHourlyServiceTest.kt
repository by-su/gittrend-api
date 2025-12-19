package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticHourlyRepository
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
    private val service: GithubRepoMetadataStatisticHourlyService,
    private val repository: GithubRepoLanguageStatisticHourlyRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
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
}