package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubEventStatisticHourly
import org.springframework.data.jpa.repository.JpaRepository

interface GithubEventStatisticsHourlyRepository: JpaRepository<GithubEventStatisticHourly, Long> {
}