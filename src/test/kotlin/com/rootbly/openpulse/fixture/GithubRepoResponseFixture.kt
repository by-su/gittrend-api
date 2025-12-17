package com.rootbly.openpulse.fixture

import com.rootbly.openpulse.payload.GithubRepoResponse
import com.rootbly.openpulse.payload.LicenseDto
import com.rootbly.openpulse.payload.OwnerDto
import java.time.Instant

object GithubRepoResponseFixture {

    /**
     * Creates a GithubRepoResponse with customizable fields for testing purposes
     */
    fun create(
        id: Long = 12345L,
        name: String = "test-repo",
        owner: String = "test-owner",
        ownerId: Long = id,
        ownerAvatarUrl: String = "https://avatars.githubusercontent.com/u/$ownerId",
        description: String? = "Test description",
        fork: Boolean = false,
        language: String? = "Kotlin",
        visibility: String = "public",
        createdAt: Instant = Instant.now(),
        updatedAt: Instant = Instant.now(),
        pushedAt: Instant = Instant.now(),
        stargazersCount: Int = 0,
        watchersCount: Int = 0,
        forksCount: Int = 0,
        openIssuesCount: Int = 0,
        license: LicenseDto? = null,
        topics: List<String> = emptyList(),
        networkCount: Int = 0,
        subscribersCount: Int = 0
    ): GithubRepoResponse {
        return GithubRepoResponse(
            id = id,
            name = name,
            owner = OwnerDto(
                login = owner,
                id = ownerId,
                avatarUrl = ownerAvatarUrl
            ),
            description = description,
            fork = fork,
            language = language,
            visibility = visibility,
            createdAt = createdAt,
            updatedAt = updatedAt,
            pushedAt = pushedAt,
            stargazersCount = stargazersCount,
            watchersCount = watchersCount,
            forksCount = forksCount,
            openIssuesCount = openIssuesCount,
            license = license,
            topics = topics,
            networkCount = networkCount,
            subscribersCount = subscribersCount
        )
    }

    /**
     * Creates a simple GithubRepoResponse with minimal configuration
     * Useful for tests that don't need customization
     */
    fun createSimple(
        id: Long = 12345L,
        name: String = "test-repo",
        owner: String = "test-owner"
    ): GithubRepoResponse {
        return create(
            id = id,
            name = name,
            owner = owner
        )
    }
}