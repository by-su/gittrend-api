package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.statistic.event.GithubEventStatisticDaily
import com.rootbly.openpulse.service.GithubEventStatisticDailyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/events")
class GithubEventStatisticDailyController(
    private val githubEventStatisticDailyService: GithubEventStatisticDailyService
) {

    @GetMapping("/statistic/daily")
    fun getGithubEventDailyStatistic(): List<GithubEventStatisticDaily> {
        return githubEventStatisticDailyService.retrieveDaily()
    }
}
