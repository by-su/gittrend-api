package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import com.rootbly.openpulse.service.GithubRepoMetadataStatisticDailyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/topics")
class GithubRepoTopicStatisticDailyController(
    private val githubRepoMetadataStatisticDailyService: GithubRepoMetadataStatisticDailyService
) {

    @GetMapping("/statistic/daily")
    fun getGithubRepoTopicStatisticDaily(): List<GithubRepoTopicStatisticDaily> {
        return githubRepoMetadataStatisticDailyService.retrieveGithubRepoTopicStatisticDaily()
    }
}