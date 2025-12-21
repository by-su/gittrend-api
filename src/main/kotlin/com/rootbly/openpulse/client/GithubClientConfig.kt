package com.rootbly.openpulse.client

import com.rootbly.openpulse.common.config.GithubProperties
import com.rootbly.openpulse.service.GithubTokenRotationService
import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpHeaders
import org.springframework.web.reactive.function.client.ClientRequest
import org.springframework.web.reactive.function.client.ExchangeFilterFunction
import org.springframework.web.reactive.function.client.WebClient

@Configuration
@EnableConfigurationProperties(GithubProperties::class)
class GithubClientConfig(
    private val githubTokenRotationService: GithubTokenRotationService
) {
    private val logger = LoggerFactory.getLogger(GithubClientConfig::class.java)

    @Bean
    fun githubWebClient(builder: WebClient.Builder): WebClient {
        return builder
            .baseUrl("https://api.github.com")
            .defaultHeader(HttpHeaders.ACCEPT, "application/vnd.github+json")
            .defaultHeader(HttpHeaders.USER_AGENT, "openpulse-collector")
            .filter(tokenRotationFilter())
            .build()
    }

    private fun tokenRotationFilter(): ExchangeFilterFunction {
        return ExchangeFilterFunction { request, next ->
            val token = githubTokenRotationService.getNextToken()
            logger.debug("Setting Authorization header with token: ${token.take(10)}...")
            val modifiedRequest = ClientRequest.from(request)
                .header(HttpHeaders.AUTHORIZATION, "Bearer $token")
                .build()
            logger.debug("Request URL: ${modifiedRequest.url()}, Headers: ${modifiedRequest.headers()}")
            next.exchange(modifiedRequest)
        }
    }
}