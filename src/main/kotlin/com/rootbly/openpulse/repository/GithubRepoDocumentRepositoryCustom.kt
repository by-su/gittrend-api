package com.rootbly.openpulse.repository

import com.rootbly.openpulse.common.constants.SortBy
import com.rootbly.openpulse.payload.GithubRepoSearchResult
import org.springframework.data.domain.Page

interface GithubRepoDocumentRepositoryCustom {
    fun search(
        query: String?,
        language: String?,
        sortBy: SortBy,
        sortDirection: String,
        page: Int,
        size: Int
    ): Page<GithubRepoSearchResult>
}
