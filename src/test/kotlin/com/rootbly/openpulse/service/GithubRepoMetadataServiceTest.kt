package com.rootbly.openpulse.service

import com.rootbly.openpulse.fixture.GithubRepoResponseFixture
import com.rootbly.openpulse.payload.LicenseDto
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.Instant

@SpringBootTest
class GithubRepoMetadataServiceTest @Autowired constructor(
    private val service: GithubRepoMetadataService,
    private val repository: GithubRepoMetadataRepository
) {

    @BeforeEach
    fun clean() {
        repository.deleteAll()
    }

    @Test
    fun `save should insert new repository metadata when repoId does not exist`() {
        // Given
        val response = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "test-repo",
            owner = "test-owner",
            stargazersCount = 100
        )

        // When
        service.save(response)

        // Then
        val count = repository.count()
        assertEquals(1, count, "Should insert one record")

        val saved = repository.findAll().first()
        assertEquals(12345L, saved.repoId)
        assertEquals("test-repo", saved.name)
        assertEquals("test-owner", saved.owner)
        assertEquals(100, saved.starCount)
    }

    @Test
    fun `save should update existing repository metadata when repoId already exists`() {
        // Given - Insert initial data
        val initialResponse = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "test-repo",
            owner = "test-owner",
            stargazersCount = 100
        )
        service.save(initialResponse)

        val initialCount = repository.count()
        assertEquals(1, initialCount, "Initial insert should create one record")

        // When - Update with same repoId but different data
        val updatedResponse = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "updated-repo",
            owner = "updated-owner",
            stargazersCount = 200
        )
        service.save(updatedResponse)

        // Then - Should update, not insert new record
        val finalCount = repository.count()
        assertEquals(1, finalCount, "Should still have only one record (updated, not inserted)")

        val updated = repository.findAll().first()
        assertEquals(12345L, updated.repoId, "RepoId should remain the same")
        assertEquals("updated-repo", updated.name, "Name should be updated")
        assertEquals("updated-owner", updated.owner, "Owner should be updated")
        assertEquals(200, updated.starCount, "Star count should be updated")
    }

    @Test
    fun `save should update all fields correctly on upsert`() {
        // Given - Insert initial data
        val now = Instant.now()
        val initialResponse = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "test-repo",
            owner = "test-owner",
            description = "Initial description",
            fork = false,
            stargazersCount = 100,
            watchersCount = 50,
            forksCount = 10,
            openIssuesCount = 5,
            language = "Kotlin",
            license = LicenseDto(
                key = "mit",
                name = "MIT License",
                url = "https://opensource.org/licenses/MIT",
                spdxId = "MIT",
                nodeId = "node1"
            ),
            topics = listOf("kotlin", "spring"),
            visibility = "public",
            networkCount = 10,
            subscribersCount = 20,
            createdAt = now,
            updatedAt = now,
            pushedAt = now
        )
        service.save(initialResponse)

        // When - Update all fields
        val updatedNow = Instant.now().plusSeconds(3600)
        val updatedResponse = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "updated-repo",
            owner = "updated-owner",
            description = "Updated description",
            fork = true,
            stargazersCount = 200,
            watchersCount = 100,
            forksCount = 20,
            openIssuesCount = 10,
            language = "Java",
            license = LicenseDto(
                key = "apache-2.0",
                name = "Apache License 2.0",
                url = "https://opensource.org/licenses/Apache-2.0",
                spdxId = "Apache-2.0",
                nodeId = "node2"
            ),
            topics = listOf("java", "spring-boot", "microservices"),
            visibility = "private",
            networkCount = 30,
            subscribersCount = 40,
            createdAt = now,
            updatedAt = updatedNow,
            pushedAt = updatedNow
        )
        service.save(updatedResponse)

        // Then - Verify all fields are updated
        val count = repository.count()
        assertEquals(1, count, "Should still have only one record")

        val updated = repository.findAll().first()
        assertEquals(12345L, updated.repoId)
        assertEquals("updated-repo", updated.name)
        assertEquals("updated-owner", updated.owner)
        assertEquals("Updated description", updated.description)
        assertEquals(true, updated.fork)
        assertEquals(200, updated.starCount)
        assertEquals(100, updated.watcherCount)
        assertEquals(20, updated.forkCount)
        assertEquals(10, updated.openIssueCount)
        assertEquals("Java", updated.language)
        assertEquals("apache-2.0", updated.licenseKey)
        assertEquals("Apache License 2.0", updated.licenseName)
        assertNotNull(updated.topics)
        // Note: MySQL JSON column type auto-escapes the JSON string
        // So the value is stored as a JSON-encoded string
        assertEquals("\"[\\\"java\\\",\\\"spring-boot\\\",\\\"microservices\\\"]\"", updated.topics)
        assertEquals("private", updated.visibility)
        assertEquals(30, updated.networkCount)
        assertEquals(40, updated.subscriberCount)
    }

    @Test
    fun `save should handle null fields correctly`() {
        // Given
        val response = GithubRepoResponseFixture.create(
            id = 12345L,
            name = "test-repo",
            owner = "test-owner",
            description = null,
            language = null,
            license = null,
            topics = emptyList()
        )

        // When
        service.save(response)

        // Then
        val saved = repository.findAll().first()
        assertEquals("", saved.description, "Null description should be stored as empty string")
        assertNull(saved.language, "Language should be null")
        assertNull(saved.licenseKey, "License key should be null")
        assertNull(saved.licenseName, "License name should be null")
        assertNull(saved.topics, "Topics should be null when empty")
    }

    @Test
    fun `save should handle multiple different repos correctly`() {
        // Given
        val repo1 = GithubRepoResponseFixture.create(id = 1L, name = "repo1", owner = "owner1")
        val repo2 = GithubRepoResponseFixture.create(id = 2L, name = "repo2", owner = "owner2")
        val repo3 = GithubRepoResponseFixture.create(id = 3L, name = "repo3", owner = "owner3")

        // When
        service.save(repo1)
        service.save(repo2)
        service.save(repo3)

        // Then
        val count = repository.count()
        assertEquals(3, count, "Should have 3 different repos")

        val allRepos = repository.findAll()
        assertEquals(3, allRepos.size)
        assertEquals(setOf(1L, 2L, 3L), allRepos.map { it.repoId }.toSet())
    }

    @Test
    fun `save should update only the specific repo without affecting others`() {
        // Given - Insert 3 repos
        val repo1 = GithubRepoResponseFixture.create(id = 1L, name = "repo1", owner = "owner1", stargazersCount = 100)
        val repo2 = GithubRepoResponseFixture.create(id = 2L, name = "repo2", owner = "owner2", stargazersCount = 200)
        val repo3 = GithubRepoResponseFixture.create(id = 3L, name = "repo3", owner = "owner3", stargazersCount = 300)

        service.save(repo1)
        service.save(repo2)
        service.save(repo3)

        // When - Update only repo2
        val updatedRepo2 = GithubRepoResponseFixture.create(
            id = 2L,
            name = "updated-repo2",
            owner = "updated-owner2",
            stargazersCount = 999
        )
        service.save(updatedRepo2)

        // Then - Only repo2 should be updated
        val count = repository.count()
        assertEquals(3, count, "Should still have 3 repos")

        val allRepos = repository.findAll()
        val savedRepo1 = allRepos.first { it.repoId == 1L }
        val savedRepo2 = allRepos.first { it.repoId == 2L }
        val savedRepo3 = allRepos.first { it.repoId == 3L }

        // Repo1 unchanged
        assertEquals("repo1", savedRepo1.name)
        assertEquals("owner1", savedRepo1.owner)
        assertEquals(100, savedRepo1.starCount)

        // Repo2 updated
        assertEquals("updated-repo2", savedRepo2.name)
        assertEquals("updated-owner2", savedRepo2.owner)
        assertEquals(999, savedRepo2.starCount)

        // Repo3 unchanged
        assertEquals("repo3", savedRepo3.name)
        assertEquals("owner3", savedRepo3.owner)
        assertEquals(300, savedRepo3.starCount)
    }
}