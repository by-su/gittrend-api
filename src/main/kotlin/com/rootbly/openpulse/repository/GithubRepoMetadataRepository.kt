package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoMetadata
import com.rootbly.openpulse.payload.GithubRepoResponse
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface GithubRepoMetadataRepository: JpaRepository<GithubRepoMetadata, Long> {

    /**
     * Find the most recently updated repositories
     */
    fun findTop30ByOrderByUpdatedAtDesc(): List<GithubRepoMetadata>

    @Modifying
    @Query(value = """
        INSERT INTO github_repo_metadata (
            repo_id, name, owner, description, fork,
            created_at, updated_at, pushed_at,
            star_count, watcher_count, fork_count, open_issue_count,
            language, license_key, license_name, topics, visibility,
            network_count, subscriber_count
        ) VALUES (
            :#{#response.id},
            :#{#response.name},
            :#{#response.owner.login},
            :#{#response.description != null ? #response.description : ''},
            :#{#response.fork},
            :#{T(java.time.LocalDateTime).ofInstant(#response.createdAt, T(java.time.ZoneId).systemDefault())},
            :#{T(java.time.LocalDateTime).ofInstant(#response.updatedAt, T(java.time.ZoneId).systemDefault())},
            :#{T(java.time.LocalDateTime).ofInstant(#response.pushedAt, T(java.time.ZoneId).systemDefault())},
            :#{#response.stargazersCount},
            :#{#response.watchersCount},
            :#{#response.forksCount},
            :#{#response.openIssuesCount},
            :#{#response.language},
            :#{#response.license?.key},
            :#{#response.license?.name},
            :topicsJson,
            :#{#response.visibility},
            :#{#response.networkCount},
            :#{#response.subscribersCount}
        )
        ON DUPLICATE KEY UPDATE
            name = VALUES(name),
            owner = VALUES(owner),
            description = VALUES(description),
            fork = VALUES(fork),
            created_at = VALUES(created_at),
            updated_at = VALUES(updated_at),
            pushed_at = VALUES(pushed_at),
            star_count = VALUES(star_count),
            watcher_count = VALUES(watcher_count),
            fork_count = VALUES(fork_count),
            open_issue_count = VALUES(open_issue_count),
            language = VALUES(language),
            license_key = VALUES(license_key),
            license_name = VALUES(license_name),
            topics = VALUES(topics),
            visibility = VALUES(visibility),
            network_count = VALUES(network_count),
            subscriber_count = VALUES(subscriber_count)
    """, nativeQuery = true)
    fun upsert(
        @Param("response") response: GithubRepoResponse,
        @Param("topicsJson") topicsJson: String?
    )

    /**
     * Find repositories created or updated within a time range
     */
    @Query("SELECT r FROM GithubRepoMetadata r WHERE r.updatedAt >= :startTime AND r.updatedAt < :endTime")
    fun findByUpdatedAtBetween(
        @Param("startTime") startTime: LocalDateTime,
        @Param("endTime") endTime: LocalDateTime
    ): List<GithubRepoMetadata>

    fun findAllByForkIsFalse(pageable: Pageable): Page<GithubRepoMetadata>

    @Query(value = "SELECT COUNT(*) FROM github_repo_metadata WHERE fork = false", nativeQuery = true)
    fun countByForkFalse(): Long
}