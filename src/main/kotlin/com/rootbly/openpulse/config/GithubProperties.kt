package com.rootbly.openpulse.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "github")
data class GithubProperties(
    val tokens: List<String> = emptyList()
)