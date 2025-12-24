package com.rootbly.openpulse.service

import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import com.rootbly.openpulse.payload.GithubRepoResponse
import com.rootbly.openpulse.repository.GithubRepoDocumentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class GithubRepoDocumentService(
    private val githubRepoDocumentRepository: GithubRepoDocumentRepository,
) {

    @Transactional
    fun save(githubRepo: GithubRepoResponse): GithubRepoDocument {
        val entity = GithubRepoDocument(
            repoId = githubRepo.id,
            name = githubRepo.name,
            description = githubRepo.description,
            topics = githubRepo.topics,
            language = githubRepo.language,
            starCount = githubRepo.stargazersCount,
            watcherCount = githubRepo.watchersCount,
            forkCount = githubRepo.forksCount,
            updatedAt = LocalDateTime.ofInstant(githubRepo.updatedAt, ZoneId.systemDefault()),
        )
        return githubRepoDocumentRepository.save(entity)
    }



}