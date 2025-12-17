package com.rootbly.openpulse.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.entity.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticHourlyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticHourlyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * GitHub repository metadata hourly statistics generation service
 *
 * Generates topic and language statistics from repository metadata every hour.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoMetadataStatisticHourlyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoTopicStatisticHourlyRepository: GithubRepoTopicStatisticHourlyRepository,
    private val githubRepoLanguageStatisticHourlyRepository: GithubRepoLanguageStatisticHourlyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates hourly topic and language statistics from repository metadata
     *
     * @param targetTime Target time (defaults to previous hour)
     */
    @Transactional
    fun generateHourlyRepoMetadataStatistics(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        val hourStart = targetTime.truncatedTo(ChronoUnit.HOURS)
        val hourEnd = hourStart.plusHours(1)
        val hourStartInstant = hourStart.toInstant(ZoneOffset.UTC)

        logger.info("Generating repo metadata statistics for hour: $hourStart to $hourEnd")

        val repos = githubRepoMetadataRepository.findByCreatedAtBetween(hourStart, hourEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateTopicStatistics(repos, hourStartInstant)
        generateLanguageStatistics(repos, hourStartInstant)
    }

    /**
     * Generates hourly topic statistics
     */
    private fun generateTopicStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticHour: Instant) {
        val topicCounts = mutableMapOf<String, Int>()

        repos.forEach { repo ->
            if (!repo.topics.isNullOrEmpty()) {
                try {
                    // MySQL JSON column may return double-encoded JSON string
                    var topicsJson = repo.topics!!

                    // If the value starts and ends with quotes, it's double-encoded, so decode once
                    if (topicsJson.startsWith("\"") && topicsJson.endsWith("\"")) {
                        topicsJson = objectMapper.readValue(topicsJson, String::class.java)
                    }

                    val topics = objectMapper.readValue(topicsJson, object : TypeReference<List<String>>() {})
                    topics.forEach { topic ->
                        topicCounts[topic] = topicCounts.getOrDefault(topic, 0) + 1
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to parse topics JSON for repo ${repo.repoId}. Topics value: '${repo.topics}', Error: ${e.message}")
                }
            }
        }

        logger.info("Found ${topicCounts.size} unique topics")

        val now = Instant.now()
        val entities = topicCounts.map { (topic, count) ->
            GithubRepoTopicStatisticHourly(
                topic = topic,
                repoCount = count,
                statisticHour = statisticHour,
                createdAt = now,
                updatedAt = now
            )
        }

        githubRepoTopicStatisticHourlyRepository.saveAll(entities)
        logger.info("Saved ${entities.size} topic statistics")
    }

    /**
     * Generates hourly language statistics
     */
    private fun generateLanguageStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticHour: Instant) {
        val languageCounts = mutableMapOf<String, Int>()

        repos.forEach { repo ->
            repo.language?.takeIf { it.isNotEmpty() }?.let { language ->
                languageCounts[language] = languageCounts.getOrDefault(language, 0) + 1
            }
        }

        logger.info("Found ${languageCounts.size} unique languages")

        val now = Instant.now()
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