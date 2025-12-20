package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticHourly
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface GithubRepoTopicStatisticHourlyRepository : JpaRepository<GithubRepoTopicStatisticHourly, Long> {
    fun findAllByStatisticHourBetween(startTime: Instant, endTime: Instant): List<GithubRepoTopicStatisticHourly>
}