package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.elasticsearch.GithubRepoDocument
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository

interface GithubRepoDocumentRepository :
    ElasticsearchRepository<GithubRepoDocument, String>,
    GithubRepoDocumentRepositoryCustom {
}