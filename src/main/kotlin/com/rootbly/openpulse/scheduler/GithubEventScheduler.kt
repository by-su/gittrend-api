package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.service.GithubEventService
import com.rootbly.openpulse.service.GithubRepoMetadataService
import com.rootbly.openpulse.usecase.GithubMetadataCollectorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GithubEventScheduler(
    private val githubMetadataCollectorUseCase: GithubMetadataCollectorUseCase
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    @Scheduled(fixedDelay = 3000)
    fun fetchAndSaveGithubEvents() {
        scope.launch {
            try {
                githubMetadataCollectorUseCase.collectGithubEventAndRepoMetadata()
            } catch (e: Exception) {
                logger.error("Failed to fetch and save Github events", e)
            }
        }
    }
}