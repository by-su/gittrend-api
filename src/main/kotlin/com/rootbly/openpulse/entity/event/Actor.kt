package com.rootbly.openpulse.entity.event

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Actor(
    @Column(name = "actor_id", nullable = false)
    val actorId: Long,

    @Column(name = "actor_login", nullable = false)
    val login: String,

    @Column(name = "actor_display_login")
    val displayLogin: String? = null,

    @Column(name = "actor_url", nullable = false)
    val url: String,

    @Column(name = "actor_avatar_url")
    val avatarUrl: String? = null
) {
    override fun toString(): String {
        return "Actor(actorId=$actorId, login='$login', displayLogin=$displayLogin, url='$url', avatarUrl=$avatarUrl)"
    }
}
