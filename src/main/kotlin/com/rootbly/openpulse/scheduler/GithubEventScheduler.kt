package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.service.GithubEventService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class GithubEventScheduler(
    private val githubEventClient: GithubEventClient,
    private val githubEventService: GithubEventService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    @Scheduled(fixedDelay = 3000)
    fun fetchAndSaveGithubEvents() {
        scope.launch {
            try {
                val events = githubEventClient.fetchEvents()
                githubEventService.save(events)
            } catch (e: Exception) {
                logger.error("Failed to fetch and save Github events", e)
            }
        }
    }
}