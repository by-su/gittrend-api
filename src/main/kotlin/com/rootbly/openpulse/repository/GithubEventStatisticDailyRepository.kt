package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubEventStatisticDaily
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface GithubEventStatisticDailyRepository: JpaRepository<GithubEventStatisticDaily, Long> {

    fun findAllByCreatedAtBetween(startTime: Instant, endTime: Instant): List<GithubEventStatisticDaily>

    fun findAllByStatisticDayGreaterThanEqualAndStatisticDayLessThan(startTime: Instant, endTime: Instant): List<GithubEventStatisticDaily>
}