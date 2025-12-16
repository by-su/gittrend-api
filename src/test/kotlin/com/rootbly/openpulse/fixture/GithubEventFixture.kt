package com.rootbly.openpulse.fixture

import com.rootbly.openpulse.entity.event.Actor
import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.entity.event.Payload
import com.rootbly.openpulse.entity.event.Repo
import java.time.Instant

object GithubEventFixture {

    /**
     * Creates a GithubEvent with customizable fields for testing purposes
     */
    fun create(
        eventId: Long,
        type: String = "PushEvent",
        createdAt: Instant = Instant.now(),
        actorId: Long = 10_000L + eventId,
        actorLogin: String = "user$eventId",
        actorDisplayLogin: String? = null,
        actorUrl: String = "https://api.github.com/users/$actorLogin",
        actorAvatarUrl: String? = null,
        repoId: Long = 20_000L + eventId,
        repoName: String = "repo$eventId",
        repoUrl: String = "https://api.github.com/repos/org/$repoName",
        pushId: Long = 30_000L + eventId,
        ref: String = "refs/heads/main",
        head: String? = "deadbeef$eventId",
        before: String? = "beadfeed$eventId",
        isPublic: Boolean = true
    ): GithubEvent {
        return GithubEvent(
            eventId = eventId,
            type = type,
            actor = Actor(
                actorId = actorId,
                login = actorLogin,
                displayLogin = actorDisplayLogin,
                url = actorUrl,
                avatarUrl = actorAvatarUrl
            ),
            repo = Repo(
                repoId = repoId,
                name = repoName,
                url = repoUrl
            ),
            payload = Payload(
                pushId = pushId,
                ref = ref,
                head = head,
                before = before
            ),
            public = isPublic,
            createdAt = createdAt
        )
    }

    /**
     * Creates a simple GithubEvent with minimal configuration
     * Useful for tests that don't need customization
     */
    fun createSimple(
        eventId: Long,
        type: String = "PushEvent",
        createdAt: Instant = Instant.now()
    ): GithubEvent {
        return create(
            eventId = eventId,
            type = type,
            createdAt = createdAt
        )
    }
}