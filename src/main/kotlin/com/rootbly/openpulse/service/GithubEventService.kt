package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.payload.GithubEventDto
import com.rootbly.openpulse.repository.GithubEventJdbcRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class GithubEventService(
    private val githubEventJdbcRepository: GithubEventJdbcRepository
) {

    @Transactional
    fun save(response: List<GithubEventDto>): List<GithubEvent> {
        val entities = response.map { it.toEntity() }
        githubEventJdbcRepository.saveInBatch(entities)
        return entities
    }
}