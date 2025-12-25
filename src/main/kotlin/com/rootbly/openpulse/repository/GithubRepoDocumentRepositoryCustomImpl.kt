package com.rootbly.openpulse.repository

import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery
import co.elastic.clients.elasticsearch._types.query_dsl.MultiMatchQuery
import co.elastic.clients.elasticsearch._types.query_dsl.Query
import co.elastic.clients.elasticsearch._types.query_dsl.TermQuery
import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import com.rootbly.openpulse.common.constants.SortBy
import com.rootbly.openpulse.payload.GithubRepoSearchResult
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.elasticsearch.client.elc.NativeQuery
import org.springframework.data.elasticsearch.core.ElasticsearchOperations
import org.springframework.data.elasticsearch.core.SearchHitSupport
import org.springframework.stereotype.Repository

@Repository
class GithubRepoDocumentRepositoryCustomImpl(
    private val elasticsearchOperations: ElasticsearchOperations
) : GithubRepoDocumentRepositoryCustom {

    override fun search(
        query: String?,
        language: String?,
        sortBy: SortBy,
        sortDirection: String,
        page: Int,
        size: Int
    ): Page<GithubRepoSearchResult> {
        val boolQueryBuilder = BoolQuery.Builder()

        if (!query.isNullOrBlank()) {
            val multiMatchQuery = MultiMatchQuery.Builder()
                .query(query)
                .fields(
                    "name", "description^2", "topics^3"
                )
                .build()
            boolQueryBuilder.must(Query.Builder().multiMatch(multiMatchQuery).build())
        }

        if (!language.isNullOrBlank()) {
            val termQuery = TermQuery.Builder()
                .field("language")
                .value(language)
                .build()
            boolQueryBuilder.filter(Query.Builder().term(termQuery).build())
        }

        // 정렬 기준 설정
        val sort = when (sortBy) {
            SortBy.STARS -> {
                val direction = if (sortDirection.lowercase() == "asc") Sort.Direction.ASC else Sort.Direction.DESC
                Sort.by(direction, "starCount")
            }
            SortBy.FORKS -> {
                val direction = if (sortDirection.lowercase() == "asc") Sort.Direction.ASC else Sort.Direction.DESC
                Sort.by(direction, "forkCount")
            }
            SortBy.RELEVANCE -> Sort.by(Sort.Order.desc("_score"))
        }

        val pageable = PageRequest.of(page, size, sort)

        val nativeQuery = NativeQuery.builder()
            .withQuery(Query.Builder().bool(boolQueryBuilder.build()).build())
            .withPageable(pageable)
            .withTrackTotalHits(true)
            .build()

        val searchHits = elasticsearchOperations.search(nativeQuery, GithubRepoDocument::class.java)
        val searchPage = SearchHitSupport.searchPageFor(searchHits, pageable)

        return PageImpl(
            searchPage.content.map { hit ->
                val doc = hit.content
                GithubRepoSearchResult(
                    repoId = doc.repoId,
                    name = doc.name,
                    description = doc.description,
                    topics = doc.topics,
                    language = doc.language,
                    starCount = doc.starCount,
                    watcherCount = doc.watcherCount,
                    forkCount = doc.forkCount,
                    updatedAt = doc.updatedAt,
                    score = hit.score
                )
            },
            pageable,
            searchPage.totalElements
        )
    }
}
