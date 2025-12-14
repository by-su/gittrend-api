package com.rootbly.openpulse.usecase

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.service.GithubEventService
import org.springframework.stereotype.Component

@Component
class GithubMetadataCollectorUseCase(
    private val githubEventClient: GithubEventClient,
    private val githubEventService: GithubEventService,
    private val githubEventChannel: GithubEventChannel,
) {

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