package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticDailyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * GitHub repository topic daily statistics service
 *
 * Generates topic statistics from repository metadata daily.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoTopicStatisticDailyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoTopicStatisticDailyRepository: GithubRepoTopicStatisticDailyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Retrieves yesterday's daily topic statistics
     */
    fun retrieveGithubRepoTopicStatisticDaily(): List<GithubRepoTopicStatisticDaily> {
        val now = LocalDateTime.now()
        val todayStart = now.truncatedTo(java.time.temporal.ChronoUnit.DAYS)
        val yesterdayStart = todayStart.minusDays(1)
        val yesterdayEnd = todayStart

        val startTime = yesterdayStart.toInstant(java.time.ZoneOffset.UTC)
        val endTime = yesterdayEnd.toInstant(java.time.ZoneOffset.UTC)

        return githubRepoTopicStatisticDailyRepository.findAllByStatisticDayBetween(startTime, endTime)
    }

    /**
     * Generates daily topic statistics from repository metadata
     */
    @Transactional
    fun generateDailyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val dayStart = targetTime.truncatedTo(java.time.temporal.ChronoUnit.DAYS)
        val dayEnd = dayStart.plusDays(1)
        val dayStartInstant = dayStart.toInstant(java.time.ZoneOffset.UTC)

        logger.info("Generating repo topic statistics for day: $dayStart to $dayEnd")

        val repos = githubRepoMetadataRepository.findByUpdatedAtBetween(dayStart, dayEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateTopicStatistics(repos, dayStartInstant)
    }

    /**
     * Generates daily topic statistics
     */
    private fun generateTopicStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticDay: java.time.Instant) {
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
            GithubRepoTopicStatisticDaily(
                topic = topic,
                repoCount = count,
                statisticDay = statisticDay,
                createdAt = now,
                updatedAt = now
            )
        }

        githubRepoTopicStatisticDailyRepository.saveAll(entities)
        logger.info("Saved ${entities.size} topic statistics")
    }
}
