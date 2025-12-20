package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticDaily
import com.rootbly.openpulse.service.GithubRepoLanguageStatisticDailyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/languages")
class GithubRepoLanguageStatisticDailyController(
    private val githubRepoLanguageStatisticDailyService: GithubRepoLanguageStatisticDailyService
) {

    @GetMapping("/statistic/daily")
    fun getGithubRepoLanguageStatisticDaily(): List<GithubRepoLanguageStatisticDaily> {
        return githubRepoLanguageStatisticDailyService.retrieveGithubRepoLanguageStatisticDaily()
    }
}