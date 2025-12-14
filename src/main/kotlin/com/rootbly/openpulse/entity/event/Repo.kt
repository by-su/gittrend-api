package com.rootbly.openpulse.entity.event

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class Repo(

    @Column(name = "repo_id", nullable = false)
    val repoId: Long,

    @Column(name = "repo_name", nullable = false)
    val name: String,

    @Column(name = "repo_url", nullable = false)
    val url: String
) {
    override fun toString(): String {
        return "Repo(repoId=$repoId, name='$name', url='$url')"
    }
}