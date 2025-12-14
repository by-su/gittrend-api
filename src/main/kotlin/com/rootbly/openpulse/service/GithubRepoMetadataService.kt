package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.payload.GithubRepoResponse
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GithubRepoMetadataService(
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val objectMapper: ObjectMapper
) {

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