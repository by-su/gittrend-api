package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.statistic.language.GithubRepoLanguageStatisticDaily
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface GithubRepoLanguageStatisticDailyRepository : JpaRepository<GithubRepoLanguageStatisticDaily, Long> {
    fun findAllByStatisticDayBetween(startTime: Instant, endTime: Instant): List<GithubRepoLanguageStatisticDaily>
}