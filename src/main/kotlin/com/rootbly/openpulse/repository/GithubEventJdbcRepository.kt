package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.event.GithubEvent
import org.springframework.jdbc.core.JdbcTemplate
import org.springframework.stereotype.Repository
import java.sql.Timestamp

@Repository
class GithubEventJdbcRepository(
    private val jdbcTemplate: JdbcTemplate
) {
    fun saveInBatch(entities: List<GithubEvent>) {
        val sql = """
        INSERT IGNORE INTO github_event (
            event_id,
            type, 
            actor_id, actor_login, actor_display_login, actor_url, actor_avatar_url,
            repo_id, repo_name, repo_url,
            push_id, git_ref, head_sha, before_sha,
            is_public, 
            created_at
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
    """.trimIndent()

        jdbcTemplate.batchUpdate(sql, entities, entities.size) { ps, event ->

            var idx = 1
            ps.setLong(idx++, event.eventId)
            ps.setString(idx++, event.type)
            ps.setLong(idx++, event.actor.actorId)
            ps.setString(idx++, event.actor.login)
            ps.setString(idx++, event.actor.displayLogin)
            ps.setString(idx++, event.actor.url)
            ps.setString(idx++, event.actor.avatarUrl)
            ps.setLong(idx++, event.repo.repoId)
            ps.setString(idx++, event.repo.name)
            ps.setString(idx++, event.repo.url)
            ps.setLong(idx++, event.payload.pushId)
            ps.setString(idx++, event.payload.ref)
            ps.setString(idx++, event.payload.head)
            ps.setString(idx++, event.payload.before)
            ps.setBoolean(idx++, event.public)
            ps.setTimestamp(idx++, Timestamp.from(event.createdAt))
        }
    }
}