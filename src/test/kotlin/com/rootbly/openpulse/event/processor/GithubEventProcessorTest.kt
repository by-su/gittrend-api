package com.rootbly.openpulse.event.processor

import com.rootbly.openpulse.client.GithubEventClient
import com.rootbly.openpulse.event.broadcast.GithubMetadataEventBroadcaster
import com.rootbly.openpulse.event.channel.GithubEventChannel
import com.rootbly.openpulse.event.dto.GithubRepoMetadataFetchEvent
import com.rootbly.openpulse.fixture.GithubRepoResponseFixture
import com.rootbly.openpulse.service.GithubRepoDocumentService
import com.rootbly.openpulse.service.GithubRepoMetadataService
import io.mockk.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import java.util.concurrent.atomic.AtomicInteger
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class GithubEventProcessorTest {

    private val githubEventChannel = mockk<GithubEventChannel>()
    private val githubEventClient = mockk<GithubEventClient>()
    private val githubRepoDocumentService = mockk<GithubRepoDocumentService>()
    private val githubRepoMetadataService = mockk<GithubRepoMetadataService>()
    private val metadataEventBroadcaster = mockk<GithubMetadataEventBroadcaster>()

    @AfterEach
    fun cleanup() {
        clearAllMocks()
    }

    @Test
    fun `should process metadata events successfully`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val processedRepos = mutableListOf<String>()

        coEvery { githubEventClient.fetchRepoMetadata(any()) } answers {
            val repoName = firstArg<String>()
            processedRepos.add(repoName)
            GithubRepoResponseFixture.create(name = repoName)
        }
        every { githubRepoMetadataService.save(any()) } just Runs
        every { githubRepoDocumentService.save(any()) } returns mockk()
        coEvery { metadataEventBroadcaster.broadcast(any()) } just Runs

        val processor = GithubEventProcessor(
            channel,
            githubEventClient,
            githubRepoDocumentService,
            githubRepoMetadataService,
            metadataEventBroadcaster,
            concurrentWorkers = 2
        )

        // When
        processor.startProcessing()

        // Send test events
        channel.sendMetadataFetchEvent(GithubRepoMetadataFetchEvent("owner/repo1", 1L))
        channel.sendMetadataFetchEvent(GithubRepoMetadataFetchEvent("owner/repo2", 2L))

        // Wait until all events are processed (with timeout)
        var attempts = 0
        while (processedRepos.size < 2 && attempts < 100) {
            delay(50)
            attempts++
        }

        // Then
        processor.cleanup()
        assertEquals(2, processedRepos.size)
        assertTrue(processedRepos.contains("owner/repo1"))
        assertTrue(processedRepos.contains("owner/repo2"))
    }

    @Test
    fun `should handle exceptions without stopping other workers`() = runTest {
        // Given
        val channel = GithubEventChannel()
        val successCount = AtomicInteger(0)
        val failureCount = AtomicInteger(0)

        coEvery { githubEventClient.fetchRepoMetadata(any()) } coAnswers {
            val repoName = firstArg<String>()
            if (repoName.contains("fail")) {
                failureCount.incrementAndGet()
                throw RuntimeException("Simulated failure")
            } else {
                successCount.incrementAndGet()
                GithubRepoResponseFixture.create(name = repoName)
            }
        }
        every { githubRepoMetadataService.save(any()) } just Runs
        every { githubRepoDocumentService.save(any()) } returns mockk()
        coEvery { metadataEventBroadcaster.broadcast(any()) } just Runs

        val processor = GithubEventProcessor(
            channel,
            githubEventClient,
            githubRepoDocumentService,
            githubRepoMetadataService,
            metadataEventBroadcaster,
            concurrentWorkers = 2
        )

        // When
        processor.startProcessing()

        // Send mix of successful and failing events
        channel.sendMetadataFetchEvent(GithubRepoMetadataFetchEvent("owner/repo1", 1L))
        channel.sendMetadataFetchEvent(GithubRepoMetadataFetchEvent("owner/fail1", 2L))
        channel.sendMetadataFetchEvent(GithubRepoMetadataFetchEvent("owner/repo2", 3L))

        // Wait until all events are processed (with timeout)
        var attempts = 0
        while ((successCount.get() + failureCount.get()) < 3 && attempts < 100) {
            delay(50)
            attempts++
        }

        // Then
        processor.cleanup()
        assertEquals(2, successCount.get(), "Should have 2 successful processes")
        assertEquals(1, failureCount.get(), "Should have 1 failed process")
    }

    @Test
    fun `should cleanup properly on PreDestroy`() = runTest {
        // Given
        val channel = GithubEventChannel()
        coEvery { githubEventClient.fetchRepoMetadata(any()) } returns GithubRepoResponseFixture.createSimple(name = "test")
        every { githubRepoMetadataService.save(any()) } just Runs
        every { githubRepoDocumentService.save(any()) } returns mockk()
        coEvery { metadataEventBroadcaster.broadcast(any()) } just Runs

        val processor = GithubEventProcessor(
            channel,
            githubEventClient,
            githubRepoDocumentService,
            githubRepoMetadataService,
            metadataEventBroadcaster,
            concurrentWorkers = 2
        )

        // When
        processor.startProcessing()
        delay(100)
        processor.cleanup()

        // Then - Should not throw exception
        // Cleanup is successful if no exception is thrown
    }
}