package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.common.constants.SortBy
import com.rootbly.openpulse.entity.GithubRepoMetadata
import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import com.rootbly.openpulse.fixture.GithubRepoMetadataFixture
import com.rootbly.openpulse.payload.GithubRepoSearchResult
import com.rootbly.openpulse.repository.GithubRepoDocumentRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime

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

        every { githubRepoMetadataRepository.countByForkFalse() } returns 2L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then
        assertEquals(2, count)
        verify(exactly = 1) { githubRepoMetadataRepository.countByForkFalse() }
        verify(exactly = 1) { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) }
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
        val pageContent: List<GithubRepoMetadata?> = listOf(metadata, null, metadata)
        val page = PageImpl(pageContent) as org.springframework.data.domain.Page<GithubRepoMetadata>

        every { githubRepoMetadataRepository.countByForkFalse() } returns 3L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should only process non-null items
        assertEquals(2, count)
        verify(exactly = 1) { githubRepoMetadataRepository.countByForkFalse() }
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

        every { githubRepoMetadataRepository.countByForkFalse() } returns 1L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
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

        every { githubRepoMetadataRepository.countByForkFalse() } returns 1L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
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

        every { githubRepoMetadataRepository.countByForkFalse() } returns 1L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should handle blank topics as empty list
        assertEquals(1, count)
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should search repositories with query parameter`() {
        // given
        val result1 = GithubRepoSearchResult(
            repoId = 1L,
            name = "spring-boot",
            description = "Spring Boot framework",
            topics = listOf("java", "spring"),
            language = "Java",
            starCount = 1000,
            watcherCount = 500,
            forkCount = 200,
            updatedAt = LocalDateTime.now(),
            score = 1.0f
        )

        val page = PageImpl(listOf(result1), Pageable.ofSize(10), 1)
        every { githubRepoDocumentRepository.search("spring", null, SortBy.STARS, "desc", 0, 10) } returns page

        // when
        val result = service.search(
            query = "spring",
            language = null,
            sortBy = SortBy.STARS,
            sortDirection = "desc",
            page = 0,
            size = 10
        )

        // then
        assertEquals(1, result.totalCount)
        assertEquals(1, result.items.size)
        assertEquals("spring-boot", result.items[0].name)
        verify(exactly = 1) { githubRepoDocumentRepository.search("spring", null, SortBy.STARS, "desc", 0, 10) }
    }

    @Test
    fun `should search repositories with language filter`() {
        // given
        val result1 = GithubRepoSearchResult(
            repoId = 1L,
            name = "kotlin-repo",
            description = "Kotlin repository",
            topics = listOf("kotlin"),
            language = "Kotlin",
            starCount = 500,
            watcherCount = 250,
            forkCount = 100,
            updatedAt = LocalDateTime.now(),
            score = 1.0f
        )

        val page = PageImpl(listOf(result1), Pageable.ofSize(10), 1)
        every { githubRepoDocumentRepository.search(null, "Kotlin", SortBy.STARS, "desc", 0, 10) } returns page

        // when
        val result = service.search(
            query = null,
            language = "Kotlin",
            sortBy = SortBy.STARS,
            sortDirection = "desc",
            page = 0,
            size = 10
        )

        // then
        assertEquals(1, result.totalCount)
        assertEquals(1, result.items.size)
        assertEquals("Kotlin", result.items[0].language)
        verify(exactly = 1) { githubRepoDocumentRepository.search(null, "Kotlin", SortBy.STARS, "desc", 0, 10) }
    }

    @Test
    fun `should search repositories sorted by forks`() {
        // given
        val result1 = GithubRepoSearchResult(
            repoId = 1L,
            name = "popular-repo",
            description = "Popular repository",
            topics = listOf("popular"),
            language = "Java",
            starCount = 500,
            watcherCount = 250,
            forkCount = 1000,
            updatedAt = LocalDateTime.now(),
            score = 1.0f
        )

        val page = PageImpl(listOf(result1), Pageable.ofSize(10), 1)
        every { githubRepoDocumentRepository.search(null, null, SortBy.FORKS, "desc", 0, 10) } returns page

        // when
        val result = service.search(
            query = null,
            language = null,
            sortBy = SortBy.FORKS,
            sortDirection = "desc",
            page = 0,
            size = 10
        )

        // then
        assertEquals(1, result.totalCount)
        assertEquals(1, result.items.size)
        verify(exactly = 1) { githubRepoDocumentRepository.search(null, null, SortBy.FORKS, "desc", 0, 10) }
    }

    @Test
    fun `should return empty result when no repositories match`() {
        // given
        val page = PageImpl<GithubRepoSearchResult>(emptyList(), Pageable.ofSize(10), 0)
        every { githubRepoDocumentRepository.search("nonexistent", null, SortBy.STARS, "desc", 0, 10) } returns page

        // when
        val result = service.search(
            query = "nonexistent",
            language = null,
            sortBy = SortBy.STARS,
            sortDirection = "desc",
            page = 0,
            size = 10
        )

        // then
        assertEquals(0, result.totalCount)
        assertEquals(0, result.items.size)
        verify(exactly = 1) { githubRepoDocumentRepository.search("nonexistent", null, SortBy.STARS, "desc", 0, 10) }
    }

    @Test
    fun `should sync only non-fork repositories`() {
        // given
        val nonForkRepo = GithubRepoMetadataFixture.create(
            repoId = 100L,
            name = "original-repo",
            fork = false,
            topics = """["kotlin","spring"]"""
        )

        // Only non-fork repos should be returned (fork repos are filtered out by the repository)
        val nonForkList = listOf(nonForkRepo)
        val page = PageImpl(nonForkList)

        every { githubRepoMetadataRepository.countByForkFalse() } returns 1L
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then
        assertEquals(1, count)
        verify(exactly = 1) { githubRepoMetadataRepository.countByForkFalse() }
        verify(exactly = 1) { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) }
        verify(exactly = 0) { githubRepoMetadataRepository.findAll(any<Pageable>()) }
        verify(exactly = 1) { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) }
    }

    @Test
    fun `should use correct count when database has both fork and non-fork repos`() {
        // given - Database has 10 repos total (7 non-fork, 3 fork)
        val nonForkRepos = (1..7).map {
            GithubRepoMetadataFixture.create(
                repoId = it.toLong() * 100,
                name = "non-fork-repo-$it",
                fork = false,
                topics = """["kotlin"]"""
            )
        }
        val page = PageImpl(nonForkRepos)

        every { githubRepoMetadataRepository.countByForkFalse() } returns 7L  // Only non-fork count
        every { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) } returns page
        every { githubRepoDocumentRepository.saveAll(any<List<GithubRepoDocument>>()) } returns emptyList()

        // when
        val count = service.syncAll()

        // then - Should sync 7 repos (only non-fork), not 10
        assertEquals(7, count)
        verify(exactly = 1) { githubRepoMetadataRepository.countByForkFalse() }
        verify(exactly = 1) { githubRepoMetadataRepository.findAllByForkIsFalse(any<Pageable>()) }
    }
}