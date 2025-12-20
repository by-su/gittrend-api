package com.rootbly.openpulse.controller

import com.rootbly.openpulse.service.GithubRepoTopicStatisticHourlyService
import com.rootbly.openpulse.service.TopicCategorizationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/topics")
class TopicClusteringController(
    private val githubRepoTopicStatisticHourlyService: GithubRepoTopicStatisticHourlyService,
    private val topicCategorizationService: TopicCategorizationService
) {

    @GetMapping("/clustering")
    suspend fun normalize(): List<Pair<String, Int>> {
        val list = githubRepoTopicStatisticHourlyService.retrieveGithubRepoTopicStatisticHourly()
        val result =  topicCategorizationService.analyzeCategoryStats(list)
        return result.map { Pair(it.category, it.totalRepoCount) }.toList()
    }
}