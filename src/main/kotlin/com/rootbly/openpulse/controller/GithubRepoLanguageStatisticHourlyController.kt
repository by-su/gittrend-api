package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.statistic.language.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.service.GithubRepoLanguageStatisticHourlyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/languages")
class GithubRepoLanguageStatisticHourlyController(
    private val githubRepoLanguageStatisticHourlyService: GithubRepoLanguageStatisticHourlyService
) {

    @GetMapping("/statistic/hourly")
    fun getGithubRepoLanguageStatisticHourly(): List<GithubRepoLanguageStatisticHourly> {
        return githubRepoLanguageStatisticHourlyService.retrieveGithubRepoLanguageStatisticHourly()
    }
}