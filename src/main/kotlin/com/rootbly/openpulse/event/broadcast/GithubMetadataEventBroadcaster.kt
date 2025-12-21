package com.rootbly.openpulse.event.broadcast

import com.rootbly.openpulse.payload.GithubMetadataStreamDto
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component

/**
 * Broadcasts GitHub repository metadata updates to all connected clients.
 *
 * Responsibilities (SRP):
 * - Maintains a shared event stream for metadata updates
 * - Broadcasts metadata to all active subscribers
 * - Handles broadcasting errors gracefully
 */
@Component
class GithubMetadataEventBroadcaster {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val _events = MutableSharedFlow<GithubMetadataStreamDto>(
        replay = 0,  // No replay for new subscribers
        extraBufferCapacity = 100  // Buffer up to 100 events if consumers are slow
    )

    /**
     * The event stream that clients can subscribe to.
     * Read-only view of the mutable shared flow.
     */
    val events: SharedFlow<GithubMetadataStreamDto> = _events.asSharedFlow()

    /**
     * Broadcasts metadata update to all active subscribers.
     *
     * @param metadata the repository metadata DTO to broadcast
     */
    suspend fun broadcast(metadata: GithubMetadataStreamDto) {
        try {
            _events.emit(metadata)
            logger.debug("Broadcasted metadata for repo: {}/{}", metadata.owner, metadata.name)
        } catch (e: Exception) {
            logger.error("Failed to broadcast metadata for repo: {}/{}", metadata.owner, metadata.name, e)
        }
    }

    /**
     * Returns the current number of active subscribers.
     */
    val subscriberCount: Int
        get() = _events.subscriptionCount.value
}