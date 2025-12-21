package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoMetadata
import com.rootbly.openpulse.payload.GithubRepoResponse
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * GitHub repository metadata management service
 */
@Service
@Transactional(readOnly = true)
class GithubRepoMetadataService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val objectMapper: ObjectMapper
) {

    /**
     * Saves or updates GitHub repository metadata
     *
     * Topics are serialized to JSON array. Uses upsert to keep latest information.
     */
    @Transactional
    fun save(response: GithubRepoResponse) {
        val topicsJson = if (response.topics.isNotEmpty()) {
            objectMapper.writeValueAsString(response.topics)
        } else {
            null
        }
        githubRepoMetadataRepository.upsert(response, topicsJson)
    }
}