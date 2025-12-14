package com.rootbly.openpulse.event.channel

import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import org.springframework.stereotype.Component

@Component
class GithubEventChannel {

    private val _metadataFetchChannel = Channel<GithubRepoMetadataFetchEvent>(
        capacity = 90,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val metadataFetchChannel: ReceiveChannel<GithubRepoMetadataFetchEvent> = _metadataFetchChannel

    suspend fun sendMetadataFetchEvent(event: GithubRepoMetadataFetchEvent) {
        _metadataFetchChannel.send(event)
    }
}