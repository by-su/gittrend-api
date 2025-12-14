package com.rootbly.openpulse.payload

import com.fasterxml.jackson.annotation.JsonProperty
import com.rootbly.openpulse.entity.event.Actor
import com.rootbly.openpulse.entity.event.GithubEvent
import com.rootbly.openpulse.entity.event.Payload
import com.rootbly.openpulse.entity.event.Repo
import java.time.LocalDateTime
import kotlin.Long

data class GithubEventDto(
    val id: Long,
    val type: String,
    val actor: ActorDto,
    val repo: RepoDto,
    val payload: GithubPayloadDto,
    val public: Boolean,
    @JsonProperty("created_at")
    val createdAt: LocalDateTime?
) {
    fun toEntity(): GithubEvent {
        return GithubEvent(
            eventId = id,
            type = type,
            actor = actor.toEntity(),
            repo = repo.toEntity(),
            payload = payload.toEntity(),
            public = public,
            createdAt = createdAt ?: LocalDateTime.now()
        )
    }
}

data class ActorDto(
    val id: Long,
    val login: String,
    @JsonProperty("display_login")
    val displayLogin: String?,
    @JsonProperty("gravatar_id")
    val gravatarId: String?,
    val url: String,
    @JsonProperty("avatar_url")
    val avatarUrl: String?
) {
    fun toEntity(): Actor {
        return Actor(
            actorId = id,
            login = login,
            displayLogin = displayLogin,
            url = url,
            avatarUrl = avatarUrl
        )
    }
}

data class RepoDto(
    val id: Long,
    val name: String,
    val url: String
) {
    fun toEntity(): Repo {
        return Repo(
            repoId = id,
            name = name,
            url = url
        )
    }
}

data class GithubPayloadDto(
    @JsonProperty("repository_id")
    val repositoryId: Long,
    @JsonProperty("push_id")
    val pushId: Long,
    val ref: String?,
    val head: String?,
    val before: String?
) {
    fun toEntity(): Payload {
        return Payload(
            pushId = pushId,
            ref = ref,
            head = head,
            before = before
        )
    }
}
