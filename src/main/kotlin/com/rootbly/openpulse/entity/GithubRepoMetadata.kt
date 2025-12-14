package com.rootbly.openpulse.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "github_repo_metadata")
class GithubRepoMetadata(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "repo_id", unique = true)
    val repoId: Long,

    val name: String,

    val owner: String,

    @Column(length = 500)
    val description: String,

    val fork: Boolean,

    @Column(name = "created_at")
    val createdAt: LocalDateTime,
    @Column(name = "updated_at")
    val updatedAt: LocalDateTime,
    @Column(name = "pushed_at")
    val pushedAt: LocalDateTime,

    @Column(name = "star_count")
    val starCount: Int,
    @Column(name = "watcher_count")
    val watcherCount: Int,
    @Column(name = "fork_count")
    val forkCount: Int,
    @Column(name = "open_issue_count")
    val openIssueCount: Int,

    val language: String?,

    @Column(name = "license_key")
    val licenseKey: String?,
    @Column(name = "license_name")
    val licenseName: String?,

    @Lob
    @Column(columnDefinition = "json")
    val topics: String?,

    val visibility: String,

    @Column(name = "network_count")
    val networkCount: Int,
    @Column(name = "subscriber_count")
    val subscriberCount: Int,
) {
}