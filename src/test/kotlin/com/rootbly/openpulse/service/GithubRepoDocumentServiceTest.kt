package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.repository.GithubRepoDocumentRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

class GithubRepoDocumentServiceTest {

    private val githubRepoDocumentRepository = mockk<GithubRepoDocumentRepository>()
    private val githubRepoMetadataRepository = mockk<GithubRepoMetadataRepository>()
    private val objectMapper = ObjectMapper()

    private val service = GithubRepoDocumentService(
        githubRepoDocumentRepository,
        githubRepoMetadataRepository,
        objectMapper
    )

    @Test
    fun `should sync all metadata to elasticsearch`() {
        // given
        val metadata1 = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo-1",
            owner = "owner1",
            description = "Test repository 1",
            starCount = 10,
            watcherCount = 5,
            forkCount = 2,
            openIssueCount = 1,
            language = "Kotlin",
            licenseKey = "MIT",
            licenseName = "MIT License",
            topics = """["kotlin","spring"]""",
            networkCount = 2,
            subscriberCount = 5
        )

        val metadata2 = GithubRepoMetadataFixture.create(
            repoId = 200L,
            name = "test-repo-2",
            owner = "owner2",
            description = "Test repository 2",
            starCount = 20,
            watcherCount = 10,
            forkCount = 4,
            openIssueCount = 2,
            language = "Java",
            licenseKey = "Apache-2.0",
            licenseName = "Apache License 2.0",
            topics = """["java","microservices"]""",
            networkCount = 4,
            subscriberCount = 10
        )

        val metadataList = listOf(metadata1, metadata2)

        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        service.syncAllFromMetadata(metadataList, objectMapper)

        // then
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should sync all metadata from database to elasticsearch with batching`() {
        // given
        val metadata1 = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo-1",
            topics = """["kotlin","spring"]"""
        )

        val metadata2 = GithubRepoMetadataFixture.create(
            repoId = 200L,
            name = "test-repo-2",
            topics = """["java","microservices"]"""
        )

        val metadataList = listOf(metadata1, metadata2)
        val page = PageImpl(metadataList)

        every { githubRepoMetadataRepository.count() } returns 2L
        every { githubRepoMetadataRepository.findAll(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then
        assertEquals(2, count)
        verify(exactly = 1) { githubRepoMetadataRepository.count() }
        verify(exactly = 1) { githubRepoMetadataRepository.findAll(any<Pageable>()) }
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should handle null metadata in page content`() {
        // given
        val metadata = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo",
            topics = """["kotlin","spring"]"""
        )

        // Page with null item
        val pageContent = listOf(metadata, null, metadata)
        val page = PageImpl(pageContent)

        every { githubRepoMetadataRepository.count() } returns 3L
        every { githubRepoMetadataRepository.findAll(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should only process non-null items
        assertEquals(2, count)
        verify(exactly = 1) { githubRepoMetadataRepository.count() }
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should handle invalid topics JSON format`() {
        // given
        val metadata = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo",
            topics = "invalid-json"  // Invalid JSON
        )

        val page = PageImpl(listOf(metadata))

        every { githubRepoMetadataRepository.count() } returns 1L
        every { githubRepoMetadataRepository.findAll(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should handle gracefully with empty topics
        assertEquals(1, count)
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should handle empty description`() {
        // given
        val metadata = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo",
            description = "",  // Empty description
            topics = """["kotlin"]"""
        )

        val page = PageImpl(listOf(metadata))

        every { githubRepoMetadataRepository.count() } returns 1L
        every { githubRepoMetadataRepository.findAll(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should convert empty description to null
        assertEquals(1, count)
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should handle blank topics JSON`() {
        // given
        val metadata = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "test-repo",
            topics = "   "  // Blank string
        )

        val page = PageImpl(listOf(metadata))

        every { githubRepoMetadataRepository.count() } returns 1L
        every { githubRepoMetadataRepository.findAll(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should handle blank topics as empty list
        assertEquals(1, count)
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }
}