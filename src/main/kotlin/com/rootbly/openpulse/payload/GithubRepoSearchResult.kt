package com.rootbly.openpulse.payload

import java.time.LocalDateTime

data class GithubRepoSearchResult(
    val repoId: Long,
    val name: String,
    val description: String?,
    val topics: List<String>,
    val language: String?,
    val starCount: Int,
    val watcherCount: Int,
    val forkCount: Int,
    val updatedAt: LocalDateTime,
    val score: Float
)