package com.rootbly.openpulse.scheduler

import com.rootbly.openpulse.usecase.GithubMetadataCollectorUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

/**
 * GitHub event periodic collection scheduler
 *
 * Collects event data and repository metadata.
 */
@Component
class GithubEventScheduler(
    private val githubMetadataCollectorUseCase: GithubMetadataCollectorUseCase
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)

    /**
     * Periodically fetches and saves GitHub events and repository metadata
     */
    @Scheduled(fixedRate = 30000)
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