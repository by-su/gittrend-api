package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.common.util.JsonParsingUtil
import com.rootbly.openpulse.common.util.TimeRangeCalculator
import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticHourly
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
        val timeRange = TimeRangeCalculator.getPreviousHourRange()
        return githubRepoTopicStatisticHourlyRepository.findAllByStatisticHourBetween(timeRange.start, timeRange.end)
    }

    /**
     * Generates hourly topic statistics from repository metadata
     */
    @Transactional
    fun generateHourlyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        val (hourStart, hourEnd) = TimeRangeCalculator.getHourRange(targetTime)
        val hourStartInstant = TimeRangeCalculator.toInstant(hourStart)

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
                val topics = JsonParsingUtil.parseTopics(repo.topics!!, objectMapper)
                topics.forEach { topic ->
                    topicCounts[topic] = topicCounts.getOrDefault(topic, 0) + 1
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
