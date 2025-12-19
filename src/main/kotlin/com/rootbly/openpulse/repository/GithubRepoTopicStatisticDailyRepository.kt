package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface GithubRepoTopicStatisticDailyRepository : JpaRepository<GithubRepoTopicStatisticDaily, Long> {
    fun findAllByStatisticDayBetween(startTime: Instant, endTime: Instant): List<GithubRepoTopicStatisticDaily>
}