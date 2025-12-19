package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.service.GithubRepoMetadataStatisticDailyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/languages")
class GithubRepoLanguageStatisticDailyController(
    private val githubRepoMetadataStatisticDailyService: GithubRepoMetadataStatisticDailyService
) {

    @GetMapping("/statistic/daily")
    fun getGithubRepoLanguageStatisticDaily(): List<GithubRepoLanguageStatisticDaily> {
        return githubRepoMetadataStatisticDailyService.retrieveGithubRepoLanguageStatisticDaily()
    }
}