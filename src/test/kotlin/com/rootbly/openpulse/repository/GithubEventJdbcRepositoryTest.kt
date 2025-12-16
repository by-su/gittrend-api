package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.fixture.GithubEventFixture
import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.jdbc.core.JdbcTemplate
import java.time.Instant

@SpringBootTest
class GithubEventJdbcRepositoryTest @Autowired constructor(
    private val jdbcRepo: GithubEventJdbcRepository,
    private val jpaRepo: GithubEventRepository,
    private val jdbcTemplate: JdbcTemplate
) {

    companion object {
        private const val ACTOR_ID_OFFSET = 10_000L
        private const val REPO_ID_OFFSET = 20_000L
        private const val PUSH_ID_OFFSET = 30_000L
        private const val INITIAL_BATCH_SIZE = 3L
        private const val TOTAL_BATCH_SIZE = 5L
        private const val TARGET_EVENT_ID = 3L
        private const val EXPECTED_DEFAULT_TYPE = "PushEvent"
        private const val DEVELOP_BRANCH = "refs/heads/develop"
    }

    @BeforeEach
    fun clean() {
        jpaRepo.deleteAll()
    }

    @Test
    fun `saveInBatch should insert all rows successfully`() {
        // Given
        val events = (1L..TOTAL_BATCH_SIZE).map { GithubEventFixture.create(it) }

        // When
        jdbcRepo.saveInBatch(events)

        // Then
        val count = jpaRepo.count()
        assertEquals(TOTAL_BATCH_SIZE, count, "All batch rows should be inserted")
    }

    @Test
    fun `saveInBatch should ignore duplicate event IDs and insert only new events`() {
        // Given
        val initial = (1L..INITIAL_BATCH_SIZE).map { GithubEventFixture.create(it) }
        jdbcRepo.saveInBatch(initial)
        assertEquals(INITIAL_BATCH_SIZE, jpaRepo.count(), "Initial unique rows should be inserted")

        // When
        val withDuplicates = (2L..TOTAL_BATCH_SIZE).map { GithubEventFixture.create(it) }
        assertDoesNotThrow {
            jdbcRepo.saveInBatch(withDuplicates)
        }

        // Then
        val totalCount = jpaRepo.count()
        assertEquals(TOTAL_BATCH_SIZE, totalCount, "After ignoring duplicates, total unique rows should be 5")

        val duplicateCount = countDuplicateEventIds()
        assertEquals(0L, duplicateCount, "There should be no duplicate event_id rows")
    }

    @Test
    fun `saveInBatch should persist all fields correctly`() {
        // Given
        val fixedCreatedAt = Instant.parse("2022-01-02T03:04:05Z")
        val events = (1L..TOTAL_BATCH_SIZE).map {
            GithubEventFixture.create(it, createdAt = fixedCreatedAt)
        }.toMutableList()

        events[(TARGET_EVENT_ID - 1).toInt()] = GithubEventFixture.create(
            eventId = TARGET_EVENT_ID,
            actorDisplayLogin = "User$TARGET_EVENT_ID",
            actorAvatarUrl = "https://avatars.githubusercontent.com/u/${ACTOR_ID_OFFSET + TARGET_EVENT_ID}",
            ref = DEVELOP_BRANCH,
            createdAt = fixedCreatedAt
        )

        // When
        jdbcRepo.saveInBatch(events)

        // Then
        val saved = jpaRepo.findAll().first { it.eventId == TARGET_EVENT_ID }

        assertEventType(saved, EXPECTED_DEFAULT_TYPE)
        assertActorFields(saved, TARGET_EVENT_ID)
        assertRepoFields(saved, TARGET_EVENT_ID)
        assertPayloadFields(saved, TARGET_EVENT_ID, DEVELOP_BRANCH)
        assertMetadataFields(saved, isPublic = true, createdAt = fixedCreatedAt)
    }

    private fun countDuplicateEventIds(): Long {
        return jdbcTemplate.queryForObject(
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
        ) ?: 0L
    }

    private fun assertEventType(event: GithubEvent, expectedType: String) {
        assertEquals(expectedType, event.type, "Event type should match")
    }

    private fun assertActorFields(event: GithubEvent, eventId: Long) {
        val actor = event.actor
        assertEquals(ACTOR_ID_OFFSET + eventId, actor.actorId, "Actor ID should match")
        assertEquals("user$eventId", actor.login, "Actor login should match")
        assertEquals("User$eventId", actor.displayLogin, "Actor display login should match")
        assertEquals("https://api.github.com/users/user$eventId", actor.url, "Actor URL should match")
        assertEquals("https://avatars.githubusercontent.com/u/${ACTOR_ID_OFFSET + eventId}", actor.avatarUrl, "Actor avatar URL should match")
    }

    private fun assertRepoFields(event: GithubEvent, eventId: Long) {
        val repo = event.repo
        assertEquals(REPO_ID_OFFSET + eventId, repo.repoId, "Repository ID should match")
        assertEquals("repo$eventId", repo.name, "Repository name should match")
        assertEquals("https://api.github.com/repos/org/repo$eventId", repo.url, "Repository URL should match")
    }

    private fun assertPayloadFields(event: GithubEvent, eventId: Long, expectedRef: String) {
        val payload = event.payload
        assertEquals(PUSH_ID_OFFSET + eventId, payload.pushId, "Push ID should match")
        assertEquals(expectedRef, payload.ref, "Git reference should match")
        assertEquals("deadbeef$eventId", payload.head, "Head SHA should match")
        assertEquals("beadfeed$eventId", payload.before, "Before SHA should match")
    }

    private fun assertMetadataFields(event: GithubEvent, isPublic: Boolean, createdAt: Instant) {
        assertEquals(isPublic, event.public, "Public flag should match")
        assertEquals(createdAt, event.createdAt, "Created timestamp should match")
    }

}
