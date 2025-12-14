package com.rootbly.openpulse.entity.event

import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import java.time.LocalDateTime

@Entity
class GithubEvent(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "event_id", unique = true)
    val eventId: Long,

    @Column(name = "type", nullable = false)
    val type: String,

    @Embedded
    val actor: Actor,

    @Embedded
    val repo: Repo,

    @Embedded
    val payload: Payload,

    @Column(name = "is_public", nullable = false)
    val public: Boolean,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other?.javaClass) return false

        other as GithubEvent

        return id != null && id == other.id
    }

    override fun hashCode(): Int {
        return 2025
    }

    override fun toString(): String {
        return "GithubEvent(" +
                    "id=$id, " +
                    "eventId=$eventId, " +
                    "type='$type', " +
                    "actor=$actor, " +
                    "repo=$repo, " +
                    "payload=$payload, " +
                    "public=$public, " +
                    "createdAt=$createdAt" +
                ")"
    }


}