package com.rootbly.openpulse.controller

import com.rootbly.openpulse.event.broadcast.GithubMetadataEventBroadcaster
import com.rootbly.openpulse.payload.GithubMetadataStreamDto
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import org.slf4j.LoggerFactory
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerSentEvent
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for streaming GitHub repository metadata updates via SSE.
 */
@RestController
@RequestMapping("/api/github/metadata")
class GithubMetadataStreamController(
    private val metadataEventBroadcaster: GithubMetadataEventBroadcaster,
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository
) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    /**
     * SSE endpoint for streaming real-time GitHub repository metadata updates.
     *
     * Sends initial data first (most recent 30 repositories) to prevent
     * empty screen, then streams live updates as they arrive.
     *
     * @return Flow of Server-Sent Events containing metadata updates
     */
    @GetMapping("/stream", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun streamMetadataUpdates(): Flow<ServerSentEvent<GithubMetadataStreamDto>> {
        logger.info("New client connected to metadata stream. Current subscribers: {}",
            metadataEventBroadcaster.subscriberCount)

        return metadataEventBroadcaster.events
            .onStart {
                val initialData = githubRepoMetadataRepository.findTop30ByOrderByUpdatedAtDesc()
                logger.info("Sending {} initial repositories to new client", initialData.size)

                initialData.forEach { entity ->
                    GithubMetadataStreamDto.from(entity)?.let { dto ->
                        emit(dto)
                    }
                }
            }
            .map { metadata ->
                ServerSentEvent.builder(metadata)
                    .event("metadata-update")
                    .id(metadata.repoId.toString())
                    .build()
            }
    }
}