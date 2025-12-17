package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.service.GithubRepoMetadataStatisticHourlyService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * GitHub repository metadata statistics generation scheduler
 */
@Component
class GithubRepoMetadataStatisticsScheduler(
    private val githubRepoMetadataStatisticHourlyService: GithubRepoMetadataStatisticHourlyService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates hourly GitHub repository metadata statistics (topic and language)
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    fun generateGithubRepoMetadataStatisticsHourly() {
        logger.info("Starting hourly GitHub repository metadata statistics generation")
        try {
            githubRepoMetadataStatisticHourlyService.generateHourlyRepoMetadataStatistics()
            logger.info("Successfully completed hourly repo metadata statistics generation")
        } catch (e: Exception) {
            logger.error("Failed to generate hourly repo metadata statistics", e)
        }
    }
}