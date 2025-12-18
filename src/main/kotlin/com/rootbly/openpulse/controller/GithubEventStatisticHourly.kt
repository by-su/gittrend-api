package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import com.rootbly.openpulse.service.GithubEventStatisticHourlyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/events")
class GithubEventStatisticHourly(
    private val githubEventStatisticHourlyService: GithubEventStatisticHourlyService
) {

    @GetMapping("/statistic/hourly")
    fun getGithubEventHourlyStatistic(): List<GithubEventStatisticHourly> {
        return githubEventStatisticHourlyService.retrieveGithubEventStatisticHourly()
    }
}