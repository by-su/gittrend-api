package com.rootbly.openpulse.controller

import com.rootbly.openpulse.common.constants.SortBy
import com.rootbly.openpulse.service.GithubRepoDocumentService
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId

@RestController
@RequestMapping("/repositories")
class RepoSearchController(
    private val githubRepoDocumentService: GithubRepoDocumentService
) {

    @GetMapping("/search")
    fun searchRepositories(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) language: String?,
        @RequestParam(defaultValue = "relevance") sortBy: String,
        @RequestParam(defaultValue = "desc") sortDirection: String,
        @RequestParam(defaultValue = "0") page: Int,
        @RequestParam(defaultValue = "10") size: Int
    ): RepoSearchResponse {
        val sortByEnum = SortBy.fromString(sortBy)
        val searchResult = githubRepoDocumentService.search(query, language, sortByEnum, sortDirection, page, size)

        return RepoSearchResponse(
            totalCount = searchResult.totalCount,
            items = searchResult.items.map { doc ->
                RepoSearchItem(
                    name = doc.name,
                    description = doc.description,
                    topics = doc.topics,
                    language = doc.language,
                    starCount = doc.starCount,
                    forkCount = doc.forkCount,
                    updatedAt = doc.updatedAt.atZone(ZoneId.systemDefault()).toInstant()
                )
            }
        )
    }
}

data class RepoSearchResponse(
    val totalCount: Long,
    val items: List<RepoSearchItem>
)

data class RepoSearchItem(

    val name: String,
    val description: String?,
    val topics: List<String>,
    val language: String?,
    val starCount: Int,
    val forkCount: Int,
    val updatedAt: Instant
)