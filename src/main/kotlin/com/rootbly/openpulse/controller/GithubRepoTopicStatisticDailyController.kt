package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.statistic.topic.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.service.GithubRepoTopicStatisticDailyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/topics")
class GithubRepoTopicStatisticDailyController(
    private val githubRepoTopicStatisticDailyService: GithubRepoTopicStatisticDailyService
) {

    @GetMapping("/statistic/daily")
    fun getGithubRepoTopicStatisticDaily(): List<GithubRepoTopicStatisticDaily> {
        return githubRepoTopicStatisticDailyService.retrieveGithubRepoTopicStatisticDaily()
    }
}