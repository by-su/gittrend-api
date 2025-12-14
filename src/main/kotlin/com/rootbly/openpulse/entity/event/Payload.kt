package com.rootbly.openpulse.entity.event

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Payload(
    @Column(name = "push_id", nullable = false)
    val pushId: Long,

    @Column(name = "git_ref", nullable = false)
    val ref: String?,

    @Column(name = "head_sha")
    val head: String? = null,

    @Column(name = "before_sha")
    val before: String? = null
) {
    override fun toString(): String {
        return "Payload(pushId=$pushId, ref=$ref, head=$head, before=$before)"
    }
}