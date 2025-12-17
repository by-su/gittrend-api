package com.rootbly.openpulse.fixture

import com.rootbly.openpulse.entity.GithubRepoMetadata
import java.time.LocalDateTime

object GithubRepoMetadataFixture {

    /**
     * Creates a GithubRepoMetadata with customizable fields for testing purposes
     */
    fun create(
        repoId: Long = 12345L,
        name: String = "test-repo",
        owner: String = "testowner",
        description: String = "Test repo $name",
        fork: Boolean = false,
        createdAt: LocalDateTime = LocalDateTime.now(),
        updatedAt: LocalDateTime = createdAt,
        pushedAt: LocalDateTime = createdAt,
        starCount: Int = 0,
        watcherCount: Int = 0,
        forkCount: Int = 0,
        openIssueCount: Int = 0,
        language: String? = "Kotlin",
        licenseKey: String? = null,
        licenseName: String? = null,
        topics: String? = null,
        visibility: String = "public",
        networkCount: Int = 0,
        subscriberCount: Int = 0
    ): GithubRepoMetadata {
        return GithubRepoMetadata(
            repoId = repoId,
            name = name,
            owner = owner,
            description = description,
            fork = fork,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pushedAt = pushedAt,
            starCount = starCount,
            watcherCount = watcherCount,
            forkCount = forkCount,
            openIssueCount = openIssueCount,
            language = language,
            licenseKey = licenseKey,
            licenseName = licenseName,
            topics = topics,
            visibility = visibility,
            networkCount = networkCount,
            subscriberCount = subscriberCount
        )
    }

    /**
     * Creates a simple GithubRepoMetadata with minimal configuration
     * Useful for tests that don't need customization
     */
    fun createSimple(
        repoId: Long = 12345L,
        name: String = "test-repo",
        createdAt: LocalDateTime = LocalDateTime.now(),
        topics: String? = null,
        language: String? = "Kotlin"
    ): GithubRepoMetadata {
        return create(
            repoId = repoId,
            name = name,
            createdAt = createdAt,
            topics = topics,
            language = language
        )
    }
}