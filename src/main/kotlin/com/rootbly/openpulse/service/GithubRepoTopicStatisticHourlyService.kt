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
        throw NotImplementedError("To be implemented")
    }

    /**
     * Generates hourly topic statistics from repository metadata
     */
    @Transactional
    fun generateHourlyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        throw NotImplementedError("To be implemented")
    }
}
