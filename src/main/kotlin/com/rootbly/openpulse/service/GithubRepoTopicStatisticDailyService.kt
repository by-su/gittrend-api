package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.common.util.JsonParsingUtil
import com.rootbly.openpulse.common.util.TimeRangeCalculator
import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticDaily
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
        val timeRange = TimeRangeCalculator.getPreviousDayRange()
        return githubRepoTopicStatisticDailyRepository.findAllByStatisticDayBetween(timeRange.start, timeRange.end)
    }

    /**
     * Generates daily topic statistics from repository metadata
     */
    @Transactional
    fun generateDailyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val (dayStart, dayEnd) = TimeRangeCalculator.getDayRange(targetTime)
        val dayStartInstant = TimeRangeCalculator.toInstant(dayStart)

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
                val topics = JsonParsingUtil.parseTopics(repo.topics!!, objectMapper)
                topics.forEach { topic ->
                    topicCounts[topic] = topicCounts.getOrDefault(topic, 0) + 1
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
