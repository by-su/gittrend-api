package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.service.GithubEventStatisticHourlyService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * GitHub event statistics generation scheduler
 */
@Component
class GithubEventStatisticsScheduler(
    private val githubEventStatisticHourlyService: GithubEventStatisticHourlyService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates hourly GitHub event statistics
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    fun generateGithubEventStatisticsHourly() {
        logger.info("Starting hourly GitHub event statistics generation")
        try {
            githubEventStatisticHourlyService.generateHourlyEventStatistics()
            logger.info("Successfully completed hourly statistics generation")
        } catch (e: Exception) {
            logger.error("Failed to generate hourly statistics", e)
        }
    }
}