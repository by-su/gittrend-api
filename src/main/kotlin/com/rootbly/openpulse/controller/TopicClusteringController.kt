package com.rootbly.openpulse.controller

import com.rootbly.openpulse.service.TopicCategorizationService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/topics")
class TopicClusteringController(
    private val topicCategorizationService: TopicCategorizationService
) {

    @GetMapping("/clustering/hourly")
    suspend fun clusteringTopicsHourly(): List<Pair<String, Int>> {
        val result = topicCategorizationService.analyzeHourlyCategoryStats()
        return result.map { Pair(it.category, it.totalRepoCount) }.toList()
    }

    @GetMapping("/clustering/daily")
    suspend fun clusteringTopicsDaily(): List<Pair<String, Int>> {
        val result = topicCategorizationService.analyzeDailyCategoryStats()
        return result.map { Pair(it.category, it.totalRepoCount) }.toList()
    }
}