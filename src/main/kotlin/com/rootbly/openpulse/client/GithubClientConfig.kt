package com.rootbly.openpulse.client

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.WebClient

@Configuration
class GithubClientConfig(
    @Value("\${github.token:}")
    private val githubToken: String
) {

    @Bean
    fun githubWebClient(
        builder: WebClient.Builder
    ): WebClient {
        return builder
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultHeader(HttpHeaders.USER_AGENT, "openpulse-collector")
            .defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer $githubToken")
            .build()
    }
}