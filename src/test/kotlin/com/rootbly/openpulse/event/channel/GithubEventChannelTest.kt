package com.rootbly.openpulse.event.channel

import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GithubEventChannelTest {

    @Test
    fun `should send and receive events`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val event = GithubRepoMetadataFetchEvent("owner/repo", 123L)

        // When
        channel.sendMetadataFetchEvent(event)
        val received = channel.metadataFetchChannel.receive()

        // Then
        assertEquals("owner/repo", received.repoName)
        assertEquals(123L, received.entityId)
    }

    @Test
    fun `should handle multiple events in order`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val events = listOf(
            GithubRepoMetadataFetchEvent("repo1", 1L),
            GithubRepoMetadataFetchEvent("repo2", 2L),
            GithubRepoMetadataFetchEvent("repo3", 3L)
        )

        // When
        events.forEach { channel.sendMetadataFetchEvent(it) }

        // Then
        repeat(3) { index ->
            val received = channel.metadataFetchChannel.receive()
            assertEquals("repo${index + 1}", received.repoName)
            assertEquals((index + 1).toLong(), received.entityId)
        }
    }

    @Test
    fun `should drop oldest events when buffer is full`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val capacity = 90

        // When - Fill the channel beyond capacity
        repeat(capacity + 10) { index ->
            channel.sendMetadataFetchEvent(
                GithubRepoMetadataFetchEvent("repo$index", index.toLong())
            )
        }

        // Then - First 10 events should be dropped (DROP_OLDEST)
        // Remaining events should be from index 10 to 99
        val receivedEvents = mutableListOf<GithubRepoMetadataFetchEvent>()
        repeat(capacity) {
            receivedEvents.add(channel.metadataFetchChannel.receive())
        }

        // First received event should be from index 10 (oldest 10 dropped)
        assertEquals("repo10", receivedEvents.first().repoName)
        assertEquals(10L, receivedEvents.first().entityId)

        // Last received event should be from index 99
        assertEquals("repo99", receivedEvents.last().repoName)
        assertEquals(99L, receivedEvents.last().entityId)
    }

    @Test
    fun `should allow concurrent sends and receives`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val eventCount = 50
        val receivedEvents = mutableListOf<GithubRepoMetadataFetchEvent>()

        // When - Send and receive concurrently
        val sendJob = launch {
            repeat(eventCount) { index ->
                channel.sendMetadataFetchEvent(
                    GithubRepoMetadataFetchEvent("repo$index", index.toLong())
                )
            }
        }

        val receiveJob = launch {
            repeat(eventCount) {
                receivedEvents.add(channel.metadataFetchChannel.receive())
            }
        }

        sendJob.join()
        receiveJob.join()

        // Then
        assertEquals(eventCount, receivedEvents.size)
        assertEquals("repo0", receivedEvents.first().repoName)
        assertEquals("repo49", receivedEvents.last().repoName)
    }
}