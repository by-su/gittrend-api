package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.service.GithubEventStatisticHourlyService
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GithubEventStatisticsScheduler(
    private val githubEventStatisticHourlyService: GithubEventStatisticHourlyService
) {

    @Scheduled(cron = "0 0 0/1 * * *")

    fun generateGithubEventStatisticsHourly() {
        githubEventStatisticHourlyService.generateHourlyEventStatistics()
    }
}