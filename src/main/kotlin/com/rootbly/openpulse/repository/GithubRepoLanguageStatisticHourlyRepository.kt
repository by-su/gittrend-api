package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoLanguageStatisticHourly
import org.springframework.data.jpa.repository.JpaRepository
import java.time.Instant

interface GithubRepoLanguageStatisticHourlyRepository : JpaRepository<GithubRepoLanguageStatisticHourly, Long> {
    fun findAllByStatisticHourBetween(startTime: Instant, endTime: Instant): List<GithubRepoLanguageStatisticHourly>
}