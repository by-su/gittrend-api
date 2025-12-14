package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.event.Actor
import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.entity.event.Payload
import com.rootbly.openpulse.entity.event.Repo
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.LocalDateTime

@SpringBootTest
class GithubEventJdbcRepositoryTest @Autowired constructor(
    private val jdbcRepo: GithubEventJdbcRepository,
    private val jpaRepo: GithubEventRepository,
    private val jdbcTemplate: JdbcTemplate
) {

    @BeforeEach
    fun clean() {
        jpaRepo.deleteAll()
    }

    @Test
    fun saveInBatch_insertsAllRows() {
        val events = (1L..5L).map { buildEvent(it) }

        jdbcRepo.saveInBatch(events)

        val count = jpaRepo.count()
        assertEquals(5, count, "All batch rows should be inserted")
    }

    @Test
    fun saveInBatch_ignoresDuplicateEventId_and_insertsRest() {
        val initial = listOf(1L, 2L, 3L).map { buildEvent(it) }
        jdbcRepo.saveInBatch(initial)

        // Verify initial insert count
        assertEquals(3, jpaRepo.count(), "Initial unique rows should be 3")

        val withDuplicates = listOf(2L, 3L, 4L, 5L).map { buildEvent(it) }
        assertDoesNotThrow {
            jdbcRepo.saveInBatch(withDuplicates)
        }

        // Only new unique ids (4,5) should be inserted
        val totalCount = jpaRepo.count()
        assertEquals(5, totalCount, "After ignoring duplicates, total unique rows should be 5")

        // Ensure there are no duplicate event_id rows
        val duplicateCount = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM (
                SELECT event_id
                FROM github_event
                GROUP BY event_id
                HAVING COUNT(*) > 1
            ) t
            """.trimIndent(),
            Long::class.java
        )
        assertEquals(0L, duplicateCount, "There should be no duplicate event_id rows")
    }

    @Test
    fun saveInBatch_persistsAllFields_correctly() {
        val fixedCreated = LocalDateTime.of(2022, 1, 2, 3, 4, 5)

        val events = (1L..5L).map { buildEvent(it, createdAt = fixedCreated) }.toMutableList()

        val targetId = 3L
        events[(targetId - 1).toInt()] = GithubEvent(
            eventId = targetId,
            type = "PushEvent",
            actor = Actor(
                actorId = 10_000L + targetId,
                login = "user$targetId",
                displayLogin = "User$targetId",
                url = "https://api.github.com/users/user$targetId",
                avatarUrl = "https://avatars.githubusercontent.com/u/${10_000L + targetId}"
            ),
            repo = Repo(
                repoId = 20_000L + targetId,
                name = "repo$targetId",
                url = "https://api.github.com/repos/org/repo$targetId"
            ),
            payload = Payload(
                pushId = 30_000L + targetId,
                ref = "refs/heads/develop",
                head = "deadbeef$targetId",
                before = "beadfeed$targetId"
            ),
            public = true,
            createdAt = fixedCreated
        )

        jdbcRepo.saveInBatch(events)

        val saved = jpaRepo.findAll().first { it.eventId == targetId }

        assertEquals("PushEvent", saved.type)

        val actor = saved.actor
        assertEquals(10_000L + targetId, actor.actorId)
        assertEquals("user$targetId", actor.login)
        assertEquals("User$targetId", actor.displayLogin)
        assertEquals("https://api.github.com/users/user$targetId", actor.url)
        assertEquals("https://avatars.githubusercontent.com/u/${10_000L + targetId}", actor.avatarUrl)

        val repo = saved.repo
        assertEquals(20_000L + targetId, repo.repoId)
        assertEquals("repo$targetId", repo.name)
        assertEquals("https://api.github.com/repos/org/repo$targetId", repo.url)

        val payload = saved.payload
        assertEquals(30_000L + targetId, payload.pushId)
        assertEquals("refs/heads/develop", payload.ref)
        assertEquals("deadbeef$targetId", payload.head)
        assertEquals("beadfeed$targetId", payload.before)

        assertEquals(true, saved.public)
        assertEquals(fixedCreated, saved.createdAt)
    }

    private fun buildEvent(eventId: Long, createdAt: LocalDateTime = LocalDateTime.now()): GithubEvent =
        GithubEvent(
            eventId = eventId,
            type = "PushEvent",
            actor = Actor(
                actorId = 10_000L + eventId,
                login = "user$eventId",
                displayLogin = null,
                url = "https://api.github.com/users/user$eventId",
                avatarUrl = null
            ),
            repo = Repo(
                repoId = 20_000L + eventId,
                name = "repo$eventId",
                url = "https://api.github.com/repos/org/repo$eventId"
            ),
            payload = Payload(
                pushId = 30_000L + eventId,
                ref = "refs/heads/main",
                head = "deadbeef$eventId",
                before = "beadfeed$eventId"
            ),
            public = true,
            createdAt = createdAt
        )
}
