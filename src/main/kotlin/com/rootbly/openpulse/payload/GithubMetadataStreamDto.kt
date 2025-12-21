package com.rootbly.openpulse.payload

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.rootbly.openpulse.entity.GithubRepoMetadata
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * DTO for streaming GitHub repository metadata updates to clients.
 *
 * Separates API response from client stream contract (SRP).
 * Prevents exposing internal structure to clients.
 */
data class GithubMetadataStreamDto(
    val repoId: Long,
    val name: String,
    val owner: String,
    val description: String,
    val fork: Boolean,
    val starCount: Int,
    val watcherCount: Int,
    val forkCount: Int,
    val openIssueCount: Int,
    val language: String?,
    val topics: List<String>,
    val visibility: String,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val pushedAt: LocalDateTime
) {
    companion object {
        private val objectMapper = jacksonObjectMapper()

        /**
         * Converts GitHub API response to streaming DTO.
         * Factory method pattern for object creation.
         */
        fun from(response: GithubRepoResponse): GithubMetadataStreamDto {
            return GithubMetadataStreamDto(
                repoId = response.id,
                name = response.name,
                owner = response.owner.login,
                description = response.description ?: "",
                fork = response.fork,
                starCount = response.stargazersCount,
                watcherCount = response.watchersCount,
                forkCount = response.forksCount,
                openIssueCount = response.openIssuesCount,
                language = response.language,
                topics = response.topics,
                visibility = response.visibility,
                createdAt = LocalDateTime.ofInstant(response.createdAt, ZoneId.systemDefault()),
                updatedAt = LocalDateTime.ofInstant(response.updatedAt, ZoneId.systemDefault()),
                pushedAt = LocalDateTime.ofInstant(response.pushedAt, ZoneId.systemDefault())
            )
        }

        /**
         * Converts entity to streaming DTO.
         * Used for sending initial data when client connects.
         *
         * Handles potential null values gracefully with safe defaults.
         */
        fun from(entity: GithubRepoMetadata?): GithubMetadataStreamDto? {
            if (entity == null) return null

            return try {
                GithubMetadataStreamDto(
                    repoId = entity.repoId,
                    name = entity.name,
                    owner = entity.owner,
                    description = entity.description,
                    fork = entity.fork,
                    starCount = entity.starCount,
                    watcherCount = entity.watcherCount,
                    forkCount = entity.forkCount,
                    openIssueCount = entity.openIssueCount,
                    language = entity.language,
                    topics = entity.topics?.let { parseTopics(it) } ?: emptyList(),
                    visibility = entity.visibility,
                    createdAt = entity.createdAt,
                    updatedAt = entity.updatedAt,
                    pushedAt = entity.pushedAt
                )
            } catch (e: NullPointerException) {
                // Log and skip entities with invalid data
                null
            }
        }

        private fun parseTopics(topicsJson: String): List<String> {
            return try {
                objectMapper.readValue(topicsJson, List::class.java) as List<String>
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}