package com.rootbly.openpulse.usecase

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.entity.event.Actor
import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.entity.event.Payload
import com.rootbly.openpulse.entity.event.Repo
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.payload.ActorDto
import com.rootbly.openpulse.payload.GithubEventDto
import com.rootbly.openpulse.payload.GithubPayloadDto
import com.rootbly.openpulse.payload.RepoDto
import com.rootbly.openpulse.service.GithubEventService
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.time.Instant
import kotlin.test.assertEquals

class GithubMetadataCollectorUseCaseTest {

    private val githubEventClient = mockk<GithubEventClient>()
    private val githubEventService = mockk<GithubEventService>()
    private val githubEventChannel = mockk<GithubEventChannel>()

    private val useCase = GithubMetadataCollectorUseCase(
        githubEventClient,
        githubEventService,
        githubEventChannel
    )

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `should fetch events, save them, and publish metadata fetch events`() = runTest {
        // Given
        val eventDtos = listOf(
            createEventDto(id = 1L, repoName = "owner/repo1"),
            createEventDto(id = 2L, repoName = "owner/repo2")
        )

        val savedEntities = listOf(
            createEventEntity(id = 100L, eventId = 1L, repoName = "owner/repo1"),
            createEventEntity(id = 101L, eventId = 2L, repoName = "owner/repo2")
        )

        coEvery { githubEventClient.fetchEvents() } returns eventDtos
        every { githubEventService.save(eventDtos) } returns savedEntities
        coEvery { githubEventChannel.sendMetadataFetchEvent(any()) } just Runs

        // When
        useCase.collectGithubEventAndRepoMetadata()

        // Then
        coVerify(exactly = 1) { githubEventClient.fetchEvents() }
        verify(exactly = 1) { githubEventService.save(eventDtos) }
        coVerify(exactly = 1) {
            githubEventChannel.sendMetadataFetchEvent(
                GithubRepoMetadataFetchEvent("owner/repo1", 100L)
            )
        }
        coVerify(exactly = 1) {
            githubEventChannel.sendMetadataFetchEvent(
                GithubRepoMetadataFetchEvent("owner/repo2", 101L)
            )
        }
    }

    @Test
    fun `should handle empty events list`() = runTest {
        // Given
        coEvery { githubEventClient.fetchEvents() } returns emptyList()
        every { githubEventService.save(emptyList()) } returns emptyList()

        // When
        useCase.collectGithubEventAndRepoMetadata()

        // Then
        coVerify(exactly = 1) { githubEventClient.fetchEvents() }
        verify(exactly = 1) { githubEventService.save(emptyList()) }
        coVerify(exactly = 0) { githubEventChannel.sendMetadataFetchEvent(any()) }
    }

    @Test
    fun `should propagate exceptions from client`() = runTest {
        // Given
        val exception = RuntimeException("API failure")
        coEvery { githubEventClient.fetchEvents() } throws exception

        // When/Then
        try {
            useCase.collectGithubEventAndRepoMetadata()
            throw AssertionError("Expected exception to be thrown")
        } catch (e: RuntimeException) {
            assertEquals("API failure", e.message)
        }

        coVerify(exactly = 1) { githubEventClient.fetchEvents() }
        verify(exactly = 0) { githubEventService.save(any()) }
        coVerify(exactly = 0) { githubEventChannel.sendMetadataFetchEvent(any()) }
    }

    private fun createEventDto(
        id: Long,
        repoName: String,
        type: String = "PushEvent"
    ) = GithubEventDto(
        id = id,
        type = type,
        actor = ActorDto(1L, "user", "user", null, "url", "avatar"),
        repo = RepoDto(100L, repoName, "url"),
        payload = GithubPayloadDto(100L, 200L, "refs/heads/main", "abc123", "def456"),
        public = true,
        createdAt = Instant.now()
    )

    private fun createEventEntity(
        id: Long,
        eventId: Long,
        repoName: String
    ) = GithubEvent(
        id = id,
        eventId = eventId,
        type = "PushEvent",
        actor = Actor(1L, "user", "user", "url", "avatar"),
        repo = Repo(100L, repoName, "url"),
        payload = Payload(200L, "refs/heads/main", "abc123", "def456"),
        public = true,
        createdAt = Instant.now()
    )
}