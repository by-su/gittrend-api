package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.service.GithubRepoTopicStatisticHourlyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/topics")
class GithubRepoTopicStatisticHourlyController(
    private val githubRepoTopicStatisticHourlyService: GithubRepoTopicStatisticHourlyService
) {

    @GetMapping("/statistic/hourly")
    fun getGithubRepoTopicStatisticHourly(): List<GithubRepoTopicStatisticHourly> {
        return githubRepoTopicStatisticHourlyService.retrieveGithubRepoTopicStatisticHourly()
    }
}
