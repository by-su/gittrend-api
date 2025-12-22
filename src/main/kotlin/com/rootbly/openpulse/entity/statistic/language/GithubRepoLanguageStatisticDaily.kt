package com.rootbly.openpulse.entity.statistic.language

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.Instant

/**
 * Daily GitHub repository language statistics entity
 */
@Entity
@Table(
    name = "github_repo_language_statistic_daily",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["language", "statistic_day"])
    ],
    indexes = [
        Index(name = "idx_statistic_day", columnList = "statistic_day")
    ]
)
class GithubRepoLanguageStatisticDaily(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "language", nullable = false)
    val language: String,

    @Column(name = "repo_count", nullable = false)
    val repoCount: Int,

    @Column(name = "statistic_day", nullable = false)
    val statisticDay: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other != null && javaClass != other.javaClass) return false

        other as GithubRepoLanguageStatisticDaily

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return 2025
    }
}