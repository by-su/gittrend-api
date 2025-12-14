package com.rootbly.openpulse.service

import com.rootbly.openpulse.payload.GithubEventDto
import com.rootbly.openpulse.repository.GithubEventJdbcRepository
import com.rootbly.openpulse.repository.GithubEventRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GithubEventService(
    private val githubEventRepository: GithubEventRepository,
    private val githubEventJdbcRepository: GithubEventJdbcRepository
) {

    @Transactional
    fun save(response: List<GithubEventDto>) {
        val entities = response.map { it.toEntity() }
        githubEventJdbcRepository.saveInBatch(entities)
    }
}