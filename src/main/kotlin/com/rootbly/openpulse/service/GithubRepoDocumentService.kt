package com.rootbly.openpulse.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoMetadata
import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import com.rootbly.openpulse.payload.GithubRepoResponse
import com.rootbly.openpulse.repository.GithubRepoDocumentRepository
import com.rootbly.openpulse.repository.GithubRepoMetadataRepository
import org.slf4j.LoggerFactory
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime
import java.time.ZoneId

@Service
@Transactional(readOnly = true)
class GithubRepoDocumentService(
    private val githubRepoDocumentRepository: GithubRepoDocumentRepository,
    private val githubRepoMetadataRepository: GithubRepoMetadataRepository,
    private val objectMapper: ObjectMapper,
) {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    companion object {
        private const val BATCH_SIZE = 1000
    }

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

    @Transactional
    fun syncAllFromMetadata(metadataList: List<GithubRepoMetadata>, objectMapper: ObjectMapper) {
        val documents = metadataList.map { metadata ->
            GithubRepoDocument.from(metadata, objectMapper)
        }
        githubRepoDocumentRepository.saveAll(documents)
    }

    fun syncAll(): Int {
        logger.info("Starting sync of all metadata to Elasticsearch")

        val totalCount = githubRepoMetadataRepository.count()
        logger.info("Total metadata count: $totalCount")

        var processedCount = 0
        var pageNumber = 0

        while (true) {
            val pageable = PageRequest.of(pageNumber, BATCH_SIZE)
            val page = githubRepoMetadataRepository.findAll(pageable)

            if (page.isEmpty) {
                break
            }

            val documents = page.content
                .filterNotNull()
                .mapNotNull { metadata ->
                    try {
                        GithubRepoDocument.from(metadata, objectMapper)
                    } catch (e: Exception) {
                        logger.error("Failed to convert metadata to document for repo ${metadata.repoId}: ${e.message}", e)
                        null
                    }
                }

            if (documents.isNotEmpty()) {
                githubRepoDocumentRepository.saveAll(documents)
            }
            processedCount += documents.size

            logger.info("Processed $processedCount / $totalCount documents")

            if (!page.hasNext()) {
                break
            }

            pageNumber++
        }

        logger.info("Sync completed. Total documents synced: $processedCount")
        return processedCount
    }

}