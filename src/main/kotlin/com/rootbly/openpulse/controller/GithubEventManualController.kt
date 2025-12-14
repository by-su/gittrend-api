package com.rootbly.openpulse.controller

import com.rootbly.openpulse.payload.GithubEventDto
import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.service.GithubEventService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GithubEventManualController(
    private val githubEventClient: GithubEventClient,
    private val githubEventService: GithubEventService
) {

    @GetMapping("/manual")
    suspend fun eventCollect() {
        val response = githubEventClient.fetchEvents()
        githubEventService.save(response)
    }
}