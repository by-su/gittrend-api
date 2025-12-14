package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.event.GithubEvent
import org.springframework.data.jpa.repository.JpaRepository

interface GithubEventRepository: JpaRepository<GithubEvent, Long> {

}