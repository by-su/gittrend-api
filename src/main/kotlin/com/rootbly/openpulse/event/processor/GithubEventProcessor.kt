package com.rootbly.openpulse.event.processor

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.service.GithubRepoMetadataService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GithubEventProcessor(
    private val githubEventChannel: GithubEventChannel,
    private val githubEventClient: GithubEventClient,
    private val githubRepoMetadataService: GithubRepoMetadataService,
    @Value("\${github.metadata.concurrent-workers:20}")
    private val concurrentWorkers: Int
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    @PostConstruct
    fun startProcessing() {
        repeat(concurrentWorkers) { workerId ->
            scope.launch {
                processMetadataEvents(workerId)
            }
        }
    }

    private suspend fun processMetadataEvents(workerId: Int) {
        for (event in githubEventChannel.metadataFetchChannel) {
            try {
                val response = githubEventClient.fetchRepoMetadata(event.repoName)
                githubRepoMetadataService.save(response)
            } catch (e: Exception) {
                logger.error("Worker $workerId: Failed to process event for ${event.repoName}", e)
            }
        }
    }

    @PreDestroy
    fun cleanup() {
        logger.info("Shutting down {} workers", concurrentWorkers)
        scope.cancel()
        logger.info("All workers terminated")
    }

}