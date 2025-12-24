package com.rootbly.openpulse.event.processor

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.broadcast.GithubMetadataEventBroadcaster
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.payload.GithubMetadataStreamDto
import com.rootbly.openpulse.service.GithubRepoDocumentService
import com.rootbly.openpulse.service.GithubRepoMetadataService
import jakarta.annotation.PostConstruct
import jakarta.annotation.PreDestroy
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class GithubEventProcessor(
    private val githubEventChannel: GithubEventChannel,
    private val githubEventClient: GithubEventClient,
    private val githubRepoDocumentService: GithubRepoDocumentService,
    private val githubRepoMetadataService: GithubRepoMetadataService,
    private val metadataEventBroadcaster: GithubMetadataEventBroadcaster,

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
                githubRepoDocumentService.save(response)

                // Broadcast update to all connected clients
                val streamDto = GithubMetadataStreamDto.from(response)
                metadataEventBroadcaster.broadcast(streamDto)
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