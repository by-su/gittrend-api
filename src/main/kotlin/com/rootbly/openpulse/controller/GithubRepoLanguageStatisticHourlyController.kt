package com.rootbly.openpulse.controller

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
import com.rootbly.openpulse.service.GithubRepoMetadataStatisticHourlyService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/github/repos/languages")
class GithubRepoLanguageStatisticHourlyController(
    private val githubRepoMetadataStatisticHourlyService: GithubRepoMetadataStatisticHourlyService
) {

    @GetMapping("/statistic/hourly")
    fun getGithubRepoLanguageStatisticHourly(): List<GithubRepoLanguageStatisticHourly> {
        return githubRepoMetadataStatisticHourlyService.retrieveGithubRepoLanguageStatisticHourly()
    }
}