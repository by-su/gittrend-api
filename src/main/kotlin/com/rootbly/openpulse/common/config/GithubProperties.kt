package com.rootbly.openpulse.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "github")
data class GithubProperties(
    val tokens: List<String> = emptyList()
)