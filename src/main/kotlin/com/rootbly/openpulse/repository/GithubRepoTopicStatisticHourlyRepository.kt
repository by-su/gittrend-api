package com.rootbly.openpulse.repository

import com.rootbly.openpulse.entity.GithubRepoTopicStatisticHourly
import org.springframework.data.jpa.repository.JpaRepository

interface GithubRepoTopicStatisticHourlyRepository : JpaRepository<GithubRepoTopicStatisticHourly, Long>