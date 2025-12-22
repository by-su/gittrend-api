package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.common.util.TimeRangeCalculator
import com.rootbly.openpulse.entity.statistic.language.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * GitHub repository language daily statistics service
 *
 * Generates language statistics from repository metadata daily.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoLanguageStatisticDailyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoLanguageStatisticDailyRepository: GithubRepoLanguageStatisticDailyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Retrieves yesterday's daily language statistics
     */
    @Cacheable(value = ["dailyStatistics"], key = "'repoLanguage'")
    fun retrieveGithubRepoLanguageStatisticDaily(): List<GithubRepoLanguageStatisticDaily> {
        val timeRange = TimeRangeCalculator.getPreviousDayRange()
        return githubRepoLanguageStatisticDailyRepository.findAllByStatisticDayBetween(timeRange.start, timeRange.end)
    }

    /**
     * Generates daily language statistics from repository metadata
     */
    @Transactional
    fun generateDailyRepoLanguageStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val (dayStart, dayEnd) = TimeRangeCalculator.getDayRange(targetTime)
        val dayStartInstant = TimeRangeCalculator.toInstant(dayStart)

        logger.info("Generating repo language statistics for day: $dayStart to $dayEnd")

        val repos = githubRepoMetadataRepository.findByUpdatedAtBetween(dayStart, dayEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateLanguageStatistics(repos, dayStartInstant)
    }

    /**
     * Generates daily language statistics
     */
    private fun generateLanguageStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticDay: java.time.Instant) {
        val languageCounts = mutableMapOf<String, Int>()

        repos.forEach { repo ->
            repo.language?.takeIf { it.isNotEmpty() }?.let { language ->
                languageCounts[language] = languageCounts.getOrDefault(language, 0) + 1
            }
        }

        logger.info("Found ${languageCounts.size} unique languages")

        val now = java.time.Instant.now()
        val entities = languageCounts.map { (language, count) ->
            GithubRepoLanguageStatisticDaily(
                language = language,
                repoCount = count,
                statisticDay = statisticDay,
                createdAt = now,
                updatedAt = now
            )
        }

        githubRepoLanguageStatisticDailyRepository.saveAll(entities)
        logger.info("Saved ${entities.size} language statistics")
    }
}
