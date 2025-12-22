package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.common.util.TimeRangeCalculator
import com.rootbly.openpulse.entity.statistic.language.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticHourlyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * GitHub repository language hourly statistics service
 *
 * Generates language statistics from repository metadata every hour.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoLanguageStatisticHourlyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoLanguageStatisticHourlyRepository: GithubRepoLanguageStatisticHourlyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Retrieves previous hour's language statistics
     */
    fun retrieveGithubRepoLanguageStatisticHourly(): List<GithubRepoLanguageStatisticHourly> {
        val timeRange = TimeRangeCalculator.getPreviousHourRange()
        return githubRepoLanguageStatisticHourlyRepository.findAllByStatisticHourBetween(timeRange.start, timeRange.end)
    }

    /**
     * Generates hourly language statistics from repository metadata
     */
    @Transactional
    fun generateHourlyRepoLanguageStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        val (hourStart, hourEnd) = TimeRangeCalculator.getHourRange(targetTime)
        val hourStartInstant = TimeRangeCalculator.toInstant(hourStart)

        logger.info("Generating repo language statistics for hour: $hourStart to $hourEnd")

        val repos = githubRepoMetadataRepository.findByUpdatedAtBetween(hourStart, hourEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateLanguageStatistics(repos, hourStartInstant)
    }

    /**
     * Generates hourly language statistics
     */
    private fun generateLanguageStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticHour: java.time.Instant) {
        val languageCounts = mutableMapOf<String, Int>()

        repos.forEach { repo ->
            repo.language?.takeIf { it.isNotEmpty() }?.let { language ->
                languageCounts[language] = languageCounts.getOrDefault(language, 0) + 1
            }
        }

        logger.info("Found ${languageCounts.size} unique languages")

        val now = java.time.Instant.now()
        val entities = languageCounts.map { (language, count) ->
            GithubRepoLanguageStatisticHourly(
                language = language,
                repoCount = count,
                statisticHour = statisticHour,
                createdAt = now,
                updatedAt = now
            )
        }

        githubRepoLanguageStatisticHourlyRepository.saveAll(entities)
        logger.info("Saved ${entities.size} language statistics")
    }
}
