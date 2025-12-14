package com.rootbly.openpulse.event.dto

data class GithubRepoMetadataFetchEvent(
    val repoName: String,
    val entityId: Long? = null
)