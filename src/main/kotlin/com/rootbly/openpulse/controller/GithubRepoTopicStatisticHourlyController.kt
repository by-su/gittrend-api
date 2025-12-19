package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticHourly
import com.rootbly.openpulse.service.GithubRepoMetadataStatisticHourlyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/topics")
class GithubRepoTopicStatisticHourlyController(
    private val githubRepoMetadataStatisticHourlyService: GithubRepoMetadataStatisticHourlyService
) {

    @GetMapping("/statistic/hourly")
    fun getGithubRepoTopicStatisticHourly(): List<GithubRepoTopicStatisticHourly> {
        return githubRepoMetadataStatisticHourlyService.retrieveGithubRepoTopicStatisticHourly()
    }
}
