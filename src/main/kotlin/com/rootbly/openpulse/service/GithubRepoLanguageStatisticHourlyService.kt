package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
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
        throw NotImplementedError("To be implemented")
    }

    /**
     * Generates hourly language statistics from repository metadata
     */
    @Transactional
    fun generateHourlyRepoLanguageStatistic(targetTime: LocalDateTime = LocalDateTime.now().minusHours(1)) {
        throw NotImplementedError("To be implemented")
    }
}
