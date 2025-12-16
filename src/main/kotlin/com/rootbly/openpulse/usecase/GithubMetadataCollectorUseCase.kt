package com.rootbly.openpulse.usecase

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.service.GithubEventService
import org.springframework.stereotype.Component

/**
 * GitHub event and repository metadata collection use case
 *
 * ## Flow
 * 1. Fetch latest events from GitHub Events API
 * 2. Save event data to database
 * 3. Publish metadata fetch events for each repository
 */
@Component
class GithubMetadataCollectorUseCase(
    private val githubEventClient: GithubEventClient,
    private val githubEventService: GithubEventService,
    private val githubEventChannel: GithubEventChannel,
) {

    /**
     * Collects GitHub events and triggers repository metadata collection
     *
     * Publishes metadata fetch events for async processing by event listeners.
     */
    suspend fun collectGithubEventAndRepoMetadata() {
        val response = githubEventClient.fetchEvents()
        val entities = githubEventService.save(response)

        entities.forEach { entity ->
            githubEventChannel.sendMetadataFetchEvent(
                GithubRepoMetadataFetchEvent(entity.repo.name, entity.id)
            )
        }
    }
}