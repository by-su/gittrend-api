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
        throw NotImplementedError("To be implemented")
    }

    /**
     * Generates daily topic statistics from repository metadata
     */
    @Transactional
    fun generateDailyRepoTopicStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        throw NotImplementedError("To be implemented")
    }
}
