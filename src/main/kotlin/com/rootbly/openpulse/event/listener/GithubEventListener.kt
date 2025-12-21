package com.rootbly.openpulse.event.listener

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.service.GithubRepoMetadataService
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Component
import org.springframework.transaction.event.TransactionPhase
import org.springframework.transaction.event.TransactionalEventListener

@Component
class GithubEventListener(
    private val githubEventClient: GithubEventClient,
    private val githubRepoMetadataService: GithubRepoMetadataService
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @EventListener
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    suspend fun handleMetadataFetch(event: GithubRepoMetadataFetchEvent) {
        try {
            val response = githubEventClient.fetchRepoMetadata(event.repoName)
            githubRepoMetadataService.save(response)
        } catch (e: Exception) {
            logger.error("Failed to fetch metadata for repo: ${event.repoName}", e)
        }
    }
}