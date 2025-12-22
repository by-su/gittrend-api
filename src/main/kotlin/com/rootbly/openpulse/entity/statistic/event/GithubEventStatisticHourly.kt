package com.rootbly.openpulse.entity.statistic.event

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.Table
import java.time.Instant

/**
 * Hourly GitHub event statistics snapshot entity
 */
@Entity
@Table(
    indexes = [
        Index(name = "idx_statistic_hour", columnList = "statistic_hour")
    ]
)
class GithubEventStatisticHourly(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "event_type", nullable = false)
    val eventType: String,

    @Column(name = "event_count", nullable = false)
    val eventCount: Int,

    @Column(name = "statistic_hour", nullable = false)
    val statisticHour: Instant,

    @Column(name = "created_at", nullable = false)
    val createdAt: Instant,

    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant,
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other != null && javaClass != other.javaClass) return false

        other as GithubEventStatisticHourly

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return 2025
    }
}