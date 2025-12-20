package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.repository.GithubRepoLanguageStatisticDailyRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.slf4j.LoggerFactory
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
    fun retrieveGithubRepoLanguageStatisticDaily(): List<GithubRepoLanguageStatisticDaily> {
        throw NotImplementedError("To be implemented")
    }

    /**
     * Generates daily language statistics from repository metadata
     */
    @Transactional
    fun generateDailyRepoLanguageStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusDays(1)) {
        throw NotImplementedError("To be implemented")
    }
}
