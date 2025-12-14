package com.rootbly.openpulse.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.databind.ObjectMapper
import com.rootbly.openpulse.entity.GithubRepoMetadata
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId


data class GithubRepoResponse(

    val id: Long,
    val name: String,
    val owner: OwnerDto,
    val description: String?,
    val fork: Boolean,
    val language: String?,
    val visibility: String,

    @JsonProperty("created_at")
    val createdAt: Instant,
    @JsonProperty("updated_at")
    val updatedAt: Instant,
    @JsonProperty("pushed_at")
    val pushedAt: Instant,

    @JsonProperty("stargazers_count")
    val stargazersCount: Int,
    @JsonProperty("watchers_count")
    val watchersCount: Int,


    @JsonProperty("forks_count")
    val forksCount: Int,
    @JsonProperty("open_issues_count")
    val openIssuesCount: Int,

    val license: LicenseDto?,

    val topics: List<String> = emptyList(),


    @JsonProperty("network_count")
    val networkCount: Int,
    @JsonProperty("subscribers_count")
    val subscribersCount: Int
)

data class OwnerDto(
    val login: String,
    val id: Long,

    @JsonProperty("avatar_url")
    val avatarUrl: String
)

data class LicenseDto(
    val key: String,
    val name: String,
    val url: String?,

    @JsonProperty("spdx_id")
    val spdxId: String?,

    @JsonProperty("node_id")
    val nodeId: String?
)