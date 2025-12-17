package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubEventStatisticDaily
import org.springframework.data.jpa.repository.JpaRepository

interface GithubEventStatisticDailyRepository: JpaRepository<GithubEventStatisticDaily, Long> {
}