package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticHourlyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * GitHub repository topic hourly statistics service
 *
 * Generates topic statistics from repository metadata every hour.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoTopicStatisticHourlyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoTopicStatisticHourlyRepository: GithubRepoTopicStatisticHourlyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Retrieves previous hour's topic statistics
     */
    fun retrieveGithubRepoTopicStatisticHourly(): List<GithubRepoTopicStatisticHourly> {
        val now = LocalDateTime.now()
        val currentHourStart = now.truncatedTo(java.time.temporal.ChronoUnit.HOURS)
        val previousHourStart = currentHourStart.minusHours(1)
        val previousHourEnd = currentHourStart

        val startTime = previousHourStart.toInstant(java.time.ZoneOffset.UTC)
        val endTime = previousHourEnd.toInstant(java.time.ZoneOffset.UTC)

        return githubRepoTopicStatisticHourlyRepository.findAllByStatisticHourBetween(startTime, endTime)
    }

    /**
     * Generates hourly topic statistics from repository metadata
     */
    @Transactional
    fun generateHourlyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        val hourStart = targetTime.truncatedTo(java.time.temporal.ChronoUnit.HOURS)
        val hourEnd = hourStart.plusHours(1)
        val hourStartInstant = hourStart.toInstant(java.time.ZoneOffset.UTC)

        logger.info("Generating repo topic statistics for hour: $hourStart to $hourEnd")

        val repos = githubRepoMetadataRepository.findByUpdatedAtBetween(hourStart, hourEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateTopicStatistics(repos, hourStartInstant)
    }

    /**
     * Generates hourly topic statistics
     */
    private fun generateTopicStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticHour: java.time.Instant) {
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

                    val topics = objectMapper.readValue(topicsJson, object : com.fasterxml.jackson.core.type.TypeReference<List<String>>() {})
                    topics.forEach { topic ->
                        topicCounts[topic] = topicCounts.getOrDefault(topic, 0) + 1
                    }
                } catch (e: Exception) {
                    logger.warn("Failed to parse topics JSON for repo ${repo.repoId}. Topics value: '${repo.topics}', Error: ${e.message}")
                }
            }
        }

        logger.info("Found ${topicCounts.size} unique topics")

        val now = java.time.Instant.now()
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
}
