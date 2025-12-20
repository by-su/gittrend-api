package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.service.GithubRepoTopicStatisticHourlyService
import com.rootbly.openpulse.service.GithubRepoLanguageStatisticHourlyService
import com.rootbly.openpulse.service.GithubRepoTopicStatisticDailyService
import com.rootbly.openpulse.service.GithubRepoLanguageStatisticDailyService
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * GitHub repository metadata statistics generation scheduler
 */
@Component
class GithubRepoMetadataStatisticScheduler(
    private val githubRepoTopicStatisticHourlyService: GithubRepoTopicStatisticHourlyService,
    private val githubRepoLanguageStatisticHourlyService: GithubRepoLanguageStatisticHourlyService,
    private val githubRepoTopicStatisticDailyService: GithubRepoTopicStatisticDailyService,
    private val githubRepoLanguageStatisticDailyService: GithubRepoLanguageStatisticDailyService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * Generates hourly GitHub repository metadata statistics (topic and language)
     */
    @Scheduled(cron = "0 0 0/1 * * *")
    fun generateGithubRepoMetadataStatisticsHourly() {
        logger.info("Starting hourly GitHub repository metadata statistics generation")
        try {
            githubRepoTopicStatisticHourlyService.generateHourlyRepoTopicStatistic()
            githubRepoLanguageStatisticHourlyService.generateHourlyRepoLanguageStatistic()
            logger.info("Successfully completed hourly repo metadata statistics generation")
        } catch (e: Exception) {
            logger.error("Failed to generate hourly repo metadata statistics", e)
        }
    }

    /**
     * Generates daily GitHub repository metadata statistics (topic and language)
     */
    @Scheduled(cron = "0 0 0 * * *")
    fun generateGithubRepoMetadataStatisticsDaily() {
        logger.info("Starting daily GitHub repository metadata statistics generation")
        try {
            githubRepoTopicStatisticDailyService.generateDailyRepoTopicStatistic()
            githubRepoLanguageStatisticDailyService.generateDailyRepoLanguageStatistic()
            logger.info("Successfully completed daily repo metadata statistics generation")
        } catch (e: Exception) {
            logger.error("Failed to generate daily repo metadata statistics", e)
        }
    }
}