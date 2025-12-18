package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticDaily
import org.springframework.data.jpa.repository.JpaRepository

interface GithubRepoTopicStatisticDailyRepository : JpaRepository<GithubRepoTopicStatisticDaily, Long>