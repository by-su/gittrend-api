package com.rootbly.openpulse.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.entity.GithubRepoMetadata
import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import com.rootbly.openpulse.repository.GithubRepoTopicStatisticDailyRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit

/**
 * GitHub repository metadata daily statistics generation service
 *
 * Generates topic and language statistics from repository metadata daily.
 */
@Service
@Transactional(readOnly = true)
class GithubRepoMetadataStatisticDailyService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val githubRepoTopicStatisticDailyRepository: GithubRepoTopicStatisticDailyRepository,
    private val githubRepoLanguageStatisticDailyRepository: GithubRepoLanguageStatisticDailyRepository,
    private val objectMapper: ObjectMapper
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates daily topic and language statistics from repository metadata
     *
     * @param targetTime Target time (defaults to previous day)
     */
    @Transactional
    fun generateDailyRepoMetadataStatistics(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        val dayStart = targetTime.truncatedTo(ChronoUnit.DAYS)
        val dayEnd = dayStart.plusDays(1)
        val dayStartInstant = dayStart.toInstant(ZoneOffset.UTC)

        logger.info("Generating repo metadata statistics for day: $dayStart to $dayEnd")

        val repos = githubRepoMetadataRepository.findByUpdatedAtBetween(dayStart, dayEnd)
        logger.info("Found ${repos.size} repositories created in the time range")

        if (repos.isEmpty()) {
            logger.info("No repositories found in the time range, skipping statistics generation")
            return
        }

        generateTopicStatistics(repos, dayStartInstant)
        generateLanguageStatistics(repos, dayStartInstant)
    }

    /**
     * Generates daily topic statistics
     */
    private fun generateTopicStatistics(repos: List<GithubRepoMetadata>, statisticDay: Instant) {
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

    /**
     * Generates daily language statistics
     */
    private fun generateLanguageStatistics(repos: List<com.rootbly.openpulse.entity.GithubRepoMetadata>, statisticDay: Instant) {
        val languageCounts = mutableMapOf<String, Int>()

        repos.forEach { repo ->
            repo.language?.takeIf { it.isNotEmpty() }?.let { language ->
                languageCounts[language] = languageCounts.getOrDefault(language, 0) + 1
            }
        }

        logger.info("Found ${languageCounts.size} unique languages")

        val now = Instant.now()
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