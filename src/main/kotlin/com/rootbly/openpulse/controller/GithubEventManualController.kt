package com.rootbly.openpulse.controller

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.service.GithubEventService
import com.rootbly.openpulse.usecase.GithubMetadataCollectorUseCase
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GithubEventManualController(
    private val githubMetadataCollectorUseCase: GithubMetadataCollectorUseCase

) {

    @GetMapping("/manual")
    suspend fun eventCollect() {
        githubMetadataCollectorUseCase.collectGithubEventAndRepoMetadata()
    }
}